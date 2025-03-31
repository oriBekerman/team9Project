package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import il.cshaifasweng.OCSFMediatorExample.server.SimpleServer;
//import il.cshaifasweng.OCSFMediatorExample.server.repositories.CustomerRepository;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ResInfoRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.RETURN_ACTIVE_RESERVATIONS;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

public class ResInfoController {

    private static ResInfoRepository resInfoRepository;


    // Handle requests based on type
    public Response handleRequest(Request request) {
        System.out.println("Handling request: " + request.getRequestType());
        return switch (request.getRequestType())
        {
            case ADD_RESERVATION -> handleNewReservation(request);
            case CANCEL_RESERVATION-> cancelReservation(request);
            case GET_RES_REPORT -> getAllActiveReservationList();
            case GET_ACTIVE_RESERVATIONS -> getAllActiveReservationList();
            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
        };
    }
    //constructor
    public ResInfoController() {this.resInfoRepository = new ResInfoRepository();};

    public boolean checkIfEmpty()
    {
        return (resInfoRepository.checkIfEmpty());
    }

    //initialize menuItem table with base items
    public void PopulateResSInfo(List<ResInfo>resSInfo) {
        System.out.println("[ResInfoController] Populating " + resSInfo.size() + " reservations.");
        resInfoRepository.populate(resSInfo);
    }

    // Method to fetch all reservations and wrap in a response
    public Response<List<ResInfo>> getAllActiveReservationList() {
        List<ResInfo> reservations =new ArrayList<>();
        try {
            reservations= resInfoRepository.getAllActiveReservations();
            return new Response<>(RETURN_ACTIVE_RESERVATIONS, reservations,
                    "Reservations loaded successfully", SUCCESS, THIS_CLIENT);
        } catch (Exception e) {
            return new Response<>(RETURN_ACTIVE_RESERVATIONS, new ArrayList<>(),
                    "Failed to load reservations: " + e.getMessage(),
                    ERROR, THIS_CLIENT);
        }
    }

    public List <ResInfo> getAllResInfo()
    {
        return resInfoRepository.getAllResSInfo();
    }

    public Response<List<ResInfo>> getBranchMonthlyReservationsReport(int branchId, String monthYear) {
        try {
            List<ResInfo> reservations = resInfoRepository.getReservationsByBranchAndMonth(branchId, monthYear);
            return new Response<>(RETURN_RES_REPORT, reservations, "Monthly reservations for branch", SUCCESS, THIS_CLIENT);
        } catch (Exception e) {
            return new Response<>(RETURN_RES_REPORT, "Failed to fetch monthly reservations: " + e.getMessage(), ERROR, THIS_CLIENT);
        }
    }


    public Response addReservation(ResInfo reservation) {
        System.out.println("in add reservation cnt");
        Response response = new Response(ADDED_RESERVATION, null, null, BOTH);
        Response response1 = new Response(ADDED_RESERVATION, null, null, THIS_CLIENT);
        Response response2 = new Response(UPDATE_BRANCH_TABLES, null, null, ALL_CLIENTS);
        boolean customerInDB=false;

        if (reservation.getBranch() == null) {
            return new Response(ADDED_RESERVATION, null, "Reservation must include a branch.", ERROR, THIS_CLIENT);
        }

        Branch branch = reservation.getBranch();
        Customer customer = reservation.getCustomer();
        Set<RestTable> tables = reservation.getTable();
        LocalTime time = reservation.getHours();
        List<Integer> tableIds = new ArrayList<>(); //list of tables ids of the tables that need to be updated

        if (tables == null || tables.isEmpty()) {
            return new Response(ADDED_RESERVATION, null, "No tables provided for reservation", ERROR, THIS_CLIENT);
        }
        for (RestTable table : tables) {
            table.addUnavailableFromTime(time);
            tableIds.add(table.getId());
        }
        reservation.setStatus(ResInfo.Status.APPROVED);
        //wait for branch tables to be set
        synchronized (branch) {
            branch.addReservation(reservation,tables,tableIds);

            try {
                while (!branch.tablesAreSet) {
                    branch.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new Response(ADDED_RESERVATION, null, "Thread interrupted.", ERROR, THIS_CLIENT);
            }
        }
        Customer customerDB=checkIfCustomerInDB(customer.getEmail());
        if(customerDB!=null) //customer is in DB
        {
            reservation.setCustomer(null);
            customerInDB=true;
        }
        ResInfo newReservation = resInfoRepository.addReservation(reservation);
        newReservation.setBranch(branch); //set the branch with the updated tables to reservation
        if(customerInDB) //set newReservation customer to be the one in DB
        {
            System.out.println("customerInDB if set");
            newReservation.setCustomer(customerDB);
            //add customer info to reservation in db
            resInfoRepository.setCustomer(newReservation);


        }
        if (newReservation == null) {
            return new Response(ADDED_RESERVATION, null, "Failed to save reservation.", ERROR, THIS_CLIENT);
        }
        response1.setData(newReservation);
        response1.setStatus(SUCCESS);
        response1.setMessage("Dear " + customer.getName() + ",\n" +
                "Your reservation has been confirmed.\n\n" +
                "Time: " + reservation.getHours() + "\n" +
                "Guests: " + reservation.getNumOfGuests() + "\n" +
                "Branch: " + reservation.getBranch().getName() + "\n\n" +
                "Enjoy your meal!");

        response2.setData(newReservation);
        response2.setStatus(SUCCESS);

        response.setData(List.of(response1, response2));
        response.setStatus(SUCCESS);
        return response;
    }
    private Customer checkIfCustomerInDB(String email)
    {
        return resInfoRepository.getCustomerByEmail(email);
    }


    public List<ResInfo> checkTableAvailability(ResInfo resInfo)
    {
        boolean available=false;
        Set<RestTable> restTables = resInfo.getTable();
        LocalTime time = resInfo.getHours();
        List<ResInfo> conflictingReservations=resInfoRepository.findConflictingReservations(restTables,time);
        if(conflictingReservations.isEmpty())
        {
            return null;
        }
        for(ResInfo res:conflictingReservations)
        {
            System.out.println("reservation- "+res.getResID()+" is conflicting with "+resInfo.getCustomer().getName());
        }
        return conflictingReservations;
    }
    public Response handleNewReservation(Request request) {
        System.out.println("in handleNewReservation");
        ResInfo reservation = (ResInfo) request.getData();
        Response response=new Response(ADDED_RESERVATION, null, null, BOTH);
        List<ResInfo> conflictingReservations=checkTableAvailability(reservation);
        if (!(conflictingReservations==null)) {
            for(ResInfo res:conflictingReservations)
            {
                System.out.println("reservation: "+res.getResID()+" conflict with this reservation in tables:\n");
                for (RestTable t:res.getTable())
                {
                    System.out.println(t.getId()+"\n");
                }
            }
            return new Response<>(ADDED_RESERVATION, conflictingReservations,
                    "One or more selected tables are already reserved at this time."+conflictingReservations.get(0).getResID(), ERROR, THIS_CLIENT);
        }
        response=addReservation(reservation);

        return response;
    }

public Response<List<Response>> cancelReservation(Request request) {
    try {
        Integer resID = (Integer) request.getData();

        // Retrieve reservation object
        ResInfo reservation = resInfoRepository.findById(resID);
        if (reservation == null || reservation.getIsCancelled()) {
            return new Response<>(
                    Response.ResponseType.CANCELED_RESERVATION,
                    null,
                    "Reservation not found or already cancelled",
                    Response.Status.ERROR,
                    Response.Recipient.THIS_CLIENT
            );
        }

        // Cancel in DB and get penalty result
        String cancelResult = resInfoRepository.cancelReservation(resID);  // updates isCancelled, table times, etc.

        // Update branch tables & reservations
        Branch branch = reservation.getBranch();
        List<Integer> tableIds = new ArrayList<>();
        for (RestTable table : reservation.getTable()) {
            tableIds.add(table.getId());
        }
        synchronized (branch) {
            branch.cancelReservation(reservation, reservation.getTable(), tableIds);
            reservation = resInfoRepository.refreshReservationWithBranch(resID);
        }


        // Prepare responses
        String message;
        String penalty = "0";

        if (cancelResult.startsWith("PENALTY")) {
            penalty = cancelResult.split(":")[1];
            message = "Reservation cancelled with penalty: " + penalty + " ILS";
        } else {
            message = "Reservation cancelled successfully, no penalty.";
        }
        Response<String> clientRes = new Response<>(CANCELED_RESERVATION, penalty, message, SUCCESS, THIS_CLIENT);
        Response<ResInfo> updateRes = new Response<>(UPDATE_BRANCH_TABLES, reservation, message, SUCCESS, ALL_CLIENTS);
        Response<List<Response>> combined = new Response<>(CANCELED_RESERVATION, List.of(clientRes, updateRes), message, SUCCESS, BOTH);
        System.out.println("returned combined from res controller");
        return combined;

    } catch (Exception e) {
        return new Response<>(CANCELED_RESERVATION, null, "Error cancelling reservation: " + e.getMessage(), ERROR, THIS_CLIENT);
    }
}



}
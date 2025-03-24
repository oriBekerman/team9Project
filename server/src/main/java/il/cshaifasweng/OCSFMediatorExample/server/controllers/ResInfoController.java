package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.server.SimpleServer;
//import il.cshaifasweng.OCSFMediatorExample.server.repositories.CustomerRepository;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ResInfoRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
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
//            case CANCEL_RESERVATION-> cancelReservation(request);
            case GET_RES_REPORT -> getAllReservations();
            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
        };
    }

//    private Response addReservation(Request request) {
//        Response response=new Response(ADDED_RESERVATION,null,null,THIS_CLIENT);
//        ResInfo reservation= (ResInfo) request.getData();
//        Customer customer= reservation.getCustomer();
////        String email= customer.getEmail();
////        Customer customer1=getCustomer(email);
////        if(customer1!=null) //customer is already in database no need to save it again
////        {
////            reservation.setCustomer(customer1);
////            resInfoRepository.addReservation(reservation,true);
////        }
////        //customer is not in database need to save it
////        else {
////            resInfoRepository.addReservation(reservation,false);
////        }
//        resInfoRepository.addReservation(reservation,false);
//        response.setData(reservation);
//        response.setStatus(SUCCESS);
//        response.setMessage("Dear " + customer.getName() + ",\n" +
//                "Your reservation has been confirmed.\n" +
//                "Here are the details:\n\n" +
//                "Time: " + reservation.getHours() + "\n" +
//                "Guests: " + reservation.getNumOfGuests() + "\n" +
//                "Branch: " + reservation.getBranch().getName() + "\n" +
//                "Enjoy your meal!");
//        return response;
//    }

    //constructor
    public ResInfoController() {this.resInfoRepository = new ResInfoRepository();};

    public boolean checkIfEmpty()
    {
        return (resInfoRepository.checkIfEmpty());
    }

    //initialize menuItem table with base items
    public void PopulateResSInfo(List<ResInfo>resSInfo) {
        resInfoRepository.populate(resSInfo);
    }

    // Method to fetch all reservations and wrap in a response
    public Response<List<ResInfo>> getAllReservations() {
        try {
            List<ResInfo> reservations = resInfoRepository.getAllResSInfo();
            return new Response<>(RETURN_RES_REPORT, reservations, "Fetched all reservations successfully",SUCCESS,THIS_CLIENT);
        } catch (Exception e) {
            return new Response<>(RETURN_RES_REPORT, "Failed to fetch reservations: " + e.getMessage(), ERROR,THIS_CLIENT);
        }
    }

    public List <ResInfo> getAllResSInfo()
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


    public boolean checkTableAvailability(ResInfo resInfo)
    {
        boolean available=false;
        Set<RestTable> restTables = resInfo.getTable();
        LocalTime time = resInfo.getHours();
        List<ResInfo> conflictingReservations=resInfoRepository.findConflictingReservations(restTables,time);
        if(conflictingReservations.isEmpty())
        {
            available=true;
        }
        return available;
    }
    public Response handleNewReservation(Request request) {
        System.out.println("in handleNewReservation");
        ResInfo reservation = (ResInfo) request.getData();
        Response response=new Response(ADDED_RESERVATION, null, null, BOTH);

        if (!checkTableAvailability(reservation)) {
            return new Response<>(ADDED_RESERVATION, null,
                    "One or more selected tables are already reserved at this time.", ERROR, THIS_CLIENT);
        }
        response=addReservation(reservation);

        return response;
    }















//    public Response<List<Response>>cancelReservation(Request request)
//    {
//        int fine=0;
//        //send to all client to update tables
//        Response response=new Response(CANCELED_RESERVATION,null,ERROR,BOTH);
//        List<Response> responses=new ArrayList<>();
//        Response response1=new Response<>(CANCELED_RESERVATION,null,ERROR,ALL_CLIENTS);
//        //send to this client to get fine
//        Response response2=new Response<>(CANCELED_RESERVATION,null,ERROR,THIS_CLIENT);
//        ResInfo reservation= (ResInfo) request.getData();
//        if(reservation.getHours().minusHours(1).isBefore(LocalTime.now()))
//        {
//            fine=10;
//        }
//        resInfoRepository.deleteReservation(reservation);
//        response1.setData(reservation);
//        response1.setStatus(SUCCESS);
//        response2.setData(fine);
//        response2.setStatus(SUCCESS);
//        response2.setMessage("since the cancellation is within an hour of the reservation time the cancellation fine is "+fine);
//        responses.add(response1);
//        responses.add(response2);
//        response.setData(responses);
//        response.setStatus(SUCCESS);
//        return response;
//    }
//    private Customer getCustomer(String email)
//    {
//        return customerRepository.findByEmail(email);
//    }



}
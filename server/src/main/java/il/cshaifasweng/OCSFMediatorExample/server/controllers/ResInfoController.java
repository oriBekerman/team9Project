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
            case GET_RES_REPORT -> getAllReservations();
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
        resInfoRepository.populate(resSInfo);
    }

    // Method to fetch all reservations and wrap in a response
    public Response<List<ResInfo>> getAllReservations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<ResInfo> reservations = session.createQuery(
                    "SELECT DISTINCT r FROM ResInfo r JOIN FETCH r.customer WHERE r.isCancelled = false",
                    ResInfo.class).list();

            return new Response<>(
                    Response.ResponseType.RETURN_RES_REPORT,
                    reservations,
                    "Reservations loaded successfully",
                    Response.Status.SUCCESS,
                    Response.Recipient.THIS_CLIENT
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(
                    Response.ResponseType.RETURN_RES_REPORT,
                    new ArrayList<>(),
                    "Failed to load reservations: " + e.getMessage(),
                    Response.Status.ERROR,
                    Response.Recipient.THIS_CLIENT
            );
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
        return conflictingReservations;
    }
    public Response handleNewReservation(Request request) {
        System.out.println("in handleNewReservation");
        ResInfo reservation = (ResInfo) request.getData();
        Response response=new Response(ADDED_RESERVATION, null, null, BOTH);
        List<ResInfo> conflictingReservations=checkTableAvailability(reservation);
        if (!(conflictingReservations==null)) {
            return new Response<>(ADDED_RESERVATION, conflictingReservations,
                    "One or more selected tables are already reserved at this time.", ERROR, THIS_CLIENT);
        }
        response=addReservation(reservation);

        return response;
    }



    public Response<String> cancelReservation(Request request) {
        Integer resID= (Integer) request.getData();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            ResInfo reservation = session.get(ResInfo.class, resID);

            if (reservation == null || reservation.getIsCancelled()) {
                tx.rollback();
                return new Response<>(
                        CANCELED_RESERVATION,
                        null,
                        "Reservation not found or already cancelled",
                        Response.Status.ERROR,
                        Response.Recipient.THIS_CLIENT
                );
            }

            LocalTime reservationTime = reservation.getHours();
            LocalTime currentTime = LocalTime.now();
            int guests = reservation.getNumOfGuests();

            boolean penalty = currentTime.isAfter(reservationTime.minusHours(1));

            reservation.setIsCancelled(true);

            Set<RestTable> tables = reservation.getTable();
            for (RestTable table : tables) {
                table.removeUnavailableFromTime(reservationTime);
                session.update(table);
            }

            session.update(reservation);
            tx.commit();

            int penaltyAmount = penalty ? guests * 10 : 0;
            String message = penalty ?
                    "Reservation cancelled with penalty: " + penaltyAmount + " ILS" :
                    "Reservation cancelled successfully, no penalty.";

            return new Response<>(
                    CANCELED_RESERVATION,
                    String.valueOf(penaltyAmount),
                    message,
                    Response.Status.SUCCESS,
                    Response.Recipient.THIS_CLIENT
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(
                    CANCELED_RESERVATION,
                    null,
                    "Error cancelling reservation: " + e.getMessage(),
                    Response.Status.ERROR,
                    Response.Recipient.THIS_CLIENT
            );
        }
    }




}
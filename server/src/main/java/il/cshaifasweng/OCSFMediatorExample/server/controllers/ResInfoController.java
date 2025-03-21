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

    private ResInfoRepository resInfoRepository;
//    private static CustomerRepository customerRepository;

    // Handle requests based on type
    public Response handleRequest(Request request) {
        System.out.println("Handling request: " + request.getRequestType());
        return switch (request.getRequestType())
        {
            case ADD_RESERVATION -> addReservation(request);
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

    public Response addReservation(Request request) {
        System.out.println("in add reservation controller 11111");
        Response response=new Response(ADDED_RESERVATION,null,null,THIS_CLIENT);
        ResInfo reservation = (ResInfo) request.getData();
        if(reservation.getBranch()==null)
        {
            System.out.println("reservation has no branch");
        }
        Branch branch = reservation.getBranch();
        Customer customer = reservation.getCustomer();
        Set<RestTable> tables = reservation.getTable();
        LocalTime time = reservation.getHours();
        System.out.println("in add reservation controller 2222");
        // 1. Validate table list
        if (tables == null || tables.isEmpty()) {
            System.out.println("tables is empty or null");
            return new Response(ADDED_RESERVATION, null, "No tables provided for reservation", ERROR, THIS_CLIENT);

        }
        System.out.println("in add reservation controller 3333");
        // 2. Mark each table as unavailable
        for (RestTable table : tables) {
                System.out.println("in add reservation table loop: " + table.getId());
                table.addUnavailableFromTime(time);
        }
        System.out.println("in add reservation controller after table.addunavilable");

        // 3. Set status and link relationships
        reservation.setStatus(ResInfo.Status.APPROVED);
        branch.addReservation(reservation);  // sets branch on reservation and adds to branch.reservations

        // 4. Save reservation
        resInfoRepository.addReservation(reservation);
        response.setData(reservation);
        response.setStatus(SUCCESS);
        response.setMessage("Dear " + customer.getName() + ",\n" +
                "Your reservation has been confirmed.\n" +
                "Here are the details:\n\n" +
                "Time: " + reservation.getHours() + "\n" +
                "Guests: " + reservation.getNumOfGuests() + "\n" +
                "Branch: " + reservation.getBranch().getName() + "\n" +
                "Enjoy your meal!");

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
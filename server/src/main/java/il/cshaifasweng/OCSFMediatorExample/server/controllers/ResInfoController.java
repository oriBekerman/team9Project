package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.server.SimpleServer;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ResInfoRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

public class ResInfoController {

    private ResInfoRepository resInfoRepository;

    // Handle requests based on type
    public Response handleRequest(Request request) {
        System.out.println("Handling request: " + request.getRequestType());
        return switch (request.getRequestType())
        {
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
        System.out.println("[ResInfoController] Populating " + resSInfo.size() + " reservations.");
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
    public Response<List<Response>>cancelReservation(Request request)
    {
        int fine=0;
        //send to all client to update tables
        Response response=new Response(CANCELED_RESERVATION,null,ERROR,BOTH);
        List<Response> responses=new ArrayList<>();
        Response response1=new Response<>(CANCELED_RESERVATION,null,ERROR,ALL_CLIENTS);
        //send to this client to get fine
        Response response2=new Response<>(CANCELED_RESERVATION,null,ERROR,THIS_CLIENT);
        ResInfo reservation= (ResInfo) request.getData();
        if(reservation.getHours().minusHours(1).isBefore(LocalTime.now()))
        {
            fine=10;
        }
        resInfoRepository.deleteReservation(reservation);
        response1.setData(reservation);
        response1.setStatus(SUCCESS);
        response2.setData(fine);
        response2.setStatus(SUCCESS);
        response2.setMessage("since the cancellation is within an hour of the reservation time the cancellation fine is "+fine);
        responses.add(response1);
        responses.add(response2);
        response.setData(responses);
        response.setStatus(SUCCESS);
        return response;
    }

}
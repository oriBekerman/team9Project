package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ResInfoRepository;

import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

public class ResInfoController {

    private ResInfoRepository resInfoRepository;

    // Handle requests based on type
    public Response<List<ResInfo>> handleRequest(Request request) {
        System.out.println("Handling request: " + request.getRequestType());
        return switch (request.getRequestType()) {
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



}

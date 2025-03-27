package il.cshaifasweng.OCSFMediatorExample.server.controllers;

//import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.Response;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ComplaintRepository;
import javafx.util.Pair;

import java.time.LocalDateTime;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

public class ComplaintController {
    private static ComplaintRepository complaintRepository;
    public ComplaintController() {
        complaintRepository = new ComplaintRepository();
    }
    //calls the needed method for each request,each method returns response
//    public Response handleRequest(Request request)
//    {
//        return switch (request.getRequestType())
//        {
//            case SUBMIT_COMPLAINT ->
//            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
//        };
//    }

    public Response handleRequest(Request request) {
        System.out.println("Handling complaint request: " + request.getRequestType());
        return switch (request.getRequestType())
                {
                    case SUBMIT_COMPLAINT -> createComplaint(request);
                    default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
                };
    }


    public Response createComplaint(Request request) {
        Response response = new Response(Response.ResponseType.COMPLAINT_CREATED, null, ERROR, THIS_CLIENT);
        System.out.println("Creating complaint...");

        try {
            // Extract data from the request
            Complaint complaint = (Complaint) request.getData(); // Assume we send a Complaint object in the request

            // Ensure complaintDate is set to the current LocalDateTime if not already set
            if (complaint.getComplaintDate() == null) {
                complaint.setComplaintDate(LocalDateTime.now()); // Set current LocalDateTime
            }

            // Create the complaint and check the result
            boolean isCreated = ComplaintRepository.populateComplaint(complaint);

            // If the complaint was created successfully, set the response status to SUCCESS
            if (isCreated) {
                response.setStatus(SUCCESS);
                response.setData(complaint); // include complaint in the response
            } else {
                // If creation fails, set the response status to ERROR
                response.setStatus(ERROR);
                response.setMessage("Failed to create complaint");
            }

        } catch (Exception exception) {
            // Handle exceptions and set the response status to ERROR
            response.setStatus(ERROR);
            response.setMessage("Error while creating the complaint: " + exception.getMessage());
            System.err.println("Error while creating complaint: " + exception.getMessage());
            exception.printStackTrace();
        }

        return response;
    }


    public void populateComplaints(List<Complaint> complaints) {
        for (Complaint complaint : complaints) {
            boolean success = complaintRepository.populateComplaint(complaint);
            if (!success) {
                System.err.println("Failed to save complaint: " + complaint);
            }
        }
    }

    public boolean checkIfEmpty()
    {
        return (complaintRepository.checkIfEmpty());
    }
//    public Response submitComplaint(Request request)
//    {
//        Pair<Complaint,List<String>> pair= (Pair<Complaint, List<String>>) request.getData();
//        Complaint complaint = pair.getKey();
//        List<String> customerDetails = pair.getValue();
//
//
//    }

}

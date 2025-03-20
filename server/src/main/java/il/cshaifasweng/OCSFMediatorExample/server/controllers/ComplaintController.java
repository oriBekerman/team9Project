package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.Response;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ComplaintRepository;
import javafx.util.Pair;

import java.util.List;

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
    public void populateComplaints(List<Complaint> complaints) {
        boolean flag=true;
        for(Complaint complaint : complaints) {
            if(flag){

                flag=complaintRepository.populateComplaint(complaint);
            }
            if(!flag)
            {
                System.out.println("complaint not saved!");
                break;
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

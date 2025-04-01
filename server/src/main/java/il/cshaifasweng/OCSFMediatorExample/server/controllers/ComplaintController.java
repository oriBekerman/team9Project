package il.cshaifasweng.OCSFMediatorExample.server.controllers;

//import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.EmailSender;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ComplaintRepository;
import javafx.util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

public class ComplaintController {
    private static ComplaintRepository complaintRepository;
    public ComplaintController() {
        complaintRepository = new ComplaintRepository();
    }
//    calls the needed method for each request,each method returns response
    public Response handleRequest(Request request)
    {
        return switch (request.getRequestType())
        {
            case GET_ALL_COMPLAINTS ->getAllComplaints();
            case HANDLE_COMPLAINT_TABLE -> updateComplaint((List<Complaint>) request.getData());
            case SUBMIT_COMPLAINT -> createComplaint(request);
            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
        };
    }
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

    public boolean checkIfEmpty()
    {
        return (complaintRepository.checkIfEmpty());
    }
    private Response<List<Complaint>> getAllComplaints()
    {
        Response response=new Response(RETURN_ALL_COMPLAINTS,null,null,ERROR,THIS_CLIENT);
        List<Complaint> complaints=new ArrayList<>();
        complaints=complaintRepository.findAll();
        if(complaints.size()==0)
        {
            response.setMessage("No complaints found");
            return response;
        }
        response.setStatus(SUCCESS);
        response.setData(complaints);
        return response;
    }
    private Response<List<Complaint>> updateComplaint(List<Complaint> complaints)
    {
        Response response=new Response(UPDATE_COMPLAINT,null,null,ERROR,THIS_CLIENT);

        complaints=complaintRepository.updateComplaintsList(complaints);
        if(complaints.size()==0)
        {
            response.setMessage("No complaints found");
            return response;
        }

        response.setStatus(SUCCESS);
        response.setData(complaints);
        for(Complaint complaint : complaints)
        {
            sendEmail(complaint);
        }
        return response;
    }
    private void sendEmail(Complaint complaint)
    {
        Customer customer=complaint.getCustomer();
        String body;
        if(complaint.getCompensation()==0)
        {
             body="Dear"+customer.getName()+",\n" +
                    "\n" +
                    "Thanks for your message. We’ve reviewed your complaint and taken care of it.\n" +
                    "\n" +
                    "Sorry again for the inconvenience, and thank you for helping us improve!\n" +
                    "\n" +
                    "Let us know if there’s anything else we can do.\n" +
                    "\n" +
                    "Best,  \n" +
                    "Mama’s Kitchen Team";
        }
        else
        {
            body="Dear"+customer.getName()+",\n" +
                    "\n" +
                    "Thanks for your message. We’ve reviewed your complaint and it has already been handled.\n" +
                    "\n" +
                    "As a small apology, we’ve added a refund of"+complaint.getCompensation()+"$ to your account.\n" +
                    "\n" +
                    "Sorry again for the inconvenience, and thank you for helping us improve!\n" +
                    "\n" +
                    "Best,  \n" +
                    "Mama’s Kitchen Team";
        }

        EmailSender.sendEmail(customer.getEmail(), "Mama's kitchen complaint",body);
    }

    private Response<List<Complaint>> getComplaintByEmployee(Request request)
    {
        Response response=new Response<>(Complaint_BY_EMPLOYEE,null,"",ERROR,THIS_CLIENT);
        Employee employee= (Employee) request.getData();
        if(!(employee.getEmployeeType().equals(Complaint_BY_EMPLOYEE)))
        {
            response.setResponseType(Complaint_BY_EMPLOYEE);
            response.setStatus(ERROR);
            response.setMessage("Not a customer service employee");
            return response;
        }
        Integer id= employee.getId();
        List<Complaint> complaints=complaintRepository.getComplaintsByEmployee(id);
        if(complaints.size()==0)
        {
            response.setResponseType(Complaint_BY_EMPLOYEE);
            response.setStatus(ERROR);
            response.setMessage("No complaint attached to this employee");
            return response;
        }
        response.setResponseType(Complaint_BY_EMPLOYEE);
        response.setData(complaints);
        response.setStatus(SUCCESS);
        return  response;
    };
    private Response<List<Complaint>>getComplaintByDate(Request request)
    {
        Response response=new Response<>(Complaint_BY_DATE,null,"",ERROR,THIS_CLIENT);
        LocalDateTime dateTime= (LocalDateTime) request.getData();
        List<Complaint> complaints=complaintRepository.getComplaintsByDate(dateTime);
        if(complaints.size()==0)
        {
            response.setStatus(ERROR);
            response.setMessage("No complaints enter at this date");
            return response;
        }
        response.setResponseType(Complaint_BY_DATE);
        response.setData(complaints);
        response.setStatus(SUCCESS);
        return  response;
    };

}

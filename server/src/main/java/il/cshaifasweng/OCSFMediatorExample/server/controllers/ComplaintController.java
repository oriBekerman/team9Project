package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ComplaintRepository;

import java.util.List;

public class ComplaintController {
    private static ComplaintRepository complaintRepository;
    public ComplaintController() {
        complaintRepository = new ComplaintRepository();
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
    public boolean checkIfEmpty()
    {
        return (complaintRepository.checkIfEmpty());
    }

}

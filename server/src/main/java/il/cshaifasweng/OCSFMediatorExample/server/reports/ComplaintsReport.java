package il.cshaifasweng.OCSFMediatorExample.server.reports;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ComplaintRepository;

import java.util.ArrayList;
import java.util.List;

public class ComplaintsReport extends Report {
    private ComplaintRepository repository;

    public ComplaintsReport(int branchId, ComplaintRepository repository) {
        super(branchId);
        this.repository = repository;
    }

    @Override
    public void fetchData() {
        data = repository.getComplaintsForReport(branchId);
    }

    @Override
    public Object generateReportData() {
        return data;  // return complaints directly, NOT formatted strings
    }

}
//    @Override
//    public Object generateReportData() {
//        List<String> formattedData = new ArrayList<>();
//        List<Complaint> complaints = (List<Complaint>) data;
//        for (Complaint complaint : complaints) {
//            String entry = "Complaint ID: " + complaint.getComplaintId() + ", Status: " + complaint.getStatus();
//            formattedData.add(entry);
//        }
//        return formattedData;
//    }
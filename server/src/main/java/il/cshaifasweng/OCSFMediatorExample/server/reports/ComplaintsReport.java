package il.cshaifasweng.OCSFMediatorExample.server.reports;

import il.cshaifasweng.OCSFMediatorExample.server.repositories.ComplaintRepository;

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

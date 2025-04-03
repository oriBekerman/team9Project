package il.cshaifasweng.OCSFMediatorExample.server.reports;

import il.cshaifasweng.OCSFMediatorExample.server.repositories.DeliveryRepository;

import java.util.List;

public class DeliveriesReport extends Report {
    private DeliveryRepository repository;

    public DeliveriesReport(int branchId, DeliveryRepository repository) {
        super(branchId);
        this.repository = repository;
    }

    @Override
    public void fetchData() {
        data = repository.getDeliveriesForReport(branchId);
        System.out.println("[DeliveriesReport] fetched data size: " + ((List)data).size());
    }


    @Override
    public Object generateReportData() {
        return data;  // directly return List<ResInfo>
    }

}


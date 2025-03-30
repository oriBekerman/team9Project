package il.cshaifasweng.OCSFMediatorExample.server.reports;
import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;
import java.util.ArrayList;
import java.util.List;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ResInfoRepository;

public class ReservationsReport extends Report {
    private ResInfoRepository repository;

    public ReservationsReport(int branchId, ResInfoRepository repository) {
        super(branchId);
        this.repository = repository;
    }

    @Override
    public void fetchData() {
        data = repository.getReservationsForReport(branchId);
    }


    @Override
    public Object generateReportData() {
        return data;  // directly return List<ResInfo>
    }
}

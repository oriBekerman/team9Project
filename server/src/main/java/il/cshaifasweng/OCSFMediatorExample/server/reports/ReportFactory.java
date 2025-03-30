package il.cshaifasweng.OCSFMediatorExample.server.reports;

import il.cshaifasweng.OCSFMediatorExample.entities.RequestType;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ComplaintRepository;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.DeliveryRepository;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ResInfoRepository;


public class ReportFactory {
    public static Report getReport(RequestType type, int branchId, ResInfoRepository resInfoRepo, DeliveryRepository deliveryRepo, ComplaintRepository complaintRepo) {
        switch (type) {
            case GET_RES_REPORT:
                return new ReservationsReport(branchId, resInfoRepo);
            case GET_DELIV_REPORT:
                return new DeliveriesReport(branchId, deliveryRepo);
            case GET_COMP_REPORT:
                return new ComplaintsReport(branchId, complaintRepo);
            default:
                throw new IllegalArgumentException("Unsupported report type: " + type);
        }
    }
}


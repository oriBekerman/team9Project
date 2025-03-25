package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;

import java.util.List;

public class ReceivedAllComplaintsEvent {
    List<Complaint> complaintList;
    String message;
    public ReceivedAllComplaintsEvent(String message) {

    }
    public ReceivedAllComplaintsEvent(List<Complaint> complaintList, String message) {
        this.complaintList = complaintList;
        this.message = "";
    }
    public List<Complaint> getComplaintList() {
        return complaintList;
    }
    public void setComplaintList(List<Complaint> complaintList) {
        this.complaintList = complaintList;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

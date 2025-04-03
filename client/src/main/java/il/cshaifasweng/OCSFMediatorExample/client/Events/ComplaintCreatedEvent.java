package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;

public class ComplaintCreatedEvent {
    private final Complaint complaint;

    public ComplaintCreatedEvent(Complaint complaint) {
        this.complaint = complaint;
    }

    public Complaint getComplaint() {
        return complaint;
    }
}

package il.cshaifasweng.OCSFMediatorExample.entities;

public enum ComplaintStatus {
    NEW,                        // A newly submitted complaint
    IN_PROGRESS,                // An employee has started handling the complaint
    RESOLVED_WITH_COMPENSATION, // Complaint resolved with a refund
    RESOLVED_WITHOUT_COMPENSATION // Complaint resolved without any compensation
}

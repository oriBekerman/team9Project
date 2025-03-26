package il.cshaifasweng.OCSFMediatorExample.entities;

public class ReservationCancelledEvent {
    private final String message;

    public ReservationCancelledEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

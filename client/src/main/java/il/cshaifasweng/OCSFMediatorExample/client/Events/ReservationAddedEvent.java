package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;

public class ReservationAddedEvent {
    ResInfo reservation;
    String message;
    public ReservationAddedEvent(ResInfo reservation, String message) {
        this.reservation = reservation;
        this.message = message;
    }
    public ResInfo getReservation() {
        return reservation;
    }
    public void setReservation(ResInfo reservation) {
        this.reservation = reservation;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

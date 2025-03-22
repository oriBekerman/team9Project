package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;

public class TableIsReservedEvent {
    ResInfo reservation;
    public TableIsReservedEvent(ResInfo reservation) {
        this.reservation = reservation;
    }
    public ResInfo getReservation() {
        return reservation;
    }
    public void setReservation(ResInfo reservation) {
        this.reservation = reservation;
    }
}

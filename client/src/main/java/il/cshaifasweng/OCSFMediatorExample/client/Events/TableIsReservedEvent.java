package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;

import java.util.List;

public class TableIsReservedEvent {
   List <ResInfo> reservations;
    public TableIsReservedEvent(List<ResInfo> reservations) {
        this.reservations = reservations;
    }
    public List <ResInfo> getReservation() {
        return reservations;
    }
    public void setReservation(List <ResInfo> reservation) {
        this.reservations = reservation;
    }
}


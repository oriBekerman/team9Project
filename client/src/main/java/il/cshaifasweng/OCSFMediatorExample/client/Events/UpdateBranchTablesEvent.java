package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;

public class UpdateBranchTablesEvent {
    ResInfo reservation;
    public UpdateBranchTablesEvent(ResInfo reservation) {
        reservation = reservation;
    }
    public ResInfo getReservation() {
        return reservation;
    }
    public void setReservation(ResInfo reservation) {
        this.reservation = reservation;
    }

}

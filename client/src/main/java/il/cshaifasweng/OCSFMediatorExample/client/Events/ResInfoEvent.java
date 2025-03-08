package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;

import java.util.List;

public class ResInfoEvent {
    public List<ResInfo> reservations;
    public ResInfoEvent(List<ResInfo> reservations) {
        this.reservations = reservations;
    }
}
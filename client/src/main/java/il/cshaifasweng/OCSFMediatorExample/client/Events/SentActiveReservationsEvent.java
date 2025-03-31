package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;

import java.util.List;

public class SentActiveReservationsEvent {
    List<ResInfo> resInfos;
    public SentActiveReservationsEvent(List<ResInfo> resInfos) {
        this.resInfos = resInfos;
    }
    public List<ResInfo> getResInfos() {
        return resInfos;
    }
    public void setResInfos(List<ResInfo> resInfos) {
        this.resInfos = resInfos;
    }
}

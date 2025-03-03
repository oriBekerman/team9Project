package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;

import java.util.List;

public class DeliverableItemsSentEvent {
    List<MenuItem> deliverables;
    public DeliverableItemsSentEvent(List<MenuItem> deliverables) {
        this.deliverables = deliverables;
    }
    public List<MenuItem> getDeliverables() {
        return deliverables;
    }
}

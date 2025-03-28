package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;

public class RemoveDishEvent {
    private final MenuItem removedMenuItem;

    public RemoveDishEvent(MenuItem removedMenuItem) {
        this.removedMenuItem = removedMenuItem;
    }

    public MenuItem getRemovedMenuItem() {
        return removedMenuItem;
    }
}
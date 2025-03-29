package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;

public class AddDishEvent {
    private final MenuItem addedMenuItem;

    public AddDishEvent(MenuItem addedMenuItem) {
        this.addedMenuItem = addedMenuItem;
    }

    public MenuItem getAddedMenuItem() {
        return addedMenuItem;
    }
}
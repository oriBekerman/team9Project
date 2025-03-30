package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;

public class UpdateDishTypeEvent {
    private final MenuItem updatedMenuItem;

    public UpdateDishTypeEvent(MenuItem updatedMenuItem) {
        this.updatedMenuItem = updatedMenuItem;
    }

    public MenuItem getUpdatedMenuItem() {
        return updatedMenuItem;
    }
}

package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;

public class UpdateIngredientsEvent {
    private final MenuItem updatedMenuItem;

    public UpdateIngredientsEvent(MenuItem updatedMenuItem) {
        this.updatedMenuItem = updatedMenuItem;
    }

    public MenuItem getUpdatedMenuItem() {
        return updatedMenuItem;
    }
}
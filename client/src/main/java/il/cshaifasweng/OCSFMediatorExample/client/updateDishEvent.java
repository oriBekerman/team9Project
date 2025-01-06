package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;

public class updateDishEvent {
    MenuItem item;
    updateDishEvent(MenuItem dish) {
        this.item = dish;
    }
}

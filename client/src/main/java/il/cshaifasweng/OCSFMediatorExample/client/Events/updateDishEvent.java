package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;

public class updateDishEvent {
    MenuItem item;
    public updateDishEvent(MenuItem dish)
    {
        this.item = dish;
    }
}

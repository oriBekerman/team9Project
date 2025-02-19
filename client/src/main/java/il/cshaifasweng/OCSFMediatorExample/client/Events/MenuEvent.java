package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;

import java.util.List;

public class MenuEvent {
    private Menu menu;

    public MenuEvent(Menu menu) {
        this.menu = menu;
    }

    public List<MenuItem> getMenuItems() {
        return menu.getMenuItems();
    }
    public Menu getMenu() {
        return menu;
    }
} //change
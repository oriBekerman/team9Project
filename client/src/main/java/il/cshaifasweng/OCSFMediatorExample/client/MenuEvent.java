package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
public class MenuEvent {
    private Menu menu;

    public MenuEvent(Menu menu) {
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }
}
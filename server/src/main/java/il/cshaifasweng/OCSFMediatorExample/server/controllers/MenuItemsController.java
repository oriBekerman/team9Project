package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.MenuItemsRepository;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class MenuItemsController {

    private MenuItemsRepository menuItemsRepository;



    // Constructor to inject the repository
    public MenuItemsController(SessionFactory sessionFactory) {
        if(sessionFactory == null)
        {
            throw new NullPointerException(" in MenuController sessionFactory is null");
        }
        System.out.println("in MenuController constructor");
        this.menuItemsRepository = new MenuItemsRepository(sessionFactory);
    }
    //constructor
    public MenuItemsController() {};

    //checks if menuItems table is empty in DB and if empty saves the next items to table
    public void checkAndPopulateMenuItems() {
        try {
            // Check if the menu is already populated
           if(menuItemsRepository.checkIfEmpty())
           {
               MenuItem item1 = new MenuItem("Salad", 35.00, "Tomatoes, cucumbers, lettuce",
                       "Low calorie", null);

               MenuItem item2 = new MenuItem("Pizza ", 55.00, " Mushrooms, onions, tomatoes",
                       " Includes vegan option ", null);

               MenuItem item3 = new MenuItem("Pasta", 60.00, "Mushroom cream sauce",
                       "Available gluten-free", null);

               MenuItem item4 = new MenuItem("Hamburger", 80.00, "Meatball, pickle, tomato, lettuce",
                       "Choice of meat or plant-based", null);

               MenuItem item5 = new MenuItem("Edamame", 30.00, "Edamame",
                       "Served with sea salt", null);

               List<MenuItem> initMenuItems=new ArrayList<MenuItem>();
               initMenuItems.add(item1);
               initMenuItems.add(item2);
               initMenuItems.add(item3);
               initMenuItems.add(item4);
               initMenuItems.add(item5);
               menuItemsRepository.populate(initMenuItems);
           }
        } catch (Exception exception) {
            throw exception; // Rethrow to ensure rollback in the constructor
        }
    }
    //gets  items from MenuItems table and returns menu
    public Menu displayMenu() //gets menu from menuitem repository
    {
        System.out.println("in MenuController displayMenu");
        Menu menu= new Menu(menuItemsRepository.getMenuItems());
        menu.printMenu();
        return menu;
    }

}

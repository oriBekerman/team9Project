package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.MenuItemsRepository;
import org.hibernate.SessionFactory;

import java.util.List;

public class MenuItemsController {

    private MenuItemsRepository menuItemsRepository;


    // constructor to inject the repository
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
    public boolean checkIfEmpty()
    {
        return (menuItemsRepository.checkIfEmpty());
    }

    //initialize menuItem table with base items
    public void PopulateMenuItems(List<MenuItem>menuItems) {
        menuItemsRepository.populate(menuItems);
    }
//    //gets  items from MenuItems table and returns menu
//    public Menu displayBaseMenu() //gets menu from menuitem repository
//    {
//        System.out.println("in MenuController displayMenu");
//        Menu menu= new Menu(menuItemsRepository.getMenuItems());
//        menu.printMenu();
//        return menu;
//    }
    public MenuItem updatePrice(Request request)
    {
        System.out.println("in MenuController updatePrice1");
        String[] data = (String[]) request.getData();
        int id = Integer.parseInt(data[0]);
        double price =Double.parseDouble(data[1]);
        System.out.println("in MenuController updatePrice2");
        return menuItemsRepository.updatePrice(id, price);
    }
    public List <MenuItem> getAllItems()
    {
        return menuItemsRepository.getAllItems();
    }


}

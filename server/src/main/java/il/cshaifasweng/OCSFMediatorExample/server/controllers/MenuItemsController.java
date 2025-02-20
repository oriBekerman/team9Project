package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.MenuItemsRepository;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.ALL_CLIENTS;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

public class MenuItemsController {

    private MenuItemsRepository menuItemsRepository;
    public Response handleRequest(Request request)
    {
        System.out.println("Handling request: " + request.getRequestType());
        return switch (request.getRequestType())
        {
            case GET_BASE_MENU->getBaseItems();
             case UPDATE_PRICE->updatePrice(request);
            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
        };
    }



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
   //get base menu items
    public Response getBaseItems() {
        Response response=new Response(RETURN_MENU,null,null,THIS_CLIENT);
        System.out.println("getBaseItems control");
        List<MenuItem> menuItems= menuItemsRepository.getBaseItems();
        if(menuItems.isEmpty())
        {
            response.setStatus(ERROR);
        }
        Menu menu=new Menu(menuItems);
        if(menuItems.isEmpty())
        {
            response.setStatus(ERROR);
        }
        else{
            response.setStatus(SUCCESS);
            response.setData(menu);
        }
        return response;
    }
    public Response updatePrice(Request request)
    {
        Response response=new Response(UPDATED_PRICE,null,null,ALL_CLIENTS);
        System.out.println("in MenuController updatePrice1");
        String[] data = (String[]) request.getData();
        int id = Integer.parseInt(data[0]);
        double price =Double.parseDouble(data[1]);
        System.out.println("in MenuController updatePrice2");
        MenuItem item= menuItemsRepository.updatePrice(id, price);
        if(item == null)
        {
            response.setStatus(ERROR);
        }
        else
        {
            response.setStatus(SUCCESS);
            response.setData(item);
        }
        return response;
    }
    public List <MenuItem> getAllItems()
    {
        return menuItemsRepository.getAllItems();
    }


}

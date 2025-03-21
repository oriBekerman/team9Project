package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.MenuItemsRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.ALL_CLIENTS;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;
import il.cshaifasweng.OCSFMediatorExample.entities.Response.Status;
import il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType;

public class MenuItemsController {

    private MenuItemsRepository menuItemsRepository;
    public Response handleRequest(Request request) {
        System.out.println("Handling request: " + request.getRequestType());
        return switch (request.getRequestType()) {
            case GET_BASE_MENU -> getBaseItems();
            case UPDATE_PRICE -> updateThePrice(request);
            case ADD_DISH -> handleAddDishRequest(request);
            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
        };


    }
    public Response handleAddDishRequest(Request<MenuItem> request) {
        MenuItem newDish = request.getData();  // Retrieve the new dish from the request
        // Add the new dish to the database
        boolean success = menuItemsRepository.addMenuItem(newDish);

        if (success) {
            return new Response<>(ResponseType.ADD_DISH, newDish, "Dish added successfully", Status.SUCCESS, Response.Recipient.THIS_CLIENT);
        } else {
            return new Response<>(ResponseType.ADD_DISH, null, "Failed to add dish", Status.ERROR, Response.Recipient.THIS_CLIENT);
        }
    }


    //constructor
    public MenuItemsController() {
        this.menuItemsRepository = new MenuItemsRepository();
    };
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
            System.out.println("getBaseItems returned empty list");
        }
        else{
            Menu menu=new Menu(menuItems);
            response.setStatus(SUCCESS);
            response.setData(menu);
            System.out.println("getBaseItems returned successfully");
            menu.printMenu();
        }
        return response;
    }


    public List<MenuItem> searchMenuItems(String keyword, Double maxPrice, DishType type) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        String hql = "FROM MenuItem m WHERE 1=1 ";
        if (keyword != null && !keyword.isEmpty()) {
            hql += "AND (m.name LIKE :kw OR m.ingredients LIKE :kw) ";
        }
        if (maxPrice != null) {
            hql += "AND m.price <= :maxPrice ";
        }
        if (type != null) {
            hql += "AND m.dishType = :type ";
        }
        Query<MenuItem> query = session.createQuery(hql, MenuItem.class);
        if (keyword != null && !keyword.isEmpty()) {
            query.setParameter("kw", "%" + keyword + "%");
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        if (type != null) {
            query.setParameter("type", type);
        }
        List<MenuItem> results = query.list();
        session.close();
        return results;
    }


    public Response updateThePrice(Request request)
    {
        Response response=new Response(UPDATED_PRICE,null,null,ALL_CLIENTS);
        System.out.println("in MenuController updateThePrice1");
        String[] data = (String[]) request.getData();
        int id = Integer.parseInt(data[0]);
        double price =Double.parseDouble(data[1]);
        System.out.println("in MenuController updateThePrice2");
        MenuItem item= menuItemsRepository.updateThePrice(id, price);
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

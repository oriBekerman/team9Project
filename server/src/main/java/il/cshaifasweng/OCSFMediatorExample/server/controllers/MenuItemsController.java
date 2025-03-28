package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.MenuItemsRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import il.cshaifasweng.OCSFMediatorExample.entities.Response.Status;
import il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType;
import java.util.List;


import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.ALL_CLIENTS;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

public class MenuItemsController {

    private MenuItemsRepository menuItemsRepository;

    public Response handleRequest(Request request) {
        System.out.println("Handling request: " + request.getRequestType());
        return switch (request.getRequestType()) {
            case GET_BASE_MENU -> getBaseItems();
            case UPDATE_PRICE -> updateThePrice(request);
            case ADD_DISH -> handleAddDishRequest(request);
            case REMOVE_DISH -> handleRemoveDishRequest(request);  // Handle REMOVE_DISH here
            case UPDATE_INGREDIENTS -> handleUpdateDishIngredientsRequest(request); // Handle UPDATE_INGREDIENTS here
            case UPDATE_DISH_TYPE -> handleUpdateDishTypeRequest(request); // Handle UPDATE_DISH_TYPE here
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
// In the MenuItemsController's handleRequest method

    public Response handleRemoveDishRequest(Request<MenuItem> request) {
        MenuItem dishToRemove = request.getData();  // Retrieve the dish to be removed from the request
        boolean success = menuItemsRepository.removeDish(dishToRemove);  // Remove the dish from the repository

        if (success) {
            return new Response<>(ResponseType.REMOVE_DISH, dishToRemove, "Dish removed successfully", Status.SUCCESS, ALL_CLIENTS);
        } else {
            return new Response<>(ResponseType.REMOVE_DISH, null, "Failed to remove dish", Status.ERROR, ALL_CLIENTS);
        }
    }

    // Constructor
    public MenuItemsController() {
        this.menuItemsRepository = new MenuItemsRepository();
    }

    // Check if the menu items repository is empty
    public boolean checkIfEmpty() {
        return (menuItemsRepository.checkIfEmpty());
    }

    // Initialize the menuItem table with base items
    public void PopulateMenuItems(List<MenuItem> menuItems) {
        menuItemsRepository.populate(menuItems);
    }

    // Get base menu items
    public Response getBaseItems() {
        Response response = new Response(RETURN_MENU, null, null, THIS_CLIENT);
        System.out.println("getBaseItems control");
        List<MenuItem> menuItems = menuItemsRepository.getBaseItems();
        if (menuItems.isEmpty()) {
            response.setStatus(ERROR);
            System.out.println("getBaseItems returned empty list");
        } else {
            Menu menu = new Menu(menuItems);
            response.setStatus(SUCCESS);
            response.setData(menu);
            System.out.println("getBaseItems returned successfully");
            menu.printMenu();
        }
        return response;
    }

    // Search menu items by keyword, max price, or type
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

    public Response handleUpdateDishIngredientsRequest(Request<MenuItem> request) {
        MenuItem dishToUpdate = request.getData();  // Retrieve the dish with updated ingredients from the request
        int itemId = dishToUpdate.getItemID();  // Get the item ID
        String newIngredients = dishToUpdate.getIngredients();  // Get the new ingredients

        boolean success = menuItemsRepository.updateDishIngredients(itemId, newIngredients);  // Call repository method to update ingredients

        if (success) {
            return new Response<>(ResponseType.UPDATE_INGREDIENTS, dishToUpdate, "Dish ingredients updated successfully", Status.SUCCESS, Response.Recipient.THIS_CLIENT);
        } else {
            return new Response<>(ResponseType.UPDATE_INGREDIENTS, null, "Failed to update dish ingredients", Status.ERROR, Response.Recipient.THIS_CLIENT);
        }
    }

    // Update the price of a menu item
    public Response updateThePrice(Request request) {
        Response response = new Response(UPDATED_PRICE, null, null, ALL_CLIENTS);
        System.out.println("in MenuController updateThePrice1");
        String[] data = (String[]) request.getData();
        int id = Integer.parseInt(data[0]);
        double price = Double.parseDouble(data[1]);
        System.out.println("in MenuController updateThePrice2");
        MenuItem item = menuItemsRepository.updateThePrice(id, price);
        if (item == null) {
            response.setStatus(ERROR);
        } else {
            response.setStatus(SUCCESS);
            response.setData(item);
        }
        return response;
    }

    // Get all menu items
    public List<MenuItem> getAllItems() {
        return menuItemsRepository.getAllItems();
    }


    // Handle update of dish type
    public Response handleUpdateDishTypeRequest(Request<MenuItem> request) {
        MenuItem dishToUpdate = request.getData();  // Retrieve the dish with updated dish type from the request
        int itemId = dishToUpdate.getItemID();  // Get the item ID
        DishType newDishType = dishToUpdate.getDishType();  // Get the new dish type

        boolean success = menuItemsRepository.updateDishType(itemId, newDishType);  // Call repository method to update dish type

        if (success) {
            return new Response<>(ResponseType.UPDATE_DISH_TYPE, dishToUpdate, "Dish type updated successfully", Status.SUCCESS, Response.Recipient.THIS_CLIENT);
        } else {
            return new Response<>(ResponseType.UPDATE_DISH_TYPE, null, "Failed to update dish type", Status.ERROR, Response.Recipient.THIS_CLIENT);
        }
    }
}

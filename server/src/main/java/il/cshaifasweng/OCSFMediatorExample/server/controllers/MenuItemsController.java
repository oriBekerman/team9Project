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
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import org.hibernate.Transaction;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.ALL_CLIENTS;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

public class MenuItemsController
{

    private MenuItemsRepository menuItemsRepository;

    public Response handleRequest(Request request) {
        System.out.println("Handling request: " + request.getRequestType());
        return switch (request.getRequestType())
        {

            case GET_BASE_MENU -> getBaseItems();
            case UPDATE_PRICE -> updateThePrice(request);
            case ADD_DISH -> handleAddDishRequest(request);
            case REMOVE_DISH -> handleRemoveDishRequest(request);
            case UPDATE_INGREDIENTS -> handleUpdateDishIngredientsRequest(request);
            case UPDATE_DISH_TYPE -> handleUpdateDishTypeRequest(request);
            case UPDATE_BRANCH_MENU -> handleUpdateBranchMenuRequest(request);
            case GET_LATEST_MENU_ITEM_ID -> handleGetLatestMenuItemId(request);

            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
        };
    }

    public Response handleGetLatestMenuItemId(Request request) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer latestId = session.createQuery(
                            "SELECT itemID FROM MenuItem ORDER BY itemID DESC", Integer.class)
                    .setMaxResults(1)
                    .uniqueResult();

            if (latestId != null) {
                return new Response<>(Response.ResponseType.GET_LATEST_MENU_ITEM_ID, latestId,
                        "Fetched latest MenuItem ID", Response.Status.SUCCESS, THIS_CLIENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Response<>(Response.ResponseType.GET_LATEST_MENU_ITEM_ID, null,
                "Failed to fetch latest ID", Response.Status.ERROR, THIS_CLIENT);
    }


    public Response handleAddDishRequest(Request<MenuItem> request)
    {
        MenuItem newDish = request.getData();
        boolean success = menuItemsRepository.addMenuItem(newDish);

        if (success)
        {
            List<Branch> branchesToUpdate = menuItemsRepository.getBranchesWithDish(newDish);

            for (Branch branch : branchesToUpdate)
            {
                branch.addMenuItem(newDish);
                menuItemsRepository.updateBranchMenu(branch);
                UpdateBranchSpecialItemRequest data = new UpdateBranchSpecialItemRequest(branch.getId(), newDish.getItemID());


            }

            return new Response<>(ResponseType.ADD_DISH, newDish, "Dish added successfully and branches updated", Status.SUCCESS, ALL_CLIENTS);
        } else {
            return new Response<>(ResponseType.ADD_DISH, null, "Failed to add dish", Status.ERROR, ALL_CLIENTS);
        }
    }

    public Response handleUpdateBranchMenuRequest(Request<Branch> request) {
        Branch branchToUpdate = request.getData();

        boolean success = menuItemsRepository.updateBranchMenu(branchToUpdate);

        if (success) {

            return new Response<>(ResponseType.UPDATE_BRANCH_MENU, branchToUpdate, "Branch menu updated successfully", Status.SUCCESS, ALL_CLIENTS);
        } else {
            return new Response<>(ResponseType.UPDATE_BRANCH_MENU, null, "Failed to update branch menu", Status.ERROR, ALL_CLIENTS);
        }
    }


    public Response handleRemoveDishRequest(Request<MenuItem> request)
    {
        MenuItem dishToRemove = request.getData();
        boolean success = menuItemsRepository.removeDish(dishToRemove);
        if (success)
        {
            return new Response<>(ResponseType.REMOVE_DISH, dishToRemove, "Dish removed successfully", Status.SUCCESS, ALL_CLIENTS);
        }
        else
        {
            return new Response<>(ResponseType.REMOVE_DISH, null, "Failed to remove dish", Status.ERROR, ALL_CLIENTS);
        }
    }

    public MenuItemsController() {
        this.menuItemsRepository = new MenuItemsRepository();
    }
    public boolean checkIfEmpty() {
        return (menuItemsRepository.checkIfEmpty());
    }
    public void PopulateMenuItems(List<MenuItem> menuItems) {
        menuItemsRepository.populate(menuItems);
    }

    public Response getBaseItems()
    {
        Response response = new Response(RETURN_MENU, null, null, THIS_CLIENT);
        System.out.println("getBaseItems control");
        List<MenuItem> menuItems = menuItemsRepository.getBaseItems();
        if (menuItems.isEmpty())
        {
            response.setStatus(ERROR);
            System.out.println("getBaseItems returned empty list");
        }
        else
        {
            Menu menu = new Menu(menuItems);
            response.setStatus(SUCCESS);
            response.setData(menu);
            System.out.println("getBaseItems returned successfully");
            menu.printMenu();
        }
        return response;
    }

    public List<MenuItem> searchMenuItems(String keyword, Double maxPrice, DishType type)
    {
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

    public Response handleUpdateDishIngredientsRequest(Request<MenuItem> request)
    {
        MenuItem dishToUpdate = request.getData();  // Retrieve the dish with updated ingredients from the request
        int itemId = dishToUpdate.getItemID();  // Get the item ID
        String newIngredients = dishToUpdate.getIngredients();

        boolean success = menuItemsRepository.updateDishIngredients(itemId, newIngredients);

        if (success)
        {
            return new Response<>(ResponseType.UPDATE_INGREDIENTS, dishToUpdate, "Dish ingredients updated successfully", Status.SUCCESS, ALL_CLIENTS);
        }
        else
        {
            return new Response<>(ResponseType.UPDATE_INGREDIENTS, null, "Failed to update dish ingredients", Status.ERROR, ALL_CLIENTS);
        }
    }

    public Response updateThePrice(Request request)
    {
        Response response = new Response(UPDATED_PRICE, null, null, ALL_CLIENTS);
        System.out.println("in MenuController updateThePrice1");
        String[] data = (String[]) request.getData();
        int id = Integer.parseInt(data[0]);
        double price = Double.parseDouble(data[1]);
        System.out.println("in MenuController updateThePrice2");
        MenuItem item = menuItemsRepository.updateThePrice(id, price);
        if (item == null) {
            response.setStatus(ERROR);
        }
        else
        {
            response.setStatus(SUCCESS);
            response.setData(item);
        }
        return response;
    }

    public List<MenuItem> getAllItems() {
        return menuItemsRepository.getAllItems();
    }


    public Response handleUpdateDishTypeRequest(Request<MenuItem> request)
    {
        MenuItem dishToUpdate = request.getData();
        int itemId = dishToUpdate.getItemID();
        DishType newDishType = dishToUpdate.getDishType();
        boolean success = menuItemsRepository.updateDishType(itemId, newDishType);

        if (success)
        {
            return new Response<>(ResponseType.UPDATE_DISH_TYPE_RES, dishToUpdate, "Dish type updated successfully", Status.SUCCESS, ALL_CLIENTS);
        }
        else
        {
            return new Response<>(ResponseType.UPDATE_DISH_TYPE_RES, null, "Failed to update dish type", Status.ERROR, ALL_CLIENTS);
        }
    }
}

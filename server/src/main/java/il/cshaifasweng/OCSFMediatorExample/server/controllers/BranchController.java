package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.*;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.ALL_CLIENTS;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.ALL_CLIENTS_EXCEPT_SENDER;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.UPDATE_BRANCH_BASE_ITEM;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;


public class BranchController {
    private static BranchRepository branchRepository;
    // constructor
    public BranchController() {

        branchRepository = new BranchRepository();
    }

    //calls the needed method for each request,each method returns response
    public Response handleRequest(Request request)
    {

        return switch (request.getRequestType())
        {
            case UPDATE_BRANCH_SPECIAL_ITEM -> handleUpdateBranchSpecialItem(request);
            case GET_BRANCH_BY_NAME ->getByName(request.getData());
            case GET_BRANCHES->getALLBranches();
            case GET_BRANCH_MENU->getBranchMenu(request);
            case GET_DELIVERABLES -> getDeliverableItems(request);
            case FETCH_BRANCH_TABLES -> getRestTables(request);
            case UPDATE_BRANCH -> updateBranch(request);
            case UPDATE_BRANCH_BASE_ITEM -> handleUpdateBranchBaseItem(request);

            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());

        };
    }
    public boolean checkIfEmpty()
    {
        return (branchRepository.checkIfEmpty());
    }
    public void populateBranches(List<Branch> branches)
    {
        branchRepository.populate(branches);
    }
    public Response getByName(Object branchName)
    {
        Response response=new Response<>(RETURN_BRANCH_BY_NAME,null,null,THIS_CLIENT);
        String branchNameString = (String) branchName;
        Branch branch= branchRepository.getByName(branchNameString);
        if(branch == null)
        {
            response.setStatus(ERROR);
        }
        else
        {
            response.setStatus(SUCCESS);
            response.setData(branch);
        }
        return response;
    }


    public Response handleUpdateBranchBaseItem(Request request)
    {
        UpdateBranchSpecialItemRequest baseItemRequest = (UpdateBranchSpecialItemRequest) request.getData();

        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            Transaction transaction = session.beginTransaction();

            MenuItem menuItem = session.get(MenuItem.class, baseItemRequest.getMenuItemId());

            if (menuItem != null)
            {
                // Get all branches
                List<Branch> allBranches = session.createQuery("from Branch", Branch.class).list();

                for (Branch branch : allBranches)
                {
                    // Add the dish if not already there
                    if (!branch.getBranchMenuItems().contains(menuItem))
                    {
                        branch.getBranchMenuItems().add(menuItem);
                        session.merge(branch);
                    }
                }

                transaction.commit();

                return new Response<>(Response.ResponseType.UPDATE_BRANCH_BASE_ITEM, null, "Base item added to all branches", Response.Status.SUCCESS, ALL_CLIENTS);
            }

            transaction.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new Response<>(Response.ResponseType.UPDATE_BRANCH_BASE_ITEM, null, "Failed to update base item", Response.Status.ERROR, ALL_CLIENTS);
    }



    public Response handleUpdateBranchSpecialItem(Request request)
    {
        UpdateBranchSpecialItemRequest specialItemRequest = (UpdateBranchSpecialItemRequest) request.getData();

        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            Transaction transaction = session.beginTransaction();

            Branch selectedBranch = session.get(Branch.class, specialItemRequest.getBranchId());
            MenuItem menuItem = session.get(MenuItem.class, specialItemRequest.getMenuItemId());

            if (selectedBranch != null && menuItem != null)
            {
                // Get all branches
                List<Branch> allBranches = session.createQuery("from Branch", Branch.class).list();

                for (Branch branch : allBranches)
                {
                    // Remove the menuItem from all branches
                    if (branch.getBranchMenuItems().contains(menuItem)) {
                        branch.getBranchMenuItems().remove(menuItem);
                        session.merge(branch);
                    }
                }

                // Add the menuItem to the selected branch
                selectedBranch.getBranchMenuItems().add(menuItem);
                session.merge(selectedBranch);

                transaction.commit();

                return new Response<>(Response.ResponseType.UPDATE_BRANCH_SPECIAL_ITEM, selectedBranch, "Special item updated successfully", Response.Status.SUCCESS, ALL_CLIENTS);
            }

            transaction.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new Response<>(Response.ResponseType.UPDATE_BRANCH_SPECIAL_ITEM, null, "Failed to update special item", Response.Status.ERROR, ALL_CLIENTS);
    }


    public Response getALLBranches()
    {
        Response response=new Response<>(BRANCHES_SENT,null,ERROR,THIS_CLIENT);
         List<Branch>branches=branchRepository.findAll();
         if(branches==null)
         {
             response.setStatus(ERROR);
         }
         else
         {
             response.setStatus(SUCCESS);
             response.setData(branches);
         }
         return response;
    }

    private Response getBranchMenu(Request request) {
        Response response=new Response<>(RETURN_MENU,null,ERROR,THIS_CLIENT);
        Branch branch= (Branch) request.getData();
        Menu menu = new Menu();
        List<MenuItem> items;
        items=branchRepository.getBranchMenuItems(branch);
        System.out.println(items.get(0).getName());
        menu.setMenuItems(items);
        if(!menu.getMenuItems().isEmpty())
        {
            response.setData(menu);
            response.setStatus(SUCCESS);
        }
        return response;
    }
    public Response getDeliverableItems(Request request)
    {
        Branch branch= (Branch) request.getData();
        Response response=new Response<>(RETURN_DELIVERABLES,null,ERROR,THIS_CLIENT);
        List<MenuItem> deliverables= branchRepository.getDeliverableMenuItems(branch);
           if(deliverables != null)
           {
               response.setStatus(SUCCESS);
               response.setData(deliverables);
           }
           return response;
    }

    public Response getRestTables(Request request)
    {
        Branch branch= (Branch) request.getData();
        Response response=new Response<>(RETURN_BRANCH_TABLES,null,ERROR,THIS_CLIENT);
        List<RestTable> restTables=new ArrayList<>();
        restTables= branchRepository.getRestTables(branch);
        System.out.println("fetch table in cont after rep");
        if(restTables != null)
        {
            response.setStatus(SUCCESS);
            response.setData(restTables);
            for(RestTable restTable:restTables)
            {
                restTable.print();
            }
        }
        return response;
    }
    public Response updateBranch(Request request)
    {
        Response response=new Response<>(UPDATE_BRANCH_RESERVATION,null,ERROR,ALL_CLIENTS_EXCEPT_SENDER);
        Branch branch= (Branch) request.getData();
        Branch branches=branchRepository.updateBranch(branch);
        if(branches==null)
        {
            response.setStatus(ERROR);
        }
        else {
            response.setStatus(SUCCESS);
            response.setData(branches);
        }
        return response;

    }
    
    
}

package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.*;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.*;
import org.hibernate.SessionFactory;

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
            case GET_BRANCH_BY_NAME ->getByName(request.getData());
            case GET_BRANCHES->getALLBranches();
            case GET_BRANCH_MENU->getBranchMenu(request);
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
        Response response=new Response<>(RETURN_BRANCH,null,null,THIS_CLIENT);
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
    public Response getALLBranches()
    {
        Response response=new Response<>(BRANCHES_SENT,null,ERROR,THIS_CLIENT);
         List<Branch>branches=branchRepository.findAll();
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
    
}

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
    //calls the needed method for each request,each method returns response
    public Response handleRequest(Request request)
    {

        return switch (request.getRequestType())
        {
            case GET_BRANCH_BY_NAME ->getByName(request.getData());
            case GET_BRANCHES->getALLBranches();
            case GET_BRANCH_MENU->getBranchMenu(request.getData());
            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
        };
    }
    // constructor to inject the repository
    public BranchController(SessionFactory sessionFactory) {
        if(sessionFactory == null)
        {
            throw new NullPointerException(" in MenuController sessionFactory is null");
        }
        System.out.println("in MenuController constructor");
        this.branchRepository = new BranchRepository(sessionFactory);
    }
    //constructor
    public BranchController() {};

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
         List<Branch>breanches=branchRepository.findAll();
         if(breanches==null)
         {
             response.setStatus(ERROR);
         }
         else {
             response.setStatus(SUCCESS);
             response.setData(breanches);
         }
         return response;

    }
    private Response getBranchMenu(Object data) {
        Response response=new Response<>(  RETURN_BRANCH_MENU,null,ERROR,THIS_CLIENT);
        Branch branch = (Branch) data;
        Menu menu = new Menu();
        List<MenuItem> items;
        items=branchRepository.getBranchMenuItems(branch);
        menu.setMenuItems(items);
        if(!menu.getMenuItems().isEmpty())
        {
            response.setResponseType(BRANCHES_SENT);
            response.setData(menu);
            response.setStatus(SUCCESS);
        }
        return response;
    }
}

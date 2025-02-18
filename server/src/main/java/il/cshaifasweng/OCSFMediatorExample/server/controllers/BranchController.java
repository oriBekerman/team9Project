package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.*;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class BranchController {

    private static BranchRepository branchRepository;

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
    public Branch getByName(String branchName)
    {
        return branchRepository.getByName(branchName);
    }
    public static List<Branch>getALLBranches()
    {
        return branchRepository.findAll();
    }
    //change
}

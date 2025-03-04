package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.BranchRepository;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.RestTableRepository;

import java.util.List;

public class RestTableController {
    private static RestTableRepository restTableRepository;

    // constructor
    public RestTableController() {

        restTableRepository = new RestTableRepository();
    }
    public void populateTables(List<RestTable> tables)
    {
        restTableRepository.populate(tables);
    }
    public boolean checkIfEmpty()
    {
        return (restTableRepository.checkIfEmpty());
    }


}
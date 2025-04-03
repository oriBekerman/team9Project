package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;

public class BranchSelectedEvent {
    private final Branch branch;

    public BranchSelectedEvent(Branch branch) {
        this.branch = branch;
        System.out.println("[BranchSelectedEvent -BranchSelectedEvent constructor ]the branch that selected is: " + branch.getName() +"ID: " + branch.getId());
    }

    public Branch getBranch() {
        return branch;
    }
}

//change
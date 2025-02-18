package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;

public class BranchSelectedEvent {
    private final Branch branch;

    public BranchSelectedEvent(Branch branch) {
        this.branch = branch;
    }

    public Branch getBranch() {
        return branch;
    }
}

//change
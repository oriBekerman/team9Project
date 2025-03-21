package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;

public class BranchSentEvent {
    Branch branch;
    public BranchSentEvent(Branch branch) {
        this.branch = branch;
    }
    public Branch getBranch() {
        return branch;
    }
}

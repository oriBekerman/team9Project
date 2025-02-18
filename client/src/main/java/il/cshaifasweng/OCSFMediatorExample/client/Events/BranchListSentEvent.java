package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.*;


import java.util.List;

public class BranchListSentEvent {
    public List<Branch> branches;
    public BranchListSentEvent(List<Branch> branches) {
        this.branches = branches;
    }
} //change
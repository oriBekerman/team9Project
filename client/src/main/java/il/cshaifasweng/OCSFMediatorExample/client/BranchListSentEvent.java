package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;


import java.util.List;

public class BranchListSentEvent {
    List<Branch> branches;
    BranchListSentEvent(List<Branch> branches) {
        this.branches = branches;
    }
} //change
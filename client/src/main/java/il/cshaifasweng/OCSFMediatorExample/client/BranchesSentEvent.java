package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;


import java.util.List;

public class BranchesSentEvent {
    List<Branch> branches;
    BranchesSentEvent(List<Branch> branches) {
        this.branches = branches;
    }
}
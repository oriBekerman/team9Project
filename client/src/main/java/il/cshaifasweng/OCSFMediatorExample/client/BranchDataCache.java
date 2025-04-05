package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;

import java.util.List;

public class BranchDataCache {
    private static List<Branch> branchList = null;

    public static List<Branch> getBranchList() {
        return branchList;
    }

    public static void setBranchList(List<Branch> list) {
        branchList = list;
    }

    public static boolean isBranchListLoaded() {
        return branchList != null;
    }
}

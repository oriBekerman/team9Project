package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class UpdateBranchSpecialItemRequest implements Serializable
{
    private int branchId;
    private int menuItemId;

    // Constructor
    public UpdateBranchSpecialItemRequest(int branchId, int menuItemId)
    {
        this.branchId = branchId;
        this.menuItemId = menuItemId;
    }

    // Getters
    public int getBranchId() {
        return branchId;
    }

    public int getMenuItemId() {
        return menuItemId;
    }
}

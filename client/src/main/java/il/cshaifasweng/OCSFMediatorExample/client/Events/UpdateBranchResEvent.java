package il.cshaifasweng.OCSFMediatorExample.client.Events;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;

public class UpdateBranchResEvent {
    public Branch getBranch() {
        return branch;
    }

    private final Branch branch;

   public UpdateBranchResEvent(Branch branch) {
       this.branch = branch;}
}

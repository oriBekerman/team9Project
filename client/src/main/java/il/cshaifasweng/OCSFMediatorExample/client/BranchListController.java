package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class BranchListController {
    @FXML private ListView<String> branchListView;
    private List<Branch> branches;
    private PrimaryController primaryController; // Reference to PrimaryController

    @FXML
    public void initialize() {
        branchListView.setItems(FXCollections.observableArrayList());

        // Handle selection
        branchListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Selected: " + newValue);

                // Find the selected branch object
                for (Branch branch : branches) {
                    if (branch.getName().equals(newValue)) {
                        BranchSelectedEvent event = new BranchSelectedEvent(branch);
                        EventBus.getDefault().post(event);
                        break;
                    }
                }
                branchListView.getScene().getWindow().hide(); // Close popup
            }
        });
    }

    // Set branch list and populate ListView
    public void setBranches(List<Branch> branches) {
        this.branches = branches;
        List<String> branchNames = new ArrayList<>();
        for (Branch branch : branches) {
            branchNames.add(branch.getName());
        }
        branchListView.setItems(FXCollections.observableArrayList(branchNames));
    }

    // Set reference to PrimaryController
    public void setPrimaryController(PrimaryController primaryController) {
        this.primaryController = primaryController;
    }
}

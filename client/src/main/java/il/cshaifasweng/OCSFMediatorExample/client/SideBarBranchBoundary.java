package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class SideBarBranchBoundary {
    public Branch branch;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView MOMSImage;

    @FXML
    private Button complaintBtn;

    @FXML
    private Button deliveryBtn;

    @FXML
    private Button menuBtn;

    @FXML
    private Button reservationBtn;

    @FXML
    private VBox sideBar;

    @FXML
    private Button toggleButtonBranch;
    private Popup popup = new Popup();

    public List<Branch> branchList = null;
    public boolean branchListInit = false;
    private final Object lock = new Object();

    @FXML
    void navToComplaintPage(ActionEvent event) {
        switchScreen("Complaint");
    }

    @FXML
    void navToDeliveryPage(ActionEvent event) {
        switchScreen("Delivery");
    }

    @FXML
    void navToReservationPage(ActionEvent event) {
        switchScreen("Reservation");
    }

//    @FXML
//    void navToMenu(ActionEvent event) {switchScreen("Menu");}

    @FXML
    public void navToMenu(ActionEvent actionEvent) {
        switchScreen("menu");
        try {
            App.setRoot("menu");
            SimpleClient.getClient().displayBranchMenu(branch);
//            Menu menu = new Menu(branch.getBranchMenuItems());
//            menu.printMenu();
//            SimpleClient.getClient().showMenu(menu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// im not sure if needed...
//	@FXML
//	void displayMenuFunc(ActionEvent event) throws IOException {
//		switchScreen("Update Menu");
//		try {
//			//App.setRoot("updateMenu");
//			SimpleClient.getClient().displayNetworkMenu();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}


    @FXML
    void initialize() {
        assert MOMSImage != null : "fx:id=\"MOMSImage\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert complaintBtn != null : "fx:id=\"complaintBtn\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert deliveryBtn != null : "fx:id=\"deliveryBtn\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert menuBtn != null : "fx:id=\"menuBtn\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert reservationBtn != null : "fx:id=\"reservationBtn\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert sideBar != null : "fx:id=\"sideBar\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert toggleButtonBranch != null : "fx:id=\"toggleButtonBranch\" was not injected: check your FXML file 'sideBarBranch.fxml'.";


        // Set the button action here
        EventBus.getDefault().register(this);
        SimpleClient.getClient().getBranchList(); // Request branch list from server

        toggleButtonBranch.setOnAction(e -> {
            System.out.println("Button clicked - showing popup");
            GetBranchListPopup();
        });


        // This section display the image of mamasKitchen
        String imagePath = "il/cshaifasweng/OCSFMediatorExample/client/mamasKitchen.jpg";
        Image image = new Image(imagePath);
        MOMSImage.setImage(image);
    }


    // shir may - I duplicated those functions from the PrimaryBoundary
    // since I want the branch list to be also in the branch(es) page ( when clicking on the btn of " Our Branches")

    /// /////////////////////////////////////////////////////////////////////////////////////////////

    //get list of brunches pop up
    private void GetBranchListPopup() {
        synchronized (lock) {
            if (!branchListInit) {
                try {
                    SimpleClient.getClient().getBranchList();
                    while (!branchListInit) {
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread interrupted while waiting for branch list.");
                    return;
                }
            }
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
            Parent popupContent = loader.load();

            // Get BranchListController instance and set branches
            BranchListBoundary controller = loader.getController();
            controller.setBranches(branchList);

            popup.getContent().clear();
            popup.getContent().add(popupContent);
            popup.setAutoHide(true);
            // Ensure popup shows correctly
            if (toggleButtonBranch.getScene() != null) {
                popup.show(toggleButtonBranch.getScene().getWindow(),
                        toggleButtonBranch.localToScreen(0, 0).getX(),
                        toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight());
            } else {
                System.out.println("toggleButtonBranch scene is NULL - cannot display popup");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void getPopup(ActionEvent actionEvent) {
        System.out.println("getPopup");
        GetBranchListPopup();
    }

    // Handle the branch selected from the list
    @Subscribe
    public void onBranchSelectedEvent(BranchSelectedEvent event) {
        System.out.println("Branch selected: " + event.getBranch().getName());
        Branch branch = event.getBranch();
        if (branch == null) {
            System.out.println("branch is null");
        }
        openBranchPage(branch);
    }

    //open selected branch page
    private void openBranchPage(Branch branch) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
            Parent branchPageRoot = loader.load();
            // Get the controller and pass the branch
            BranchPageBoundary controller = loader.getController();
            controller.setBranch(branch);
            if (controller.branchIsSet) {
                System.out.println("branch is already set");
            }
            while (!controller.branchIsSet) {
                System.out.println("Waiting for branch to be set");
            }
            App.setContent(branchPageRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //handle branch list sent
    @Subscribe
    public void onBranchListSentEvent(BranchListSentEvent event) {
        synchronized (lock) {
            this.branchList = event.branches;
            this.branchListInit = true;
            System.out.println("onBranchesSentEvent");
            lock.notifyAll(); // Notify waiting threads that branches are initialized
        }
    }
/// /////////////////////////////////////////////////////////////////////////////////////////////

}
//	private void getReportsListPopup() {
//		synchronized (this) {
//			if (!reportsListInit) {
//				try {
//					SimpleClient.getClient().getReportsList(); // Request reports list from server again if not initialized
//					while (!reportsListInit) {
//						wait();  // Wait until the reports are fetched and initialized
//					}
//				} catch (InterruptedException e) {
//					Thread.currentThread().interrupt();
//					System.out.println("Thread interrupted while waiting for reports list.");
//					return;
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportsList.fxml"));
//			Parent popupContent = loader.load();
//
//			// Assuming ReportsListController sets the reports list similar to branches
//			ReportsListBoundary controller = loader.getController();
//			controller.setReports(reportList);
//
//			reportsPopup.getContent().clear();
//			reportsPopup.getContent().add(popupContent);
//			reportsPopup.setAutoHide(true);
//			if (toggleButtonReports.getScene() != null) {
//				reportsPopup.show(toggleButtonReports.getScene().getWindow(),
//						toggleButtonReports.localToScreen(0, 0).getX(),
//						toggleButtonReports.localToScreen(0, 0).getY() + toggleButtonReports.getHeight());
//			} else {
//				System.out.println("toggleButtonReports scene is NULL - cannot display popup");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}






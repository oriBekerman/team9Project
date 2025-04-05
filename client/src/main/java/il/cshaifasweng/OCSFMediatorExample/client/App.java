package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.WarningEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private SimpleClient client;
    private static Stage appStage;

    @Override
    public void start(Stage stage) throws IOException {
        appStage = stage;
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }

        client = SimpleClient.getClient();
        stage.setTitle("Client Connection");
        scene = new Scene(loadFXML("clientConnecting"));
        stage.setScene(scene);
        stage.show();

        //OLD LOGIC
        //        Scanner scanner = new Scanner(System.in);
//        System.out.println("Please enter host: ");
//        String host = scanner.nextLine();
//        System.out.println("Please enter port: ");
//        String port = scanner.nextLine();
//        int port2 = Integer.parseInt(port);
//    	client = SimpleClient.getClient();
//        client.setHost("localhost");//change later for two computer connection
//        client.setPort(3000);//change later for two computer connection
//    	client.openConnection();
//        System.out.println("try client add");
//
//        stage.setTitle("Team 9 - Mom's kitchen");
//        scene = new Scene(loadFXML("primary"), 1295, 782);
//        stage.setScene(scene);
//        stage.show();

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    

    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
    	EventBus.getDefault().unregister(this);
        client.sendToServer("remove client");
        client.closeConnection();
		super.stop();
	}
    
    @Subscribe
    public void onWarningEvent(WarningEvent event) {
    	Platform.runLater(() -> {
    		Alert alert = new Alert(AlertType.WARNING,
        			String.format("Message: %s\nTimestamp: %s\n",
        					event.getWarning().getMessage(),
        					event.getWarning().getTime().toString())
        	);
        	alert.show();
    	});
    	
    }

	public static void main(String[] args) {
        launch();
    }

    public static void setWindowTitle(String title) {
        appStage.setTitle(title);
    }
    public static void setContent(String pageName) throws IOException {
        Parent root = loadFXML(pageName);
        scene = new Scene(root);
        appStage.setScene(scene);
        appStage.show();
    }
    public static void setContent(Parent parent) throws IOException {
        System.out.println("[App] Setting root scene content...");
        scene = new Scene(parent);
        appStage.setScene(scene);
        appStage.show();
        System.out.println("[App] Scene content set successfully!");

    }
    public static void switchScreen (String screenName) {
        switch (screenName) {
            case "Home Page":
                Platform.runLater(() -> {
                    setWindowTitle("Home Page");
                    try {
                        setContent("primary");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "Branches":
                Platform.runLater(() -> {
                    setWindowTitle(" Our Branches");
                    try {
                        setContent("branches");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "Reservation":
                Platform.runLater(() -> {
                    setWindowTitle("Reservation");
                    try {
                        setContent("reservation");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "Personal Details Filling":
                Platform.runLater(() -> {
                    setWindowTitle("Personal Details Filling");
                    try {
                        setContent("personalDetailsFilling");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "Credit Card Info":
                Platform.runLater(() -> {
                    setWindowTitle("Credit Card Information");
                    try {
                        setContent("creditCardInfo");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "Login":
                Platform.runLater(() -> {
                    setWindowTitle("Login");
                    try {
                        setContent("login");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "secondary":
                Platform.runLater(() -> {
                    setWindowTitle("Update Menu");
                    try {
                        setContent("secondary");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;

            case "reservationCnt":
                Platform.runLater(() -> {
                    setWindowTitle("Reservation");
                    try {
                        setContent("reservationCnt");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;

            case "SubComplaint":
                Platform.runLater(() -> {
                    try {
                        setContent("SubComplaint");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "CancelDelivery":
                 Platform.runLater(() -> {
                    try {
                        setContent("cancelDelivery");
                   } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "enterEmail":
                Platform.runLater(() -> {
                    setWindowTitle("Enter Email");
                    try {
                        setContent("enterEmail");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "reservationList":
                Platform.runLater(() -> {
                    setWindowTitle("Your Reservations");
                    try {
                        setContent("reservationList");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "Complaints":
                Platform.runLater(() -> {
                    setWindowTitle("Complaints");
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("HandleCompTablePage.fxml"));
                        Parent root = loader.load();
                        HandleComplaintTableBoundary boundary = loader.getController();
                        boundary.setPage();  // fetch complaints & update table
                        setContent(root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;





        }
    }

    public static void switchToDelivery(Delivery delivery) {
        Platform.runLater(() -> {
            setWindowTitle("Delivery");
            try {
                // Load the FXML file for the delivery screen
                FXMLLoader loader = new FXMLLoader(App.class.getResource("delivery.fxml"));
                Parent root = loader.load();

                // Get the controller of the loaded FXML
                DeliveryBoundary deliveryBoundary = loader.getController();

                deliveryBoundary.setDelivery(delivery);
                deliveryBoundary.initialize();

                // Set the scene and show the stage
                scene = new Scene(root);
                appStage.setScene(scene);
                appStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToPDDelivery(Delivery delivery) {
        Platform.runLater(() -> {
            setWindowTitle("Personal Details Filling");
            try {
                // Load the FXML file for the delivery screen
                FXMLLoader loader = new FXMLLoader(App.class.getResource("personalDetailsFillingDelivery.fxml"));
                Parent root = loader.load();

                // Get the controller of the loaded FXML
                PersonalDetailsFillingDeliveryBoundary boundary = loader.getController();

                // Pass the branchId to the controller
                boundary.setDelivery(delivery);

                // Set the scene and show the stage
                scene = new Scene(root);
                appStage.setScene(scene);
                appStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToCCInfoDelivery(Delivery delivery) {
        Platform.runLater(() -> {
            setWindowTitle("Credit Card Information");
            try {
                // Load the FXML file for the delivery screen
                FXMLLoader loader = new FXMLLoader(App.class.getResource("creditCardInfoDelivery.fxml"));
                Parent root = loader.load();

                // Get the controller of the loaded FXML
                CreditCardInfoDeliveryBoundary boundary = loader.getController();

                // Pass the branchId to the controller
                boundary.setDelivery(delivery);

                // Set the scene and show the stage
                scene = new Scene(root);
                appStage.setScene(scene);
                appStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToSummeryDelivery(Delivery delivery) {
        Platform.runLater(() -> {
            setWindowTitle("Order Summery");
            try {
                // Load the FXML file for the delivery screen
                FXMLLoader loader = new FXMLLoader(App.class.getResource("deliverySummery.fxml"));
                Parent root = loader.load();

                // Get the controller of the loaded FXML
                DeliverySummeryBoundary boundary = loader.getController();

                // Pass the branchId to the controller
                boundary.setDelivery(delivery);

                // Set the scene and show the stage
                scene = new Scene(root);
                appStage.setScene(scene);
                appStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToBranchMenu(Branch branch) {
        Platform.runLater(() -> {
            setWindowTitle("secondary");
            try {
                // Load the FXML file for the secondary screen
                FXMLLoader loader = new FXMLLoader(App.class.getResource("secondary.fxml"));
                Parent root = loader.load();

                // Get the controller of the loaded FXML
                SecondaryBoundary secondaryBoundary = loader.getController();

                secondaryBoundary.setBranch(branch);
                secondaryBoundary.initialize();

                // Set the scene and show the stage
                scene = new Scene(root);
                appStage.setScene(scene);
                appStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
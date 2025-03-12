package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.WarningEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
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
    	EventBus.getDefault().register(this);
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Please enter host: ");
//        String host = scanner.nextLine();
//        System.out.println("Please enter port: ");
//        String port = scanner.nextLine();
//        int port2 = Integer.parseInt(port);
    	client = SimpleClient.getClient();
        client.setHost("localhost");//change later for two computer connection
        client.setPort(3000);//change later for two computer connection
    	client.openConnection();
        stage.setTitle("ProtoType Team 9 - Mom's kitchen");
        scene = new Scene(loadFXML("primary"), 1295, 782);
        stage.setScene(scene);
        stage.show();
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
        scene = new Scene(parent);
        appStage.setScene(scene);
        appStage.show();
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
            case "Delivery":
                Platform.runLater(() -> {
                    setWindowTitle("Delivery");
                    try {
                        setContent("delivery");
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
            case "LogOut":
                Platform.runLater(() -> {
                    setWindowTitle("LogOut");
                    try {
                        setContent("login");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "Menu":
                Platform.runLater(() -> {
                    setWindowTitle("Menu");
                    try {
                        setContent("menu");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "Complaint":
                Platform.runLater(() -> {
                    setWindowTitle("Complaint");
                    try {
                        setContent("complaint");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "Reports":
                Platform.runLater(() -> {
                    setWindowTitle("Reports");
                    try {
                        setContent("report");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "Update Menu":
                Platform.runLater(() -> {
                    setWindowTitle("Update Menu");
                    try {
                        setContent("updateMenu");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "New":
                Platform.runLater(() -> {
                    setWindowTitle("New");
                    try {
                        setContent("new");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
        }
    }
}
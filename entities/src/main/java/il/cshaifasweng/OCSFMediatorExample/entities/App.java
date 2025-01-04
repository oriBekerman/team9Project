package il.cshaifasweng.OCSFMediatorExample.entities;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;


import javafx.scene.layout.AnchorPane;

import org.hibernate.Session;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize Hibernate
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();

            // Insert sample data into the database
            DataInitializer.initializeData(session);

            // Load the main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuItemsView.fxml"));
            AnchorPane root = loader.load();

            primaryStage.setTitle("Menu Items");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

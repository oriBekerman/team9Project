package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.controllers.MenuItemsController;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;


import static il.cshaifasweng.OCSFMediatorExample.entities.Request.RequestType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.*;


public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    public static Session session;
    private Menu menu=new Menu();
    private MenuItemsController menuController=null;
    private String password="";
    private final DatabaseManager databaseManager=new DatabaseManager();
    public SimpleServer(int port) {
        super(port);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the database password: ");
        this.password = scanner.nextLine();
        System.out.println("after password ");
        DatabaseManager.initialize(password);//initialize DB and populate table if empty
        getControllers();
    }
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String msgString = msg.toString();
        Request request=(Request)msg;

        if (msgString.startsWith("add client")) {
            SubscribedClient connection = new SubscribedClient(client);
            SubscribersList.add(connection);
            try {
                client.sendToClient("client added successfully");
                System.out.println("Client added successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        //receives display menu msg from client and sends back menu
        else if (request.getRequestType().equals(DISPLAY_MENU))
        {
            System.out.println("in server display menu");
            menu=menuController.displayMenu();
            Response response=new Response(SUCCESS,menu, RETURN_MENU);
            try {
                client.sendToClient(response);
//                client.sendToClient(menu);//sent the menu
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //receives edit item msg from client and returns the updated item
        else if (msgString.contains("#edit item"))
        {
            System.out.println("in edit item");
            int itemId=0;
            double newPrice=0;
            String[] parts = msgString.split(",");
            try {
                itemId = Integer.parseInt(parts[1]);
                System.out.println(itemId);
                newPrice = Double.parseDouble(parts[2]);
            } catch (NumberFormatException e) {
                System.out.println("Error: One of the parts is not a valid integer.");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Error: The input string does not have enough parts.!");
            }
            System.out.println("before updateItemInDB");
            MenuItem item=menu.getItemByID(itemId-1); //id start in 1 when but index in 0
            item.printMenuItem();
            updateItemInDB(item,newPrice);
            System.out.println("after updateItemInDB");
            sendToAllClients((menu));//sent the menu to all the clients
        }
    }
    private void updateItemInDB(MenuItem item,double newPrice) {
        Session session = null;
        System.out.println("in updateItemInDB");
        try {
            SessionFactory sessionFactory = databaseManager.getSessionFactory(); //need to define session factory
            session = sessionFactory.openSession(); // have to do next two lines to make actions in the DB
            session.beginTransaction();
            //after begin transaction we can make actions in the database, when we finish we make commit

            // double the price of the item
            item.setPrice(newPrice);

            // Update the item in the database
            session.merge(item);

            // Force Hibernate to flush changes to the database
            session.flush();

            // Commit the transaction
            session.getTransaction().commit();

            System.out.println("Item with ID " + item.getItemID() + " updated successfully. New price: " + newPrice);
        } catch (Exception e) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    public void sendToAllClients(String message) {
        try {
            for (SubscribedClient subscribedClient : SubscribersList) {
                subscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    private void getControllers()
    {
        menuController=databaseManager.getMenuItemsController();
    }
}
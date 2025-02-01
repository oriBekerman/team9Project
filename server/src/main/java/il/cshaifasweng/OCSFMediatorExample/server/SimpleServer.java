package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.controllers.MenuItemsController;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;


import static il.cshaifasweng.OCSFMediatorExample.entities.Request.RequestType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.*;


public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    public static Session session;
    private Menu menu=new Menu();
    private MenuItemsController menuItemsController =null;
    public static String dataBasePassword="poolgirL1?";//change database password here
    private String password="";
    private final DatabaseManager databaseManager=new DatabaseManager(dataBasePassword);
    public SimpleServer(int port) {
        super(port);
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Please enter the database password: ");
//        this.password = scanner.nextLine();
//        System.out.println("after password ");
        DatabaseManager.initialize(dataBasePassword);//initialize DB and populate table if empty
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
            menu= menuItemsController.displayMenu();
            Response response=new Response(RETURN_MENU,menu,SUCCESS);
            try {
                client.sendToClient(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //receives edit item msg from client and returns the updated item
        else if(request.getRequestType().equals(UPDATE_PRICE))
        {
            MenuItem item=menuItemsController.updatePrice(request);
            Response response=new Response<>(UPDATED_PRICE,item,SUCCESS);
            sendToAllClients((response));//sent the item to all the clients
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
        menuItemsController =databaseManager.getMenuItemsController();
    }
}
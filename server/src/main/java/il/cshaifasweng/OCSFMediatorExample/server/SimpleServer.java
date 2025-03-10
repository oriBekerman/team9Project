package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.controllers.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import java.io.IOException;
import java.util.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.Session;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;


public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    public static Session session;

    //controllers
    private MenuItemsController menuItemsController =null;
    private BranchController branchController=null;
    private RestTableController restTableController=null;
    private LogInController logInController = null;

    private DeliveryController deliveryController = null;

    public static String dataBasePassword="Bekitnt26@";//change database password here
    public String password="";//used only when entering a new password through cmd
    private final DatabaseManager databaseManager=new DatabaseManager(dataBasePassword);
    public SimpleServer(int port) {
        super(port);
        //change to password and remove comments if we want to enter another database passwords
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Please enter the database password: ");
//        this.password = scanner.nextLine();
//        System.out.println("after password ");
//        DatabaseManager.initialize();// (if we want a different password to be entered when running change databasePassword-> password
        getControllers();
    }
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client){
        System.out.println("received request from client: ");

        String msgString = msg.toString();
        Request request=(Request)msg;

        //connect client
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
        //navigate client's request to the appropriate controller and sent the controller's response to the client
        Response response = switch (request.getCategory())
        {
            case BASE_MENU -> menuItemsController.handleRequest(request);
            case BRANCH -> branchController.handleRequest(request);
            case LOGIN -> logInController.handleRequest(request);
            case DELIVERY -> deliveryController.handleRequest(request);
            default -> throw new IllegalArgumentException("Unknown request category: " + request.getCategory());
        };


        System.out.println("Response prepared for client:");
        System.out.println("Response Type: " + response.getResponseType());
        System.out.println("Response Status: " + response.getStatus());
        System.out.println("Response Data: " + (response.getData() != null ? response.getData().toString() : "No data"));



        //check if the response should be sent to all clients or just one
        if (response.getRecipient()==ALL_CLIENTS) {
            sendToAllClients(response);
        }
        if (response.getRecipient()==THIS_CLIENT)
        {
            try {
                System.out.println("Sending response to client: " + response.getResponseType() + " with data: " + response.getData());
                client.sendToClient(response);
            } catch (Exception e)
            {
                throw new RuntimeException(e);
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
        this.menuItemsController =databaseManager.getMenuItemsController();
        this.branchController=databaseManager.getBranchController();
        this.logInController = databaseManager.getLogInController();
        this.restTableController = databaseManager.getRestTableController();
        this.deliveryController = databaseManager.getDeliveryController();
    }
}
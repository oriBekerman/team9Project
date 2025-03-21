package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.controllers.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import java.io.IOException;
import java.util.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.util.Pair;
import org.hibernate.Session;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;

public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    public static Session session;

    //controllers
    private MenuItemsController menuItemsController = null;
    private BranchController branchController = null;
    private RestTableController restTableController = null;
    private LogInController logInController = null;
    private DeliveryController deliveryController = null;
    private ResInfoController resInfoController = null;
    private ComplaintController complaintController = null;

    public static String dataBasePassword = "1234"; //change database password here
    public String password = ""; //used only when entering a new password through cmd
    private final DatabaseManager databaseManager = new DatabaseManager(dataBasePassword);

    public SimpleServer(int port) {
        super(port);
        getControllers();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("received request from client: " + msg.toString());
        String msgString = msg.toString();

        //connect client
        if (msgString.startsWith("add client")) {
            System.out.println("Client added successfully");
            SubscribedClient connection = new SubscribedClient(client);
            SubscribersList.add(connection);

            try {
                client.sendToClient("client added successfully");
                System.out.println("Client added successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Request request = (Request) msg;
        //navigate client's request to the appropriate controller and send the controller's response to the client
        Response response = switch (request.getCategory()) {
            case BASE_MENU -> menuItemsController.handleRequest(request);
            case BRANCH -> branchController.handleRequest(request);
            case LOGIN -> logInController.handleRequest(request);
            case DELIVERY -> deliveryController.handleRequest(request);
            case RESERVATION -> resInfoController.handleRequest(request);
            default -> throw new IllegalArgumentException("Unknown request category: " + request.getCategory());
        };

        System.out.println("Response prepared for client:");
        System.out.println("Response Type: " + response.getResponseType());
        System.out.println("Response Status: " + response.getStatus());
        System.out.println("Response Data: " + (response.getData() != null ? response.getData().toString() : "No data"));

        // Check if the client is still connected
        if (client == null || client.getInetAddress() == null) {
            System.err.println("Client socket appears to be closed. Skipping response.");
            return;
        }

        //check if the response should be sent to all clients or just one
        if (response.getRecipient() == ALL_CLIENTS) {
            sendToAllClients(response);
            System.out.println("response sent to client " + response.getResponseType().toString());
        }
        if (response.getRecipient() == ALL_CLIENTS_EXCEPT_SENDER) {
            sendToAllClientsExceptSender(response, client);
            System.out.println("response sent to client " + response.getResponseType().toString());
        }
        if (response.getRecipient() == THIS_CLIENT) {
            try {
                client.sendToClient(response);
                System.out.println("response sent to client: " + response.getResponseType() + " with data: " + response.getData());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //server needs to send one response to all clients and one to a specific client
        if (response.getRecipient() == BOTH) {
            List<Response> responses = (List<Response>) response.getData();
            if (responses.get(0).getRecipient() == ALL_CLIENTS) {
                try {
                    sendToAllClients(responses.get(0));
                    client.sendToClient(responses.get(1));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    sendToAllClients(responses.get(1));
                    client.sendToClient(responses.get(0));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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

    public void sendToAllClientsExceptSender(Object message, ConnectionToClient client) {
        synchronized (SubscribersList) {  // Ensures thread safety
            Iterator<SubscribedClient> iterator = SubscribersList.iterator();
            while (iterator.hasNext()) {
                SubscribedClient subscribedClient = iterator.next();
                try {
                    if (!subscribedClient.getClient().equals(client) && subscribedClient.getClient().getInetAddress() != null) {
                        subscribedClient.getClient().sendToClient(message);
                    }
                } catch (IOException e) {
                    System.err.println("Client disconnected. Removing from list.");
                    iterator.remove();  // Remove disconnected clients
                }
            }
        }
    }

    private void getControllers() {
        this.menuItemsController = databaseManager.getMenuItemsController();
        this.branchController = databaseManager.getBranchController();
        this.logInController = databaseManager.getLogInController();
        this.restTableController = databaseManager.getRestTableController();
        this.deliveryController = databaseManager.getDeliveryController();
        this.resInfoController = databaseManager.getResInfoController();
        this.complaintController = databaseManager.getComplaintController();
    }
}

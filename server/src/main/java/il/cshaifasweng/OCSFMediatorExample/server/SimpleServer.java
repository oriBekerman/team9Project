package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.controllers.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.util.Pair;
import org.hibernate.Session;
import java.io.IOException;
import java.util.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;

public class SimpleServer extends AbstractServer {
    private static final List<SubscribedClient> SubscribersList = Collections.synchronizedList(new ArrayList<>());
    public static Session session;

    // Controllers
    private MenuItemsController menuItemsController;
    private BranchController branchController;
    private RestTableController restTableController;
    private LogInController logInController;
    private DeliveryController deliveryController;
    private ResInfoController resInfoController;
    private ComplaintController complaintController;
    private static CustomerController customerController;
    private static OrderItemController orderItemController;

    public static String dataBasePassword = "poolgirL1?"; // Change database password here
    private final DatabaseManager databaseManager = new DatabaseManager(dataBasePassword);

    public SimpleServer(int port) {
        super(port);
        getControllers();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Received request from client: " + msg);

        if (msg instanceof String msgString && msgString.startsWith("add client")) {
            System.out.println("Client added successfully");
            SubscribedClient connection = new SubscribedClient(client);
            SubscribersList.add(connection);
            try {
                client.sendToClient("Client added successfully");
            } catch (IOException e) {
                System.err.println("Error sending client confirmation: " + e.getMessage());
            }
            return;
        }

        if (!(msg instanceof Request request)) {
            System.err.println("Invalid message received. Expected Request object.");
            return;
        }

        Response response;
        try {
            response = switch (request.getCategory()) {
                case BASE_MENU -> menuItemsController.handleRequest(request);
                case BRANCH -> branchController.handleRequest(request);
                case LOGIN -> logInController.handleRequest(request);
                case DELIVERY -> deliveryController.handleRequest(request);
                case RESERVATION -> resInfoController.handleRequest(request);
                case REMOVE_DISH -> menuItemsController.handleRequest(request);
                case UPDATE_INGREDIENTS -> menuItemsController.handleRequest(request);
                case UPDATE_DISH_TYPE -> menuItemsController.handleRequest(request);
                default -> throw new IllegalArgumentException("Unknown request category: " + request.getCategory());
            };
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            return;
        }

        System.out.println("Response prepared for client: " + response.getResponseType());
        sendResponseToClient(response, client);
    }

    private void sendResponseToClient(Response response, ConnectionToClient client) {
        try {
            switch (response.getRecipient()) {
                case ALL_CLIENTS -> {
                    sendToAllClients(response);
                    System.out.println("Response sent to all clients.");
                }
                case ALL_CLIENTS_EXCEPT_SENDER -> {
                    sendToAllClientsExceptSender(response, client);
                    System.out.println("Response sent to all clients except sender.");
                }
                case THIS_CLIENT -> {
                    client.sendToClient(response);
                    System.out.println("Response sent to client: " + response.getResponseType());
                }
                case BOTH -> {
                    List<Response> responses = (List<Response>) response.getData();
                    sendToAllClients(responses.get(0));
                    client.sendToClient(responses.get(1));
                }
                default -> System.err.println("Unknown response recipient: " + response.getRecipient());
            }
        } catch (IOException e) {
            System.err.println("Error sending response: " + e.getMessage());
        }
    }

    public void sendToAllClients(Object message) {
        synchronized (SubscribersList) {
            for (SubscribedClient subscribedClient : SubscribersList) {
                try {
                    subscribedClient.getClient().sendToClient(message);
                } catch (IOException e) {
                    System.err.println("Error sending message to client: " + e.getMessage());
                }
            }
        }
    }

    public void sendToAllClientsExceptSender(Object message, ConnectionToClient client) {
        synchronized (SubscribersList) {
            Iterator<SubscribedClient> iterator = SubscribersList.iterator();
            while (iterator.hasNext()) {
                SubscribedClient subscribedClient = iterator.next();
                try {
                    if (!subscribedClient.getClient().equals(client) && subscribedClient.getClient().getInetAddress() != null) {
                        subscribedClient.getClient().sendToClient(message);
                    }
                } catch (IOException e) {
                    System.err.println("Client disconnected. Removing from list.");
                    iterator.remove();
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
        this.customerController = databaseManager.getCustomerController();
        this.orderItemController = databaseManager.getOrderItemController();
    }

    public static CustomerController getCustomerController() {
        if(customerController==null){
            customerController=new CustomerController();
        }
        return customerController;
    }

    public static OrderItemController getOrderItemController() {
        if(orderItemController==null){
            orderItemController=new OrderItemController();
        }
        return orderItemController;
    }
}

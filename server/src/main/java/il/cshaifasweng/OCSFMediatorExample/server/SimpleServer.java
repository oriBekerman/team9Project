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
    public static String dataBasePassword = "Bekitnt26@"; // Change database password here
    private final DatabaseManager databaseManager = new DatabaseManager(dataBasePassword);

    public SimpleServer(int port)
    {
        super(port);
        getControllers();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client)
    {
        System.out.println("Received request from client: " + msg);

        if (msg instanceof String msgString && msgString.startsWith("add client"))
        {
            SubscribedClient connection = new SubscribedClient(client);
            SubscribersList.add(connection);
            try
            {
                client.sendToClient("Client added successfully");
            }
            catch (IOException e)
            {
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
                case COMPLAINT -> complaintController.handleRequest(request);
                case REMOVE_DISH -> menuItemsController.handleRequest(request);
                case UPDATE_INGREDIENTS -> menuItemsController.handleRequest(request);
                case UPDATE_DISH_TYPE -> menuItemsController.handleRequest(request);
                case PERMIT_GRANTED ->
                {
                    Response permitResponse = handlePermitGranted(request);
                    yield permitResponse;
                }

                case ADD_DISH ->
                {
                    Response addDishResponse = menuItemsController.handleRequest(request);
                    yield addDishResponse;
                }
                default -> throw new IllegalArgumentException("Unknown request category: " + request.getCategory());
            };
        }
        catch (Exception e)
        {
            System.err.println("Error processing request: " + e.getMessage());
            return;
        }
        sendResponseToClient(response, client);
        if(response.getMessage() !=null)
        {
            System.out.println("response msg =" +response.getMessage());
        }
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
                    if (responses.get(0).getRecipient().equals(THIS_CLIENT) && responses.get(1).getRecipient().equals(ALL_CLIENTS)) {
                        sendToAllClients(responses.get(1));
                        client.sendToClient(responses.get(0));
                    }
                    if (responses.get(0).getRecipient().equals(ALL_CLIENTS) && responses.get(1).getRecipient().equals(THIS_CLIENT)) {
                        sendToAllClients(responses.get(0));
                        client.sendToClient(responses.get(1));
                    }
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

    private Response handlePermitGranted(Request request)
    {
        Response response = new Response(Response.ResponseType.PERMIT_GRANTED_ACK,
                "Your permit request has been granted.",
                Response.Status.SUCCESS,
                Response.Recipient.ALL_CLIENTS);
        return response;
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
    }
}

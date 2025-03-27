package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.controllers.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.util.Pair;
import org.hibernate.Session;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;

public class SimpleServer extends AbstractServer {
    private static final List<SubscribedClient> SubscribersList = Collections.synchronizedList(new ArrayList<>());
    public static Session session;

    public static String host;

    public static int port;
    // Controllers
    private MenuItemsController menuItemsController;
    private BranchController branchController;
    private RestTableController restTableController;
    private LogInController logInController;
    private DeliveryController deliveryController;
    private ResInfoController resInfoController;
    private ComplaintController complaintController;
    public static String dataBasePassword = "poolgirL1?"; // Change database password here
    private final DatabaseManager databaseManager = new DatabaseManager(dataBasePassword);

    public SimpleServer(int port) throws UnknownHostException {
        super(port);
        //set host and port
        this.host = InetAddress.getLocalHost().getHostAddress(); //get the server host
        //FOR NOW ONLY USE LOCAL HOST---------REMOVE IN PRESENTATION
        this.host = "127.0.0.1";
        this.port = port;

        System.out.println("connected on host "+ this.host+" and port "+ this.port);
        getControllers();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Received request from client: " + msg);

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
                case RESERVATION -> {
                    if ("get_all_reservations".equals(request.getData())) {
                        response = resInfoController.getAllReservations();
                    } else {
                        response = resInfoController.handleRequest(request);
                    }
                    yield response;
                }

                case REMOVE_DISH -> menuItemsController.handleRequest(request);
                case UPDATE_INGREDIENTS -> menuItemsController.handleRequest(request);
                case UPDATE_DISH_TYPE -> menuItemsController.handleRequest(request);
                case CANCEL_RESERVATION -> resInfoController.cancelReservation(request);
                case COMPLAINT -> complaintController.handleRequest(request);

                // Handle permitGranted category
                case PERMIT_GRANTED ->
                {
                    System.out.println("Permit granted request received.");
                    // Process permitGranted request
                    Response permitResponse = handlePermitGranted(request);
                    yield permitResponse;
                }
                case CONNECTION -> addClient(request, client);

                default -> throw new IllegalArgumentException("Unknown request category: " + request.getCategory());
            };
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            return;
        }

        System.out.println("Response prepared for client: " + response.getResponseType());
        sendResponseToClient(response, client);
        if (response.getMessage() != null) {
            System.out.println("response msg =" + response.getMessage());
        }
    }

    private Response handlePermitGranted(Request request) {
        System.out.println("Handling permit granted message...");
        Response response = new Response(Response.ResponseType.PERMIT_GRANTED_ACK,
                "Your permit request has been granted.",
                Response.Status.SUCCESS,
                Response.Recipient.ALL_CLIENTS);

        // Return the response
        return response;
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

    private Response addClient(Request request, ConnectionToClient client) {
        System.out.println("Handling add client request...");
        String clientInfo = (String) request.getData();
        System.out.println("Client info received: " + clientInfo);

        // Extract host and port from clientInfo
        String[] parts = clientInfo.split(":");
        if (parts.length != 2) {
            return new Response(
                    Response.ResponseType.CLIENT_ADDED,
                    "Invalid client info format. Expected 'host:port'.",
                    Response.Status.ERROR,
                    Response.Recipient.THIS_CLIENT
            );
        }

        String clientHost = parts[0];
        int clientPort;

        try {
            clientPort = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return new Response(
                    Response.ResponseType.CLIENT_ADDED,
                    "Invalid port number.",
                    Response.Status.ERROR,
                    Response.Recipient.THIS_CLIENT
            );
        }

        // Normalize "localhost" to "127.0.0.1"
        if (clientHost.equalsIgnoreCase("localhost")) {
            clientHost = "127.0.0.1";
        }

        if (!clientHost.equals(this.host) || clientPort != this.port) {
            return new Response(
                    Response.ResponseType.CLIENT_ADDED,
                    "Connection rejected: Incorrect host or port.",
                    Response.Status.ERROR,
                    Response.Recipient.THIS_CLIENT
            );
        }

        // Add the client to the SubscribersList
        SubscribedClient connection = new SubscribedClient(client);
        SubscribersList.add(connection);

        Response response = new Response(
                Response.ResponseType.CLIENT_ADDED,
                "Client added successfully.",
                Response.Status.SUCCESS,
                Response.Recipient.THIS_CLIENT
        );

        try {
            // Send back a response confirming the client was added successfully
            client.sendToClient(response);
            System.out.println("Client added successfully: " + clientInfo);
            return response;
        } catch (IOException e) {
            System.err.println("Error sending client confirmation: " + e.getMessage());
            try {
                // Send failure response if adding client fails
                response.setStatus(Response.Status.ERROR);
                response.setMessage("Failed to add client.");
                client.sendToClient(response);
                return response;
            } catch (IOException ex) {
                System.err.println("Error sending failure response: " + ex.getMessage());
            }
        }
        return response;
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

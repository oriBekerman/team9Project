package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.controllers.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import java.io.IOException;
import java.util.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.reports.Report;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ComplaintRepository;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.DeliveryRepository;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.ResInfoRepository;
import javafx.util.Pair;
import org.hibernate.Session;
import java.io.IOException;
import java.util.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

import il.cshaifasweng.OCSFMediatorExample.server.reports.ReportFactory;

import il.cshaifasweng.OCSFMediatorExample.entities.Response;
import il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType;
import il.cshaifasweng.OCSFMediatorExample.entities.Response.Status;
import il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient;

public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
    private static final List<SubscribedClient> SubscribersList = Collections.synchronizedList(new ArrayList<>());
    public static Session session;

    //controllers
    private MenuItemsController menuItemsController =null;
    private BranchController branchController=null;
    private RestTableController restTableController=null;
    private LogInController logInController = null;
    private DeliveryController deliveryController = null;
    private ResInfoController resInfoController = null;
    private ComplaintController complaintController=null;
    public static String dataBasePassword="282817SMAY";//change database password here
    private final DatabaseManager databaseManager=new DatabaseManager(dataBasePassword);
    public SimpleServer(int port) {
        super(port);
       getControllers();
    }
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client){
        System.out.println("received request from client: ");

//        String msgString = msg.toString();
//        Request request=(Request)msg;
//
//        //connect client
//        if (msgString.startsWith("add client")) {
//            SubscribedClient connection = new SubscribedClient(client);
//            SubscribersList.add(connection);
//            try {
//                client.sendToClient("client added successfully");
//                System.out.println("Client added successfully");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        //navigate client's request to the appropriate controller and sent the controller's response to the client
//        Response response = switch (request.getCategory())
//        {
//            case BASE_MENU -> menuItemsController.handleRequest(request);
//            case BRANCH -> branchController.handleRequest(request);
//            case LOGIN -> logInController.handleRequest(request);
//            case DELIVERY -> deliveryController.handleRequest(request);
//            case REPORTS -> handleReportRequest(request);
//            default -> throw new IllegalArgumentException("Unknown request category: " + request.getCategory());
//        };
//
//        System.out.println("Response prepared for client:");
//        System.out.println("Response Type: " + response.getResponseType());
//        System.out.println("Response Status: " + response.getStatus());
//        System.out.println("Response Data: " + (response.getData() != null ? response.getData().toString() : "No data"));
//
//
//        //check if the response should be sent to all clients or just one
//        if (response.getRecipient()==ALL_CLIENTS) {
//            sendToAllClients(response);
//            System.out.println("response sent to client "+ response.getResponseType().toString());
//        }
//        if (response.getRecipient()==THIS_CLIENT)
//        {
//            try {
////                System.out.println("Sending response to client: " + response.getResponseType() + " with data: " + response.getData());
//                client.sendToClient(response);
//                System.out.println("response sent to client: " + response.getResponseType() + " with data: " + response.getData());
//            } catch (Exception e)
//            {
//                throw new RuntimeException(e);
//            }
//        }
//        //server needs to send one response to all clients and one to a specific client
//        //the response.data from the controller has both responses
//        if(response.getRecipient()==BOTH)
//        {
//            List<Response> responses= (List<Response>) response.getData();
//            if(responses.get(0).getRecipient()==ALL_CLIENTS)
//            {
//                try {
//                    sendToAllClients(responses.get(0));
//                    client.sendToClient(responses.get(1));
//                }
//                catch (Exception e)
//                {
//                    throw new RuntimeException(e);
//                }
//            }
//            else {
//                try {
//                    sendToAllClients(responses.get(1));
//                    client.sendToClient(responses.get(0));
//                }
//                catch (Exception e)
//                {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }


////////////////// //////////////// NEW FROM MANINN TO HANDELL
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
                    System.out.println("Permit granted request received.");
                    Response permitResponse = handlePermitGranted(request);
                    yield permitResponse;
                }

                case ADD_DISH -> {
                    Response addDishResponse = menuItemsController.handleRequest(request);
                    yield addDishResponse;
                }
                default -> throw new IllegalArgumentException("Unknown request category: " + request.getCategory());
            };
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            return;
        }

        System.out.println("Response prepared for client: " + response.getResponseType());
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
////////////////////////// NEW FROM MAIN  TO HANDELL





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


    private void getControllers()
    {
        this.menuItemsController =databaseManager.getMenuItemsController();
        this.branchController=databaseManager.getBranchController();
        this.logInController = databaseManager.getLogInController();
        this.restTableController = databaseManager.getRestTableController();
        this.deliveryController = databaseManager.getDeliveryController();
        this.resInfoController=databaseManager.getResInfoController();
        this.complaintController=databaseManager.getComplaintController();

    }


    private Response handleReportRequest(Request request) {
        String branchName = (String) request.getData();
        Branch branch = (Branch) branchController.getByName(branchName).getData();

        if (branch == null) {
            return new Response(ResponseType.RETURN_REPORT, "Branch not found", Status.ERROR, Recipient.THIS_CLIENT);
        }

        int branchId = branch.getId();
        Report report = ReportFactory.getReport(request.getRequestType(), branchId,
                new ResInfoRepository(), new DeliveryRepository(), new ComplaintRepository());
        report.fetchData();
        Object reportData = report.generateReportData();

        ResponseType responseType;
        switch (request.getRequestType()) {
            case GET_RES_REPORT -> responseType = ResponseType.RETURN_RES_REPORT;
            case GET_DELIV_REPORT -> responseType = ResponseType.RETURN_DELIV_REPORT;
            case GET_COMP_REPORT -> responseType = ResponseType.RETURN_COMP_REPORT;
            default -> throw new IllegalArgumentException("[SimpleServer- handleReportRequest] Unsupported request type: " + request.getRequestType());
        }

        return new Response(responseType, reportData, Status.SUCCESS, Recipient.THIS_CLIENT);
    }

}
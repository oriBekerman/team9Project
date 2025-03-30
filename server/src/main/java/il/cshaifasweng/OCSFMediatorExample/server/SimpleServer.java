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

    public static Session session;

    //controllers
    private MenuItemsController menuItemsController =null;
    private BranchController branchController=null;
    private RestTableController restTableController=null;
    private LogInController logInController = null;
    private DeliveryController deliveryController = null;
    private ResInfoController resInfoController = null;
    private ComplaintController complaintController=null;

    public static String dataBasePassword="282817SMAY ";//change database password here
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
        System.out.println("[SimpleServer] received request from client ");

        String msgString = msg.toString();
        Request request=(Request)msg;

        System.out.println("[SimpleServer] Request Type: " + request.getRequestType());
        System.out.println("[SimpleServer] Request Category: " + request.getCategory());


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
            case REPORTS -> handleReportRequest(request);
            default -> throw new IllegalArgumentException("Unknown request category: " + request.getCategory());
        };

        System.out.println("[SimpleServer -handleMessageFromClient ]Response prepared for client:");
        System.out.println("Response Type: " + response.getResponseType());
        System.out.println("Response Status: " + response.getStatus());
        System.out.println("Response Data: " + (response.getData() != null ? response.getData().toString() : "No data"));


        //check if the response should be sent to all clients or just one
        if (response.getRecipient()==ALL_CLIENTS) {
            sendToAllClients(response);
            System.out.println("response sent to client "+ response.getResponseType().toString());
        }
        if (response.getRecipient()==THIS_CLIENT)
        {
            try {
//                System.out.println("Sending response to client: " + response.getResponseType() + " with data: " + response.getData());
                client.sendToClient(response);
                System.out.println("response sent to client: " + response.getResponseType() + " with data: " + response.getData());
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        //server needs to send one response to all clients and one to a specific client
        //the response.data from the controller has both responses
        if(response.getRecipient()==BOTH)
        {
            List<Response> responses= (List<Response>) response.getData();
            if(responses.get(0).getRecipient()==ALL_CLIENTS)
            {
                try {
                    sendToAllClients(responses.get(0));
                    client.sendToClient(responses.get(1));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    sendToAllClients(responses.get(1));
                    client.sendToClient(responses.get(0));
                }
                catch (Exception e)
                {
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
        System.out.println("[SimpleServer - handleReportRequest] Received request for report: " + request.getRequestType());
        String branchName = (String) request.getData();
        Branch branch = (Branch) branchController.getByName(branchName).getData();

        if (branch == null) {
            System.out.println("[SimpleServer - handleReportRequest] ERROR: Branch '" + branchName + "' not found.");
            return new Response(ResponseType.RETURN_REPORT, "Branch not found", Status.ERROR, Recipient.THIS_CLIENT);
        }

        int branchId = branch.getId();
        System.out.println("[SimpleServer -handleReportRequest] Branch found. Generating report for branch ID: " + branchId);
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

        System.out.println("[SimpleServer- handleReportRequest] Before sending to client: the report data is " + reportData);
        return new Response(responseType, reportData, Status.SUCCESS, Recipient.THIS_CLIENT);
    }

}
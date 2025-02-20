package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.controllers.BranchController;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.LogInController;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.MenuItemsController;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;

import org.hibernate.Session;


import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;


public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    public static Session session;
//    private Menu menu=new Menu();
    private MenuItemsController menuItemsController =null;
//    private MenusController menusController=null;
    private BranchController branchController=null;
    public static String dataBasePassword="Bekitnt26@";//change database password here
    public String password="";//used only when entering a new password through cmd
    private LogInController logInController = null;
    private final DatabaseManager databaseManager=new DatabaseManager(dataBasePassword);
    public SimpleServer(int port) {
        super(port);
        //change to password and remove comments if we want to enter another database passwords
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Please enter the database password: ");
//        this.password = scanner.nextLine();
//        System.out.println("after password ");
        DatabaseManager.initialize(dataBasePassword);// (if we want aa different password to be entered when running change databasePassword-> password
        getControllers();
    }
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client){
        System.out.println("received request from client: ");
        String msgString = msg.toString();
        Request request=(Request)msg;
        if(request.getCategory()==BRANCH)
        {
            Response response=branchController.handleRequest(request);
            try {
                client.sendToClient(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(request.getCategory()==BASE_MENU)
        {
            System.out.println("server got menu request");
            Response response=menuItemsController.handleRequest(request);
            try {
                if(response.getRecipient()==ALL_CLIENTS)
                {
                    sendToAllClients(response);
                    System.out.println("server SENT TO all clients");
                }
                if (response.getRecipient()==THIS_CLIENT)
                {
                    client.sendToClient(response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
        //receives edit item msg from client and returns the updated item
//        else if(request.getRequestType().equals(UPDATE_PRICE)) {
//            MenuItem item = menuItemsController.updatePrice(request);
//            Response<MenuItem> response = new Response<>(UPDATED_PRICE, item, SUCCESS);
//            sendToAllClients((response));//sent the item to all the clients
//        }
        else if (request.getRequestType().equals(CHECK_USER)) {
            try {
                String data = (String) request.getData();
                String[] credentials = data.split(" ");
                String userName = credentials[0];
                String password = credentials[1];

                System.out.println("Checking login for: " + userName);

                String loginResult = logInController.verifyUser(userName, password);
                // Debug prints after verifyUser()
                System.out.println("Login result from controller: " + loginResult);

                Response<String> response;
                if (loginResult.equals("Login successful")) {
                    EmployeeType employeeType = logInController.getEmployeeTypeByUsername(userName);
                    response = new Response<>(CORRECTNESS_USER, userName + ":" + employeeType , SUCCESS,THIS_CLIENT);
                } else {
                    response = new Response<>(CORRECTNESS_USER, loginResult,ERROR,THIS_CLIENT);
                }

                System.out.println("Preparing to send response with message: " + response.getMessage());
                client.sendToClient(response);
                System.out.println("Response sent successfully.");

            } catch (Exception e) {
                System.err.println("Exception in CHECK_USER handling: " + e.getMessage());
                e.printStackTrace();
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
        logInController = databaseManager.getLogInController();
    }
}
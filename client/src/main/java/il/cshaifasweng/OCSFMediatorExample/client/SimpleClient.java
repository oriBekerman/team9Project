package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import javafx.application.Platform;
import javafx.util.Pair;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.*;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;


public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private static MenuEvent pendingMenuEvent = null;  // Store pending MenuEvent if SecondaryController isn't ready
	private static boolean isSecondaryControllerInitialized = false;
	public static String host="localhost";
	public  static int port=3000;
	private static ActiveUser activeUser = null;
	public Map <String, String> mapReservation=new HashMap<String, String>();

	private SimpleClient(String host, int port) {
		super(host, port);

	}
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(host, port);
		}
		return client;
	}
	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("Message received from server: " + msg);
		if (msg instanceof Response) {
			Response response = (Response) msg;

			// Print the response type
			System.out.println("ResponseType: " + response.getResponseType());

			// Print the status
			System.out.println("Status: " + response.getStatus());

			if (msg.getClass().equals(Warning.class)) {
				String message = msg.toString();
				System.out.println(message);
				EventBus.getDefault().post(new WarningEvent((Warning) msg));
			}
			// Safe cast
			if (response.getResponseType().equals(RETURN_MENU)) {
					Menu menu = (Menu) response.getData();
					MenuEvent menuEvent = new MenuEvent(menu);
					EventBus.getDefault().post(menuEvent);
				}
			if (response.getResponseType().equals(RETURN_BRANCH_MENU)) {
				System.out.println("Menu received, storing event...");
				Menu menu = (Menu) response.getData();
				MenuEvent menuEvent = new MenuEvent(menu);
				EventBus.getDefault().post(menuEvent);
			}
			if (response.getResponseType().equals(UPDATED_PRICE)) {
				MenuItem menuItem = (MenuItem) response.getData();
				// Post immediately if SecondaryController is ready
				updateDishEvent updateEvent = new updateDishEvent(menuItem);
				EventBus.getDefault().post(updateEvent);
			}
			//list of branches sent from server
			if (response.getResponseType().equals(BRANCHES_SENT)) {
				try {
					System.out.println("client got branches sent");
					//noinspection unchecked
					List<Branch> branches = (List<Branch>) response.getData();
					BranchListSentEvent branchSentEvent = new BranchListSentEvent(branches);
					Platform.runLater(() -> {
						EventBus.getDefault().post(branchSentEvent);
					});

				}
				catch (ClassCastException e) {
					e.printStackTrace();
				}
			}
			if(response.getResponseType().equals(RETURN_DELIVERABLES))
			{
				List<MenuItem> deliverables = (ArrayList<MenuItem>) response.getData();
				for (MenuItem item : deliverables) {
					item.printMenuItem();
				}
			}
			if (response.getResponseType().equals(RETURN_BRANCH_TABLES))
			{
				System.out.println("branch tables received from server");
				Set<RestTable> tables = new HashSet<>((Collection) response.getData());
				EventBus.getDefault().post(new BranchTablesReceivedEvent(tables));
				System.out.println("branch tables posted");
//				for (RestTable table : tables) {
//					table.print();
//				}
			}
			// Handle user authentication response
			if (response.getResponseType().equals(CORRECTNESS_USER)) {
				System.out.println("Handling CORRECTNESS_USER response with status: " + response.getStatus());

				if (response.getStatus() == SUCCESS) {
					String responseData = response.getMessage();
					System.out.println("Response Data: " + responseData);
					String[] parts = responseData.split(":");
					if (parts.length > 1) {
						String username = parts[0];
						String role = parts[1];
						// Set the active user
						SimpleClient.setActiveUser(new ActiveUser(username, EmployeeType.valueOf(role)));
						//post to eventbus
						EventBus.getDefault().post(new UserLoginSuccessEvent(username, role));
					} else {
						System.out.println("Error: Response doesn't contain both username and role.");
					}
				} else {
					String message = (String) response.getMessage();
					System.out.println("Login failed with message: " + message);
					EventBus.getDefault().post(new UserLoginFailedEvent(message != null ? message : "Unknown error"));
				}
			}
			 if (response.getResponseType().equals(SEND_DELIVERY)) {
				Delivery delivery = (Delivery) response.getData();
				if (delivery != null) {
					System.out.println(delivery);
				} else {
					System.out.println("No delivery data received.");
				}
			}
			if (response.getResponseType().equals(RETURN_BRANCH)) {
				System.out.println("hereeeeeeeeeeeeeeeeeeeeeeeee");
				Branch branch= (Branch) response.getData();
			EventBus.getDefault().post(new BranchSelectedEvent(branch));
			}

			if (response.getResponseType().equals(UPDATE_BRANCH_RESERVATION)) {
				System.out.println("updateRES!!!!!");
				Branch branch = (Branch) response.getData();
				EventBus.getDefault().removeStickyEvent(UpdateBranchResEvent.class); // Remove old events
				EventBus.getDefault().post(new UpdateBranchResEvent(branch));
			}
			if(response.getResponseType().equals(ADDED_RESERVATION))
			{
				ReservationAddedEvent event=new ReservationAddedEvent((ResInfo) response.getData(),response.getMessage());
				EventBus.getDefault().post(event);
			}
		} else {
			System.out.println("Received message is not of type Response");
		}
	}

	//called by SecondaryController to notify when it is initialized
	public static void setSecondaryControllerInitialized() {
		isSecondaryControllerInitialized = true;

		// Re-post the pending event if there's any
		if (pendingMenuEvent != null) {
			EventBus.getDefault().post(pendingMenuEvent);
			pendingMenuEvent = null;  // Clear the pending event
		}
	}

	public void editMenu(String itemId,String price) throws IOException
	{
		String[] data={itemId,price};
		Request<String[]> request= new Request<>(BASE_MENU,UPDATE_PRICE,data);
		client.sendToServer(request);
	}
	public void getBranchList(){
		Request request=new Request(BRANCH,GET_BRANCHES,null);
        try {
            client.sendToServer(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("getBranchList requested");
	}
	public void displayNetworkMenu() throws IOException {
		Request<Object> request=new Request<>(BASE_MENU,GET_BASE_MENU,null	);
		client.sendToServer(request);
		System.out.println("menu base req sent");
	}
	public void displayBranchMenu(Branch branch) throws IOException {
		Request<Branch> request= new Request<>(BRANCH,GET_BRANCH_MENU,branch);
		client.sendToServer(request);
	}
	public static ActiveUser getActiveUser() {
		return activeUser;
	}
	public static void setActiveUser(ActiveUser activeUser) {
		SimpleClient.activeUser = activeUser;
	}
	private static void clearActiveUser() {
		activeUser = null;
	}
	public static void logout() {
		clearActiveUser();  // Clear active user in SimpleClient
	}
	public void fetchTables(Branch branch) throws IOException {
		Request request=new Request(BRANCH,FETCH_BRANCH_TABLES,branch);
		System.out.println("fetch sent to server");
		client.sendToServer(request);
	}
	public void submitComplaint(List<String> customerDetails,Complaint complaint) throws IOException
	{
		Pair<Complaint,List<String>> pair=new Pair<>(complaint, customerDetails);
		Request request=new Request(COMPLAINT,SUBMIT_COMPLAINT,pair);
		try {
			sendToServer(request);
			System.out.println("complaint sent to server");
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	
}


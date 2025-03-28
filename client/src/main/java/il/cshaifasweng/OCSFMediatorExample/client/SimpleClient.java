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
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;
import il.cshaifasweng.OCSFMediatorExample.entities.Response;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private static MenuEvent pendingMenuEvent = null;  // Store pending MenuEvent if SecondaryController isn't ready
	private static boolean isSecondaryControllerInitialized = false;
	public static String host = "localhost";
	public static int port = 3000;
	private static ActiveUser activeUser = null;
	public Map<String, String> mapReservation = new HashMap<String, String>();
	public ResInfo resInfo = new ResInfo();
	public boolean rebookReservation = false;
	public boolean tableAvailable = true;

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

			// Handle the permitGranted message from the server (response)
			if (response.getResponseType() == Response.ResponseType.PERMIT_GRANTED_ACK) {

				// Optionally, you can log or show a message on the client to confirm
				System.out.println("Permit granted");
			}

			// Handle Warning response
			if (msg.getClass().equals(Warning.class)) {
				String message = msg.toString();
				System.out.println(message);
				EventBus.getDefault().post(new WarningEvent((Warning) msg));
			}

			// Handle RETURN_MENU response
			if (response.getResponseType().equals(RETURN_MENU)) {
				Menu menu = (Menu) response.getData();
				MenuEvent menuEvent = new MenuEvent(menu);
				EventBus.getDefault().post(menuEvent);
			}


			if (response.getResponseType() == Response.ResponseType.PERMIT_GRANTED_ACK
					&& response.getStatus() == Response.Status.SUCCESS)
			{

				System.out.println("Posting AcknowledgmentEvent");
				EventBus.getDefault().post(new AcknowledgmentEvent());
				System.out.println("AcknowledgmentEvent posted!");
			}



			// Handle RETURN_BRANCH_MENU response
			if (response.getResponseType().equals(RETURN_BRANCH_MENU)) {
				System.out.println("Menu received, storing event...");
				Menu menu = (Menu) response.getData();
				MenuEvent menuEvent = new MenuEvent(menu);
				EventBus.getDefault().post(menuEvent);
			}

			// Handle UPDATED_PRICE response
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

			// Handle RETURN_DELIVERABLES response
			if (response.getResponseType().equals(RETURN_DELIVERABLES)) {
				List<MenuItem> deliverables = (ArrayList<MenuItem>) response.getData();
				for (MenuItem item : deliverables) {
					item.printMenuItem();
				}
			}

			// Handle RETURN_BRANCH_TABLES response
			if (response.getResponseType().equals(RETURN_BRANCH_TABLES)) {
				System.out.println("branch tables received from server");
				Set<RestTable> tables = new HashSet<>((Collection) response.getData());
				EventBus.getDefault().post(new BranchTablesReceivedEvent(tables));
				System.out.println("branch tables posted");
			}

			// Handle user authentication response (CORRECTNESS_USER)
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
						// Post to eventbus
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

			// Handle DELIVERY_CREATED response
			if (response.getResponseType().equals(DELIVERY_CREATED)) {
				Delivery delivery = (Delivery) response.getData();
				if (delivery != null) {
					System.out.println(delivery);
					EventBus.getDefault().post(delivery);
				} else {
					System.out.println("No delivery data received.");
				}
			}

			// Handle SEND_DELIVERY response
			if (response.getResponseType().equals(SEND_DELIVERY)) {
				Delivery delivery = (Delivery) response.getData();
				if (delivery != null) {
					System.out.println(delivery);
					EventBus.getDefault().post(delivery);
				} else {
					System.out.println("No delivery data received.");
					EventBus.getDefault().post("delivery not found");

				}
			}

			// Handle DELIVERY_CANCELED response
			if (response.getResponseType().equals(DELIVERY_CANCELED)) {
				EventBus.getDefault().post("delivery deleted");
			}

			// Handle RETURN_BRANCH_BY_NAME response
			if (response.getResponseType().equals(RETURN_BRANCH_BY_NAME)) {
				Branch branch= (Branch) response.getData();
				EventBus.getDefault().post(new BranchSentEvent(branch));
			}
			// Handle ADDED_RESERVATION response
			if (response.getResponseType().equals(ADDED_RESERVATION)) {
				if (response.getStatus().equals(SUCCESS)) {
					System.out.println("in reservation succsess");
					ReservationAddedEvent event = new ReservationAddedEvent((ResInfo) response.getData(), response.getMessage());
					EventBus.getDefault().post(event);
				}
				if (response.getStatus().equals(ERROR)) {
					System.out.println("in error res");
					TableIsReservedEvent event = new TableIsReservedEvent((List<ResInfo>) response.getData());
					System.out.println("event created");
					EventBus.getDefault().post(event);
					System.out.println("event posted");
				}
			}

			// Handle UPDATE_BRANCH_TABLES response
			if (response.getResponseType().equals(UPDATE_BRANCH_TABLES)) {
				System.out.println("in updateBRANCH_TABLES");
//				ResInfo resInfo1= (ResInfo) response.getData();
//				Branch branch= (Branch) response.getData();
				UpdateBranchTablesEvent event = new UpdateBranchTablesEvent((ResInfo) response.getData());
				EventBus.getDefault().post(event);
			}
			//COMPLAINT
			if (response.getResponseType().equals(COMPLAINT_CREATED)) {
				System.out.println("in complaint created");

				// Check if complaint is null
				Complaint complaint = (Complaint) response.getData();
				if (complaint == null) {
					System.err.println("Error: Complaint object is null in response.");
					return;
				}

				System.out.println("Complaint successfully created: " + complaint);

				// Post the event with the complaint data to the EventBus
				EventBus.getDefault().post(new ComplaintCreatedEvent(complaint));
			}
			if(response.getResponseType().equals(RETURN_ACTIVE_RESERVATIONS))
			{
				System.out.println("in  RETURN_ACTIVE_RESERVATIONS is");
				if(response.getStatus().equals(SUCCESS))
				{
					System.out.println("in  RETURN_ACTIVE_RESERVATIONS succsess");
					SentActiveReservationsEvent event=new SentActiveReservationsEvent((List<ResInfo>) response.getData());
					EventBus.getDefault().post(event);
				}
				else
				{
					System.out.println("no reservations found");
				}
			}
			// Handle cancel resv response
			if (response.getResponseType().equals(CANCELED_RESERVATION)) {
				System.out.println("in CANCELED_RESERVATION");
				String message= (String) response.getMessage();
				ReservationCancelledEvent event = new ReservationCancelledEvent(message);
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

	public void editMenu(String itemId, String price) throws IOException {
		String[] data = {itemId, price};
		Request<String[]> request = new Request<>(BASE_MENU, UPDATE_PRICE, data);
		client.sendToServer(request);
	}

	public void getBranchList() {
		Request request = new Request(BRANCH, GET_BRANCHES, null);
		try {
			client.sendToServer(request);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("getBranchList requested");
	}

	public void displayNetworkMenu() throws IOException {
		Request<Object> request = new Request<>(BASE_MENU, GET_BASE_MENU, null);
		client.sendToServer(request);
		System.out.println("menu base req sent");
	}

	public void displayBranchMenu(Branch branch) throws IOException {
		Request<Branch> request = new Request<>(BRANCH, GET_BRANCH_MENU, branch);
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
		Request request = new Request(BRANCH, FETCH_BRANCH_TABLES, branch);
		System.out.println("fetch sent to server");
		client.sendToServer(request);
	}

	public void submitComplaint(List<String> customerDetails, Complaint complaint) throws IOException {
		Pair<Complaint, List<String>> pair = new Pair<>(complaint, customerDetails);
		Request request = new Request(COMPLAINT, SUBMIT_COMPLAINT, pair);
		try {
			sendToServer(request);
			System.out.println("complaint sent to server");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void removeDishFromDatabase(MenuItem dishToRemove) {
		// Assuming the category for removing a dish is BASE_MENU (similar to addDishToDatabase)
		Request<MenuItem> request = new Request<>(ReqCategory.BASE_MENU, RequestType.REMOVE_DISH, dishToRemove);
		try {
			SimpleClient.getClient().sendToServer(request);
			System.out.println("Dish removed from database: " + dishToRemove.getName());
		} catch (IOException e) {
			System.out.println("Error removing dish from database: " + e.getMessage());
		}
	}

	public void updateDishIngredients(MenuItem item) {
		try {
			Request<MenuItem> request = new Request<>(ReqCategory.BASE_MENU, RequestType.UPDATE_INGREDIENTS, item);
			getClient().sendToServer(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateDishType(MenuItem selectedItem) {
		try {
			// Get the item ID using getItemID() instead of getId()
			int itemId = selectedItem.getItemID();

			// Create a request with the selected item and the necessary dish type update
			Request<MenuItem> request = new Request<>(ReqCategory.BASE_MENU, RequestType.UPDATE_DISH_TYPE, selectedItem);

			// Send the request to the server
			getClient().sendToServer(request);

			// Print confirmation message with item ID
			System.out.println("Dish type updated for dish ID: " + itemId);
		} catch (IOException e) {
			// Handle any IOException that occurs while communicating with the server
			e.printStackTrace();
			System.err.println("Error updating dish type: " + e.getMessage());
		}
	}

	public void addDishToDatabase(MenuItem newDish) {
		// Assuming you have a way to send requests to the server:
		Request<MenuItem> request = new Request<>(ReqCategory.BASE_MENU, RequestType.ADD_DISH, newDish);
		try {
			SimpleClient.getClient().sendToServer(request);
			System.out.println("Dish added to database: " + newDish.getName());
		} catch (IOException e) {
			System.out.println("Error adding dish to database: " + e.getMessage());
		}
	}

}


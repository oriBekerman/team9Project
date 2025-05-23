package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import javafx.application.Platform;
import javafx.util.Pair;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.*;
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
	public String userEmail;
	private Response<?> lastResponse;

	public Response<?> getResponse() {
		return lastResponse;
	}

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
		if (msg instanceof Response) {
			Response response = (Response) msg;
			System.out.println("ResponseType: " + response.getResponseType());
			System.out.println("Status: " + response.getStatus());

			if (response.getResponseType() == Response.ResponseType.PERMIT_GRANTED_ACK
					&& response.getStatus() == Response.Status.SUCCESS) {
				EventBus.getDefault().post(new AcknowledgmentEvent());
			}

			if (response.getResponseType().equals(Response.ResponseType.CLIENT_ADDED))
			{
				if (response.getStatus() == Response.Status.SUCCESS)
				{

					System.out.println("Client added successfully.");

					EventBus.getDefault().post(new ClientAddedEvent("Client added successfully"));

				}
				else
				{
					String errorMessage = response.getMessage();
					System.out.println("Failed to add client: " + errorMessage);
					EventBus.getDefault().post(new ClientAddedEvent(errorMessage != null ? errorMessage : "Unknown error"));
				}
			}

			if (response.getResponseType().equals(Response.ResponseType.GET_LATEST_MENU_ITEM_ID))
			{
				Integer latestId = (Integer) response.getData();

				if (latestId != null)
				{
					try
					{
						SimpleClient.getClient().updateBranchBaseItem(1, latestId);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}

			if (response.getResponseType().equals(Response.ResponseType.REMOVE_DISH))
			{
				MenuItem removedMenuItem = (MenuItem) response.getData();
				updateDishEvent removeEvent = new updateDishEvent(removedMenuItem);
				EventBus.getDefault().post(removeEvent);
			}

			if (response.getResponseType().equals(Response.ResponseType.UPDATE_INGREDIENTS))
			{
				MenuItem updatedMenuItem = (MenuItem) response.getData();
				updateDishEvent updateIngredientsEvent = new updateDishEvent(updatedMenuItem);
				EventBus.getDefault().post(updateIngredientsEvent);
			}

			if (msg.getClass().equals(Warning.class))
			{
				String message = msg.toString();
				System.out.println(message);
				EventBus.getDefault().post(new WarningEvent((Warning) msg));
			}

			if (response.getResponseType().equals(RETURN_MENU))
			{
				Menu menu = (Menu) response.getData();
				MenuEvent menuEvent = new MenuEvent(menu);
				EventBus.getDefault().post(menuEvent);
			}
			if (response.getResponseType().equals(RETURN_BRANCH_MENU))
			{
				System.out.println("Menu received, storing event...");
				Menu menu = (Menu) response.getData();
				MenuEvent menuEvent = new MenuEvent(menu);
				EventBus.getDefault().post(menuEvent);
			}

			if (response.getResponseType().equals(UPDATED_PRICE))
			{
				MenuItem menuItem = (MenuItem) response.getData();
				updateDishEvent updateEvent = new updateDishEvent(menuItem);
				EventBus.getDefault().post(updateEvent);
			}

			if (response.getResponseType().equals(BRANCHES_SENT))
			{
				try {
					List<Branch> branches = (List<Branch>) response.getData();
					BranchListSentEvent branchSentEvent = new BranchListSentEvent(branches);
					Platform.runLater(() -> {
						EventBus.getDefault().post(branchSentEvent);
					});
				} catch (ClassCastException e) {
					e.printStackTrace();
				}
			}

			if (response.getResponseType().equals(RETURN_DELIVERABLES)) {
				System.out.println("in return deliverables");
				List<MenuItem> dlivMenuItems = (List<MenuItem>) response.getData();
				Menu menudeliv = new Menu(dlivMenuItems);
				menudeliv.printMenu();
				MenuEvent menuEvent = new MenuEvent(menudeliv);
				EventBus.getDefault().post(menuEvent);
			}

			if (response.getResponseType().equals(Response.ResponseType.ADD_DISH)) {
				MenuItem addedMenuItem = (MenuItem) response.getData();
				AddDishEvent addEvent = new AddDishEvent(addedMenuItem);
				System.out.println("[Client] Posting AddDishEvent for item: " + addedMenuItem.getName());

				// Only post once
				EventBus.getDefault().post(addEvent);
			}


			if (response.getResponseType().equals(RETURN_BRANCH_TABLES)) {
				Set<RestTable> tables = new HashSet<>((Collection) response.getData());
				EventBus.getDefault().post(new BranchTablesReceivedEvent(tables));
			}

			// Handle user authentication response (CORRECTNESS_USER)
			if (response.getResponseType().equals(CORRECTNESS_USER)) {
				if (response.getStatus() == SUCCESS) {
					String responseData = response.getMessage();
					String[] parts = responseData.split(":");
					if (parts.length > 1) {
						String username = parts[0];
						String role = parts[1];

						SimpleClient.setActiveUser(new ActiveUser(username, EmployeeType.valueOf(role)));

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

			if (response.getResponseType().equals(DELIVERY_CANCELED)) {
				EventBus.getDefault().post("delivery deleted");
			}

			if (response.getResponseType().equals(RETURN_BRANCH_BY_NAME)) {
				Branch branch = (Branch) response.getData();
				EventBus.getDefault().post(new BranchSentEvent(branch));
			}
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

			// Handle UPDATE_BRANCH_RESERVATION response
			if (response.getResponseType().equals(UPDATE_BRANCH_RESERVATION)) {
				System.out.println("updateRES!!!!!");
				Branch branch = (Branch) response.getData();
				EventBus.getDefault().removeStickyEvent(UpdateBranchResEvent.class); // Remove old events
				EventBus.getDefault().post(new UpdateBranchResEvent(branch));
			}


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

			if (response.getResponseType().equals(UPDATE_BRANCH_TABLES)) {
				System.out.println("in updateBRANCH_TABLES");
				UpdateBranchTablesEvent event = new UpdateBranchTablesEvent((ResInfo) response.getData());
				EventBus.getDefault().post(event);
			}
			if (response.getResponseType().equals(Response.ResponseType.UPDATE_DISH_TYPE_RES))
			{
				MenuItem menuItem = (MenuItem) response.getData();
				updateDishEvent updateEvent = new updateDishEvent(menuItem);
				EventBus.getDefault().post(updateEvent);
			}

			if (response.getResponseType().equals(RETURN_ALL_COMPLAINTS)) {
				ReceivedAllComplaintsEvent event = new ReceivedAllComplaintsEvent((List<Complaint>) response.getData());
				EventBus.getDefault().post(event);
			}
			if (response.getResponseType().equals(RETURN_ACTIVE_RESERVATIONS)) {
				if (response.getStatus().equals(SUCCESS)) {
					SentActiveReservationsEvent event = new SentActiveReservationsEvent((List<ResInfo>) response.getData());
					EventBus.getDefault().post(event);
				} else {
					System.out.println("no reservations found");
				}
			}

			if (response.getResponseType().equals(CANCELED_RESERVATION)) {
				System.out.println("in CANCELED_RESERVATION");
				String message = (String) response.getMessage();
				ReservationCancelledEvent event = new ReservationCancelledEvent(message);
				EventBus.getDefault().post(event);
			}
			// Insert this specific debug print for RETURN_REPORT responses:
			if (response.getResponseType().equals(RETURN_REPORT)) {
				System.out.println("[Client] Report data received: " + response.getData());
			}
			switch (response.getResponseType()) {
				case RETURN_COMP_REPORT -> {
					System.out.println("[SimpleClient] Complaints report received, posting event...");
					EventBus.getDefault().post(new ReportReceivedEvent(response.getData(), "Complaints"));
				}

				case RETURN_RES_REPORT -> {
					List<ResInfo> reservations = (List<ResInfo>) response.getData();
					System.out.println("[SimpleClient] Reservations deserialized: " + reservations);
					System.out.println("[SimpleClient] Reservations report received, posting event...");
					EventBus.getDefault().post(new ReportReceivedEvent(response.getData(), "Reservations"));
				}
				case RETURN_DELIV_REPORT -> {
					System.out.println("[SimpleClient] Deliveries report received, posting event...");
					EventBus.getDefault().post(new ReportReceivedEvent(response.getData(), "Deliveries"));
				}
			}
		} else {
			System.out.println("Received message is not of type Response");
			System.out.println("msg=" + msg);
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
		System.out.println("[SimpleClient - getBranchList] Requesting branch list from server...");

		Request request = new Request(BRANCH, GET_BRANCHES, null);
		try {
			client.sendToServer(request);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateBranchSpecialItem(int branchId, int menuItemId) throws IOException {

		UpdateBranchSpecialItemRequest data = new UpdateBranchSpecialItemRequest(branchId, menuItemId);
		Request<UpdateBranchSpecialItemRequest> request = new Request<>(ReqCategory.BRANCH, RequestType.UPDATE_BRANCH_SPECIAL_ITEM, data);
		client.sendToServer(request);
	}

	public void updateBranchBaseItem(int branchId, int menuItemId) throws IOException {

		UpdateBranchSpecialItemRequest data = new UpdateBranchSpecialItemRequest(branchId, menuItemId);
		Request<UpdateBranchSpecialItemRequest> request = new Request<>(ReqCategory.BRANCH, RequestType.UPDATE_BRANCH_BASE_ITEM, data);
		client.sendToServer(request);
	}

	public void getLatestMenuItemId() throws IOException {
		Request request = new Request<>(BASE_MENU,RequestType.GET_LATEST_MENU_ITEM_ID, null);
		sendToServer(request);
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

	public void displayDeliveryMenu(Branch branch) throws IOException
	{
		Request<Branch> request = new Request<>(BRANCH, GET_DELIVERABLES, branch);
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

	public void fetchTables(Branch branch) throws IOException
	{
		Request request = new Request(BRANCH, FETCH_BRANCH_TABLES, branch);
		client.sendToServer(request);
	}

	public void removeDishFromDatabase(MenuItem dishToRemove) {

		Request<MenuItem> request = new Request<>(ReqCategory.BASE_MENU, RequestType.REMOVE_DISH, dishToRemove);
		try {
			SimpleClient.getClient().sendToServer(request);
		}
		catch (IOException e)
		{
			System.out.println("Error sending dish removal request: " + e.getMessage());
		}
	}
	public void addDishToDatabase(MenuItem newDish, List<Branch> allBranches)
	{
		Request<MenuItem> request = new Request<>(ReqCategory.BASE_MENU, RequestType.ADD_DISH, newDish);
		try
		{
			SimpleClient.getClient().sendToServer(request);
//
//			for (Branch branch : allBranches)
//			{
//				branch.addMenuItem(newDish);
//				Request<Branch> updateRequest = new Request<>(ReqCategory.BRANCH, RequestType.UPDATE_BRANCH_MENU, branch);
//				SimpleClient.getClient().sendToServer(updateRequest);
//			}
		}
		catch (IOException e)
		{
			System.err.println("Error adding dish to database: " + e.getMessage());
		}
	}


	public void updateDishIngredients(MenuItem item)
	{
		Request<MenuItem> request = new Request<>(ReqCategory.BASE_MENU, RequestType.UPDATE_INGREDIENTS, item);
		try {
			SimpleClient.getClient().sendToServer(request);

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void updateDishType(MenuItem selectedItem)
	{
		try
		{
			int itemId = selectedItem.getItemID();
			Request<MenuItem> request = new Request<>(ReqCategory.BASE_MENU, RequestType.UPDATE_DISH_TYPE, selectedItem);
			getClient().sendToServer(request);
			System.out.println("Dish type updated for dish ID: " + itemId);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("Error updating dish type: " + e.getMessage());
		}
	}
	public void getAllComplaints()
	{
		Request request=new Request(COMPLAINT,GET_ALL_COMPLAINTS,null);
		try
		{
			sendToServer(request);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

    //Methods to request reports from the server
    public void requestReservationsReport(String branchName) throws IOException {
        sendToServer(new Request<>(ReqCategory.REPORTS, RequestType.GET_RES_REPORT, branchName));
    }

    public void requestDeliveriesReport(String branchName) throws IOException {
        sendToServer(new Request<>(ReqCategory.REPORTS, RequestType.GET_DELIV_REPORT, branchName));
        System.out.println("deliveries sent to server");
    }

    public void requestComplaintsReport(String branchName) throws IOException {
        System.out.println("[SimpleClient] Sending complaints report request for branch: " + branchName);
        sendToServer(new Request<>(ReqCategory.REPORTS, RequestType.GET_COMP_REPORT, branchName));
    }

}

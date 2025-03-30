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

public class SimpleClient extends AbstractClient
{
	//private boolean isMenuLoaded = false;
	private static SimpleClient client = null;
	private static MenuEvent pendingMenuEvent = null;  // Store pending MenuEvent if SecondaryController isn't ready
	private static boolean isSecondaryControllerInitialized = false;
	public static String host="localhost";
	public  static int port=3000;
	private static ActiveUser activeUser = null;
	public Map <String, String> mapReservation=new HashMap<String, String>();
	public ResInfo resInfo=new ResInfo();
	public boolean rebookReservation=false;
	public  boolean tableAvailable=true;
	public String userEmail;
	private Response<?> lastResponse;
	public Response<?> getResponse() {
		return lastResponse;
	}
	private SimpleClient(String host, int port)
	{
		super(host, port);
	}
	public static SimpleClient getClient()
	{
		if (client == null)
		{
			client = new SimpleClient(host, port);
		}
		return client;
	}
	@Override
	protected void handleMessageFromServer(Object msg)
	{
		if (msg instanceof Response)
		{
			Response response = (Response) msg;
			System.out.println("ResponseType: " + response.getResponseType());
			System.out.println("Status: " + response.getStatus());

			if (response.getResponseType() == Response.ResponseType.PERMIT_GRANTED_ACK
					&& response.getStatus() == Response.Status.SUCCESS)
			{
				EventBus.getDefault().post(new AcknowledgmentEvent());
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

			if (msg.getClass().equals(Warning.class)) {
				String message = msg.toString();
				System.out.println(message);
				EventBus.getDefault().post(new WarningEvent((Warning) msg));
			}

			if (response.getResponseType().equals(RETURN_MENU))
			{
//				if (!isMenuLoaded)
//				{
					Menu menu = (Menu) response.getData();
					MenuEvent menuEvent = new MenuEvent(menu);
					EventBus.getDefault().post(menuEvent);
//					isMenuLoaded = true;
//				}
//				else
//				{
//					System.out.println("Menu already loaded, skipping request.");
//				}

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
				try
				{
					List<Branch> branches = (List<Branch>) response.getData();
					BranchListSentEvent branchSentEvent = new BranchListSentEvent(branches);
					Platform.runLater(() -> {
						EventBus.getDefault().post(branchSentEvent);
					});
				}
				catch (ClassCastException e)
				{
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
			if (response.getResponseType().equals(Response.ResponseType.ADD_DISH))
			{
				MenuItem addedMenuItem = (MenuItem) response.getData();
				updateDishEvent addEvent = new updateDishEvent(addedMenuItem);
				EventBus.getDefault().post(addEvent);
			}

			if (response.getResponseType().equals(RETURN_BRANCH_TABLES))
			{
				Set<RestTable> tables = new HashSet<>((Collection) response.getData());
				EventBus.getDefault().post(new BranchTablesReceivedEvent(tables));
			}

			if (response.getResponseType().equals(CORRECTNESS_USER))
			{
				if (response.getStatus() == SUCCESS)
				{
					String responseData = response.getMessage();
					String[] parts = responseData.split(":");
					if (parts.length > 1) {
						String username = parts[0];
						String role = parts[1];
						// Set the active user
						SimpleClient.setActiveUser(new ActiveUser(username, EmployeeType.valueOf(role)));
						//post to eventbus
						EventBus.getDefault().post(new UserLoginSuccessEvent(username, role));
					}
					else
					{
						System.out.println("Error: Response doesn't contain both username and role.");
					}
				}
				else
				{
					String message = (String) response.getMessage();
					System.out.println("Login failed with message: " + message);
					EventBus.getDefault().post(new UserLoginFailedEvent(message != null ? message : "Unknown error"));
				}
			}
			if (response.getResponseType().equals(DELIVERY_CREATED))
			{
				Delivery delivery = (Delivery) response.getData();
				if (delivery != null)
				{
					System.out.println(delivery);
					EventBus.getDefault().post(delivery);
				}
				else
				{
					System.out.println("No delivery data received.");
				}
			}
			if (response.getResponseType().equals(SEND_DELIVERY))
			{
				Delivery delivery = (Delivery) response.getData();
				if (delivery != null) {
					System.out.println(delivery);
					EventBus.getDefault().post(delivery);
				}
				else
				{
					System.out.println("No delivery data received.");
					EventBus.getDefault().post("delivery not found");
				}
			}
			if (response.getResponseType().equals(DELIVERY_CANCELED)) {
				EventBus.getDefault().post("delivery deleted");
			}
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

			if (response.getResponseType().equals(UPDATE_BRANCH_TABLES)) {
				System.out.println("in updateBRANCH_TABLES");
				UpdateBranchTablesEvent event=new UpdateBranchTablesEvent((ResInfo) response.getData());
				EventBus.getDefault().post(event);
			}

			if(response.getResponseType().equals(RETURN_ALL_COMPLAINTS))
			{
				ReceivedAllComplaintsEvent event=new ReceivedAllComplaintsEvent((List<Complaint>) response.getData());
			}
			if(response.getResponseType().equals(RETURN_ACTIVE_RESERVATIONS))
			{
				if(response.getStatus().equals(SUCCESS))
				{
					SentActiveReservationsEvent event=new SentActiveReservationsEvent((List<ResInfo>) response.getData());
					EventBus.getDefault().post(event);
				}
				else
				{
					System.out.println("no reservations found");
				}
			}

			if (response.getResponseType().equals(CANCELED_RESERVATION))
			{
				System.out.println("in CANCELED_RESERVATION");
				String message= (String) response.getMessage();
				ReservationCancelledEvent event = new ReservationCancelledEvent(message);
				EventBus.getDefault().post(event);
			}
		}
		else
		{
			System.out.println("Received message is not of type Response");
			System.out.println("msg="+ msg);
		}
	}

	public static void setSecondaryControllerInitialized()
	{
		isSecondaryControllerInitialized = true;

		if (pendingMenuEvent != null)
		{
			EventBus.getDefault().post(pendingMenuEvent);
			pendingMenuEvent = null;
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
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	public void displayNetworkMenu() throws IOException
	{
		Request<Object> request = new Request<>(BASE_MENU, GET_BASE_MENU, null);
		client.sendToServer(request);
		System.out.println("menu base req sent");
	}

	public void displayBranchMenu(Branch branch) throws IOException
	{
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

	public void removeDishFromDatabase(MenuItem dishToRemove) {
		// Create a request to remove the dish
		Request<MenuItem> request = new Request<>(ReqCategory.BASE_MENU, RequestType.REMOVE_DISH, dishToRemove);
		try {
			SimpleClient.getClient().sendToServer(request);
		}
		catch (IOException e)
		{
			System.out.println("Error sending dish removal request: " + e.getMessage());
		}
	}

	public void addDishToDatabase(MenuItem newDish) {
		Request<MenuItem> request = new Request<>(ReqCategory.BASE_MENU, RequestType.ADD_DISH, newDish);
		try {
			SimpleClient.getClient().sendToServer(request);
		} catch (IOException e) {
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
}

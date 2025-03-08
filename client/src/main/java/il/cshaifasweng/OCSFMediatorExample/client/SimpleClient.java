package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.List;

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
		Response response=(Response)msg;
		if (msg.getClass().equals(Warning.class)) {
			String message = msg.toString();
			System.out.println(message);
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		// Safe cast
		if (response.getResponseType().equals(RETURN_MENU)) {
				System.out.println("Menu received, storing event...");
				Menu menu = (Menu) response.getData();
				MenuEvent menuEvent = new MenuEvent(menu);
				// Store the event if SecondaryController is not initialized
				if (!isSecondaryControllerInitialized) {
					pendingMenuEvent = menuEvent;
				} else {
					// Post immediately if SecondaryController is ready
					EventBus.getDefault().post(menuEvent);
					System.out.println("menu event posted");
				}
			}
//		if (response.getResponseType().equals(RETURN_BRANCH_MENU)) {
//			System.out.println("Menu received, storing event...");
//			Menu menu = (Menu) response.getData();
//			MenuEvent menuEvent = new MenuEvent(menu);
//			// Store the event if SecondaryController is not initialized
//			if (!isSecondaryControllerInitialized) {
//				pendingMenuEvent = menuEvent;
//			} else {
//				// Post immediately if SecondaryController is ready
//				EventBus.getDefault().post(menuEvent);
//				System.out.println("menu event posted");
//			}
//		}
			if (response.getResponseType().equals(UPDATED_PRICE)) {
				MenuItem menuItem = (MenuItem) response.getData();
				// Post immediately if SecondaryController is ready
				updateDishEvent updateEvent = new updateDishEvent(menuItem);
				EventBus.getDefault().post(updateEvent);
			}
			//list of branches sent from server
			else if (response.getResponseType().equals(BRANCHES_SENT)) {
				try {
					System.out.println("client got branches sent");
                    //noinspection unchecked
                    List<Branch> branches = (List<Branch>) response.getData();
					BranchListSentEvent branchSentEvent = new BranchListSentEvent(branches);
					EventBus.getDefault().post(branchSentEvent);
				}
				catch (ClassCastException e) {
					e.printStackTrace();
				}
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

//	public void showMenu(Menu menu) {
//		MenuEvent menuEvent = new MenuEvent(menu);
//		// Store the event if SecondaryController is not initialized
//		if (!isSecondaryControllerInitialized) {
//			pendingMenuEvent = menuEvent;
//		} else {
//			// Post immediately if SecondaryController is ready
//			EventBus.getDefault().post(menuEvent);
//			System.out.println("menu event posted");
//		}
//	}

}


package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;


import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Request.RequestType.*;


public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private static MenuEvent pendingMenuEvent = null;  // Store pending MenuEvent if SecondaryController isn't ready
	private static boolean isSecondaryControllerInitialized = false;
	public static String host = "localhost";
	public static int port = 3000;

	private SimpleClient(String host, int port) {
		super(host, port);

	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		Response response = (Response) msg;

		if (msg.getClass().equals(Warning.class)) {
			String message = msg.toString();
			System.out.println(message);
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}

//		if (msg.getClass().equals(Menu.class)) {
		if (response.getResponseType().equals(RETURN_MENU)) {
			System.out.println("Menu received, storing event...");
			Menu menu = (Menu) response.getData();
			menu.printMenu();
			MenuEvent menuEvent = new MenuEvent(menu);
			// Store the event if SecondaryController is not initialized
			if (!isSecondaryControllerInitialized) {
				pendingMenuEvent = menuEvent;
			} else {
				// Post immediately if SecondaryController is ready
				EventBus.getDefault().post(menuEvent);
			}
		}
		if (response.getResponseType().equals(UPDATED_PRICE)) {
			MenuItem menuItem = (MenuItem) response.getData();
			// Post immediately if SecondaryController is ready
			updateDishEvent updateEvent = new updateDishEvent(menuItem);
			EventBus.getDefault().post(updateEvent);
		}

		// Handle user authentication response
		if (response.getResponseType().equals(CORRECTNESS_USER)) {
			System.out.println("Handling CORRECTNESS_USER response with status: " + response.getStatus());

			if (response.getStatus() == Response.Status.SUCCESS) {
				String responseData = response.getMessage();
				System.out.println("Response Data: " + responseData);
				String[] parts = responseData.split(":");
				if (parts.length > 1) {
					String username = parts[0];
					String role = parts[1];
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

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(host, port);
		}
		return client;
	}

	public void displayMenu() throws IOException {
		Request request=new Request(DISPLAY_MENU);
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
		Request request=new Request(UPDATE_PRICE,data);
		client.sendToServer(request);
	}
}
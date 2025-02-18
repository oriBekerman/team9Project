package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Request.RequestType.*;



public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private static MenuEvent pendingMenuEvent = null;  // Store pending MenuEvent if SecondaryController isn't ready
	private static boolean isSecondaryControllerInitialized = false;
	public static String host="localhost";
	public  static int port=3000;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {

			if (msg.getClass().equals(Warning.class)) {
			String message = msg.toString();
			System.out.println(message);
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}

		if (msg instanceof Response<?> response) {
			// Safe cast
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
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(host, port);
		}
		return client;
	}

	public void displayNetworkMenu() throws IOException {
		Request<Object> request=new Request<>(GET_BASE_MENU);
		client.sendToServer(request);
		System.out.println("menu base req sent");
	}
	public void displayBranchMenu(String branchName) throws IOException {
		Request<String> request= new Request<>(GET_BRANCH_MENU);
		request.setData(branchName);
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
		Request<String[]> request= new Request<>(UPDATE_PRICE, data);
		client.sendToServer(request);
	}

	public void getBranchList() {
		Request request=new Request(GET_BRANCHES);
        try {
            client.sendToServer(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("getBranchList requested");
	}
}


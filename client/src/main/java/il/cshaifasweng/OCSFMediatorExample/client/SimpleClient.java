package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private static MenuEvent pendingMenuEvent = null;  // Store pending MenuEvent if SecondaryController isn't ready
	private static boolean isSecondaryControllerInitialized = false;

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

		if (msg.getClass().equals(Menu.class)) {
			System.out.println("Menu received, storing event...");
			MenuEvent menuEvent = new MenuEvent((Menu) msg);
			// Store the event if SecondaryController is not initialized
			if (!isSecondaryControllerInitialized) {
				pendingMenuEvent = menuEvent;
			} else {
				// Post immediately if SecondaryController is ready
				EventBus.getDefault().post(menuEvent);
			}
		}
		if(msg.getClass().equals(MenuItem.class))
		{
			MenuItem menuItem = (MenuItem) msg;
			// Post immediately if SecondaryController is ready
			updateDishEvent updateEvent=new updateDishEvent(menuItem);
			EventBus.getDefault().post(updateEvent);
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

	public void displayMenu() throws IOException {
		client.sendToServer("#display menu");
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
		client.sendToServer("#edit item"+","+ itemId +","+ price);
	}
}
package il.cshaifasweng.OCSFMediatorExample.client;

import java.util.*;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import javax.swing.event.MenuEvent;
import java.io.IOException;
public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;

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
		//got menu to display from server
		if (msg.getClass().equals(Menu.class))
		{
			System.out.println("menu received!");
			((Menu) msg).printMenu();
		}
		//got updated dish from server
		if (msg.getClass().equals(MenuItem.class))
		{
			System.out.println("new item received");
			((MenuItem) msg).printMenuItem();
			EventBus.getDefault().post(new updateDishEvent((MenuItem) msg));
			System.out.println("new item event posted");
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
	public void editMenu(String itemId,String price) throws IOException
	{
		client.sendToServer("#edit item"+","+ itemId +","+ price);
	}


}

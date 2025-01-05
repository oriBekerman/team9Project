package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;


public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private MenuItemsController menuItemsController = new MenuItemsController();

	public SimpleServer(int port) {
		super(port);}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (msgString.startsWith("add client")) {
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				client.sendToClient("client added successfully");
				System.out.println("Client added successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}
		//receives display menu msg from client and returns the menu
		else if (msgString.startsWith("#display menu"))
		{
			System.out.println("Displaying menu");
			MenuItem item2 = new MenuItem(
					"Pizza",
					55.00,
					"Mushrooms, onions, tomatoes",
					"Includes vegan option",
					null
			);
			List<MenuItem> menuList = new ArrayList<>(List.of(item2));
			Menu menu=new Menu(menuList);

			try {
				client.sendToClient(menu);//sent the menu
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//receives edit item msg from client and returns the updated item
		else if (msgString.contains("#edit item"))
		{
			int itemId=0;
			int price=0;
			System.out.println("edit item");
			String[] parts = msgString.split(",");
			try {
				 itemId = Integer.parseInt(parts[1]);
				 price = Integer.parseInt(parts[2]);
			} catch (NumberFormatException e) {
				System.out.println("Error: One of the parts is not a valid integer.");
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Error: The input string does not have enough parts.!");
			}
			updateDish(itemId,price);
			MenuItem item2 = new MenuItem(
					"Pizza",
					55.00,
					"Mushrooms, onions, tomatoes",
					"Includes vegan option",
					null
			);
			try {
				client.sendToClient(item2);//sent the menu
			} catch (IOException e) {
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
	private void updateDish(int ItemId, int price) {
		System.out.println("in updateDish");
	}
}
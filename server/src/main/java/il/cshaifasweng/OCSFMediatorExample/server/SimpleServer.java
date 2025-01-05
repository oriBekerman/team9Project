package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;




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
		//receives display menu msg from client and returns a map with "menu sent" string and a list of the menu items

		else if (msgString.startsWith("#display menu"))
		{
			System.out.println("Displaying menu");
			MenuItem item2 = new MenuItem(
					"Pizza ",
					55.00,
					" Mushrooms, onions, tomatoes ",
					"Includes vegan option ",
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

	}



	private void updateItemInDB(MenuItem item) {
		Session session = null;
		try {
			// Initialize the Hibernate session
			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();

			// Double the price of the item
			double currentPrice = item.getPrice();
			double newPrice = currentPrice * 2;
			item.setPrice(newPrice);

			// Update the item in the database
			session.merge(item);

			// Commit the transaction
			session.getTransaction().commit();

			System.out.println("Item with ID " + item.getItemID() + " updated successfully. New price: " + newPrice);
		} catch (Exception e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
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
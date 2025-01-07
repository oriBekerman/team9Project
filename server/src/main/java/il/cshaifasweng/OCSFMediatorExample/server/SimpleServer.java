package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;






public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	private static Session session;
	private Menu menu=new Menu();

	public SimpleServer(int port) {
		super(port);
		// Initialize DB
		try {

			SessionFactory sessionFactory = getSessionFactory(); //need to define session factory
			session = sessionFactory.openSession(); // have to do next two lines to make actions in the DB
			session.beginTransaction();
			List<MenuItem> existingItems = getMenuItems();
			if (existingItems.isEmpty()) {
				System.out.println("Menu is empty in the database- initilize it");
				initializeData();  // Only initialize data if the menu is empty in db
			} else {
				System.out.println("Menu already has data in the database, skipping initialization.");
			}
		} catch (Exception exception) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.err.println("An error occured, changes have been rolled back.");
			exception.printStackTrace();
		} finally {
			session.close();
		}

	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();

		if (msgString.startsWith("add client")) {
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				client.sendToClient("client added successfully");
				System.out.println("Client added successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}
		//receives display menu msg from client and sends back menu
		else if (msgString.startsWith("#display menu"))
		{
			displayMenuFun();
			try {
				client.sendToClient(menu);//sent the menu
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//receives edit item msg from client and returns the updated item
		else if (msgString.contains("#edit item"))
		{
			System.out.println("in edit item");
			int itemId=0;
			double newPrice=0;
			String[] parts = msgString.split(",");
			try {
				 itemId = Integer.parseInt(parts[1]);
				 System.out.println(itemId);
				 newPrice = Double.parseDouble(parts[2]);
			} catch (NumberFormatException e) {
				System.out.println("Error: One of the parts is not a valid integer.");
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Error: The input string does not have enough parts.!");
			}
			System.out.println("before updateItemInDB");
			MenuItem item=menu.getItemByID(itemId-1); //id start in 1 when but index in 0
			item.printMenuItem();
			updateItemInDB(item,newPrice);
			System.out.println("after updateItemInDB");
            sendToAllClients((menu.getItemByID(itemId)));//sent the menu to all the clients
        }
	}
	private void updateItemInDB(MenuItem item,double newPrice) {
		Session session = null;
		System.out.println("in updateItemInDB");
		try {
			SessionFactory sessionFactory = getSessionFactory(); //need to define session factory
			session = sessionFactory.openSession(); // have to do next two lines to make actions in the DB
			session.beginTransaction();
			//after begin transaction we can make actions in the database, when we finish we make commit

			// Double the price of the item
			item.setPrice(newPrice);

			// Update the item in the database
			session.merge(item);

			// Force Hibernate to flush changes to the database
			session.flush();

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


	private void displayMenuFun() {
		try {

			SessionFactory sessionFactory = getSessionFactory(); //need to define session factory
			session = sessionFactory.openSession(); // have to do next two lines to make actions in the DB
			session.beginTransaction();
			//after begin transaction we can make actions in the database, when we finish we make commit
			System.out.println("Displaying menu");
			menu.SetMenuItems(getMenuItems());
			menu.printMenu();
			session.getTransaction().commit(); // Save everything.
		} catch (Exception exception) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.err.println("An error occured, changes have been rolled back.");
			exception.printStackTrace();
		} finally {
			session.close();
		}
	}
	private List<MenuItem> getMenuItems()
	{
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<MenuItem> query = builder.createQuery(MenuItem.class);
		query.from(MenuItem.class);
		List<MenuItem> data = session.createQuery(query).getResultList();
		System.out.println("getting menu items");
		System.out.println(data);
		return data;
	}

	private static SessionFactory getSessionFactory() throws
			HibernateException
	{
		Configuration configuration = new Configuration();
//		Scanner scanner = new Scanner(System.in);
//		System.out.println("Please enter the database password: ");
//		String password = scanner.nextLine();
		configuration.setProperty("hibernate.connection.password", "poolgirL1?");
		//add dynamic password here
		// Add ALL of your entities(Classes) here. You can also try adding a whole package.
		configuration.addAnnotatedClass(Menu.class);
		configuration.addAnnotatedClass(MenuItem.class);


		ServiceRegistry serviceRegistry = new
				StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.build();

		return configuration.buildSessionFactory(serviceRegistry);
	}


	public static void initializeData() {

		// יצירת פריטים עבור התפריט בעזרת הבנאי
		MenuItem item1 = new MenuItem("Salad", 35.00,
				"Tomatoes, cucumbers, lettuce",
				"Low calorie", null);

		MenuItem item2 = new MenuItem("Pizza ", 55.00,
				" Mushrooms, onions, tomatoes",
				" Includes vegan option ", null);

		MenuItem item3 = new MenuItem("Pasta", 60.00,
				"Mushroom cream sauce",
				"Available gluten-free", null);

		MenuItem item4 = new MenuItem("Hamburger", 80.00,
				"Meatball, pickle, tomato, lettuce",
				"Choice of meat or plant-based", null);

		MenuItem item5 = new MenuItem("Edamame", 30.00,
				"Edamame",
				"Served with sea salt", null);

		// שמירת הפריטים במסד הנתונים
		session.save(item1);
		session.save(item2);
		session.save(item3);
		session.save(item4);
		session.save(item5);

		System.out.println("Data initialization completed!");
		session.flush();

	}

}


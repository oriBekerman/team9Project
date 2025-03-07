package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderItem;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.DeliveryController;

import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
	
	private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        server = new SimpleServer(3000);
        server.listen();
        System.out.println("Server is listening on port 3000");
    }
}

package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.DeliveryRepository;

import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.*;


public class DeliveryController {

    private DeliveryRepository deliveryRepository;

    // Constructor
    public DeliveryController() {
        this.deliveryRepository = new DeliveryRepository();
    }

    // Method to handle requests for deliveries
    public Response handleRequest(Request request) {
        System.out.println("Handling delivery request: " + request.getRequestType());
        return switch (request.getRequestType())
        {
            case CREATE_DELIVERY -> createDelivery(request);
            case GET_DELIVERY -> getDeliveryByOrderNumber(request);
            case CANCEL_DELIVERY -> cancelDeliveryByOrderNumber(request);
            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
        };
    }


    // Method to create a new delivery
    public Response createDelivery(Request request) {
        Response response = new Response(Response.ResponseType.DELIVERY_CREATED, null, ERROR, THIS_CLIENT);
        System.out.println("Creating delivery...");

        try {
            // Extract data from the request
            Delivery delivery = (Delivery) request.getData(); // Assume we send a Delivery object in the request
            delivery.setTotalPrice(delivery.calculateTotalPrice());

            // Create the delivery and check the result
            boolean isCreated = deliveryRepository.populateDelivery(delivery);

            // If the delivery was created successfully, set the response status to SUCCESS
            if (isCreated) {
                response.setStatus(SUCCESS);
                response.setData(delivery); // include delivery in the response
            } else {
                // If creation fails, set the response status to ERROR
                response.setStatus(ERROR);
                response.setMessage("Failed to create delivery");
            }

        } catch (Exception exception) {
            // Handle exceptions and set the response status to ERROR
            response.setStatus(ERROR);
            response.setMessage("Error while creating the delivery: " + exception.getMessage());
            System.err.println("Error while creating delivery: " + exception.getMessage());
            exception.printStackTrace();
        }

        return response;
    }


    // Method to retrieve all deliveries
    public Response getAllDeliveries() {
        Response response = new Response(Response.ResponseType.GET_ALL_DELIVERIES, null, ERROR, THIS_CLIENT);
        System.out.println("Getting all deliveries...");

        List<Delivery> deliveries = deliveryRepository.getAllDeliveries();
        if (deliveries.isEmpty()) {
            response.setStatus(ERROR);
            System.out.println("No deliveries found");
        } else {
            response.setStatus(SUCCESS);
            response.setData(deliveries);
            System.out.println("Found deliveries successfully");
        }
        return response;
    }

    private Response<Delivery> getDeliveryByOrderNumber(Request request) {
        // Default response with an error status
        Response<Delivery> response = new Response<>(Response.ResponseType.SEND_DELIVERY, "Invalid request",ERROR, THIS_CLIENT);

        // Extract order number from request
        Integer orderNumber = (Integer) request.getData();

        if (orderNumber == null) {
            response.setMessage("Order number is missing");
            return response;
        }

        // Fetch delivery from repository
        Delivery delivery = deliveryRepository.getDeliveryByOrderNumber(orderNumber);
        System.out.println(delivery);

        if (delivery != null) {
            response = new Response<>(Response.ResponseType.SEND_DELIVERY, delivery,SUCCESS,THIS_CLIENT);
        } else {
            response.setMessage("No delivery found for order number: " + orderNumber);
        }

        return response;
    }

    public Response cancelDeliveryByOrderNumber(Request request) {
        // Default response with an error status
        Response response = new Response(Response.ResponseType.DELIVERY_CANCELED, null, ERROR, THIS_CLIENT);
        System.out.println("Deleting delivery...");

        // Extract order number from the request
        Integer orderNumber = (Integer) request.getData();

        if (orderNumber == null) {
            response.setMessage("Order number is missing");
            return response;
        }

        // Try to delete the delivery from the repository
        boolean isDeleted = deliveryRepository.cancelDeliveryByOrderNumber(orderNumber);

        // Check if the deletion was successful
        if (isDeleted) {
            response.setStatus(SUCCESS);
            response.setMessage("Delivery successfully deleted.");
        } else {
            response.setStatus(ERROR);
            response.setMessage("Failed to delete delivery with order number: " + orderNumber);
        }

        return response;
    }




    public boolean checkIfEmpty()
    {
        return (deliveryRepository.checkIfEmpty());
    }

    public void populateDelivery(Delivery delivery)
    {
        deliveryRepository.populateDelivery(delivery);
    }

}

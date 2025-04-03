package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;

import java.util.stream.Collectors;

public class ReportDetail {

    // Reservation
    private String fullNameRES;
    private String numOfGuests;
    private String reservationDate;
    private String hours;
    private String inOrOut;

    // Delivery
    private String fullNameDelivery;
    private String deliveryDate;
    private String orderedItems;
    private String price;
    private String deliveryMethod;

    // Complaint
    private String complaintDate;
    private String detail;
    private int quantity;
    private String branchName;
    private String complaintMonth;



    // Constructor for Reservations
    public ReportDetail(ResInfo resInfo) {
        try {
            this.fullNameRES = resInfo.getCustomer() != null ? resInfo.getCustomer().getName() : "N/A";
            this.numOfGuests = String.valueOf(resInfo.getNumOfGuests());
//            this.reservationDate = resInfo.getResDate().toString();
            this.hours = resInfo.getHours().toString();
            this.inOrOut = resInfo.getInOrOut();
        } catch (Exception e) {
            System.out.println("[Constructor Exception] " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Constructor for Delivery
    public ReportDetail(Delivery delivery) {
        this.fullNameDelivery = delivery.getCustomer().getName();
//        this.deliveryDate = delivery.getDate();
        this.orderedItems = delivery.getOrderItems().stream()
                .map(item -> item.getMenuItem().getName() + " x " + item.getQuantity())
                .collect(Collectors.joining(", "));
        this.price = String.valueOf(delivery.getTotalPrice());
        this.deliveryMethod = delivery.getDeliveryMethod().toString();
    }

    // Constructor for Complaint
    public ReportDetail(Complaint complaint) {
        this.complaintDate = complaint.getComplaintDate().toString();
        this.detail = complaint.getComplaintText();
        this.quantity = 1;  // Every complaint counts as one
        this.branchName = (complaint.getBranch() != null) ? complaint.getBranch().getName() : "Unknown Branch";
        this.complaintMonth = complaint.getComplaintDate().getMonth().toString();
    }


    // Reservation Getters and Setters
    public String getFullNameRES() {
        return fullNameRES;
    }
    public void setFullNameRES(String fullNameRES) {
        this.fullNameRES = fullNameRES;
    }

    public String getNumOfGuests() {
        return numOfGuests;
    }
    public void setNumOfGuests(String numOfGuests) {
        this.numOfGuests = numOfGuests;
    }

    public String getReservationDate() {
        return reservationDate;
    }
    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getHours() {
        return hours;
    }
    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getInOrOut() {
        return inOrOut;
    }
    public void setInOrOut(String inOrOut) {
        this.inOrOut = inOrOut;
    }


    // Delivery Getters and Setters
    public String getFullNameDelivery() {
        return fullNameDelivery;
    }
    public void setFullNameDelivery(String fullNameDelivery) {
        this.fullNameDelivery = fullNameDelivery;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getOrderedItems() {
        return orderedItems;
    }
    public void setOrderedItems(String orderedItems) {
        this.orderedItems = orderedItems;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }
    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }


    // Complaint Getters and Setters

    public String getComplaintDate() {
        return complaintDate;
    }
    public void setComplaintDate(String date) {
        this.complaintDate = date;
    }

    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBranchName() {
        return branchName;
    }
    public void setBranchName(String branchName) {this.branchName = branchName;}

    public String getComplaintMonth() {
        return complaintMonth;
    }
    public void setComplaintMonth(String date) {
        this.complaintMonth = date;
    }
}

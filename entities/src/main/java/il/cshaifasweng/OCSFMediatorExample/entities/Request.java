package il.cshaifasweng.OCSFMediatorExample.entities;


import java.io.Serializable;

public class Request<T> implements Serializable{

    private String action;
    private T data;
    private RequestType requestType;

    public Request(String action, T data, RequestType requestType) {
        this.action = action;
        this.data = data;
        this.requestType = requestType;
    }
    public Request(String action,RequestType requestType) {
        this.action = action;
        this.requestType = requestType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public RequestType getRequestType()
    {
        return requestType;
    }
    public void setRequestType(RequestType requestType)
    {
        this.requestType = requestType;
    }

//    // Enums for RequestType, ActionType, and Status
//    public enum RequestType {
//        GENERAL,
//        MENU,
//        COMPLAINT,
//        LOGIN,
//        DELIVERY,
//        RESERVATION,
//        REPORT
//    }

//each request has a type (enum)
// //so the server can navigate the request to the right controller by identifying the requestType
    public enum RequestType{
       //menu related requests
        DISPLAY_MENU

        //login related requests

        //delivery related requests
    }
}
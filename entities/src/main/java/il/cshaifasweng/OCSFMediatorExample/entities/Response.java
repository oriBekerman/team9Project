package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Response<T> implements Serializable {

    private Status status;
    private T data;
    private String message = "";
    private ResponseType responseType;

    // Full Constructor
    public Response(Status status, T data, String message, ResponseType responseType) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.responseType = responseType;
    }

    // Constructor without message
    public Response(Status status, T data,ResponseType responseType) {
        this.status = status;
        this.data = data;
        this.responseType = responseType;
    }

    // Getters and Setters
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

//    public ActionType getActionType() {
//        return actionType;
//    }
//
//    public void setActionType(ActionType actionType) {
//        this.actionType = actionType;
//    }

//    // Enums for ResponseType, ActionType, and Status
//    public enum ResponseType {
//        GENERAL,
//        MENU,
//        COMPLAINT,
//        LOGIN,
//        DELIVERY,
//        RESERVATION,
//        REPORT
//    }

    public enum ResponseType {
        NO_ACTION,
        //menu related responses
        RETURN_MENU
    }

    public enum Status {
        ERROR,
        SUCCESS
    }
}

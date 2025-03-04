package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Response<T> implements Serializable {

    private ResponseType responseType;
    private Status status;
    private T data;
    private String message = "";
    private Recipient recipient = null;

    // Full Constructor
    public Response(ResponseType responseType, T data, String message,Status status, Recipient recipient) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.responseType = responseType;
        this.recipient = recipient;
    }

    // Constructor without message
    public Response(ResponseType responseType,T data,Status status, Recipient recipient) {
        this.status = status;
        this.data = data;
        this.responseType = responseType;
        this.recipient = recipient;
    }

    // Constructor without data
    public Response(ResponseType responseType, String message ,Status status, Recipient recipient) {
        this.status = status;
        this.message = message;
        this.responseType = responseType;
        this.recipient = recipient;
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

    public Recipient getRecipient()
    {
        return recipient;
    }
    public void setRecipient(Recipient recipient)
    {
        this.recipient = recipient;
    }

    public enum ResponseType {
        NO_ACTION,
        //menu related responses
        RETURN_MENU,
        UPDATED_PRICE,

        RETURN_BRANCH_MENU,
        BRANCHES_SENT,
        RETURN_BRANCH,
        RETURN_DELIVERABLES,
        RETURN_BRANCH_TABLES,
        //login
        CORRECTNESS_USER
    }

    public enum Status {
        ERROR,
        SUCCESS
    }
    public enum Recipient {
        ALL_CLIENTS,
        THIS_CLIENT
    }
}

package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Response<T> implements Serializable {

    private ResponseType responseType;
    private Status status;
    private T data;
    private String message = "";

    // Full Constructor
    public Response(ResponseType responseType, T data, String message,Status status) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.responseType = responseType;
    }

    // Constructor without message
    public Response(ResponseType responseType,T data,Status status) {
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

    public enum ResponseType {
        NO_ACTION,
        //menu related responses
        RETURN_MENU,
        UPDATED_PRICE,

        //login related responses
        CORRECTNESS_USER
    }

    public enum Status {
        ERROR,
        SUCCESS
    }
}

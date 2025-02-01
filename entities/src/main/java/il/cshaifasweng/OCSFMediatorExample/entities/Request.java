package il.cshaifasweng.OCSFMediatorExample.entities;


import java.io.Serializable;

public class Request<T> implements Serializable{

    private T data;
    private RequestType requestType;

    public Request(RequestType requestType, T data) {
        this.data = data;
        this.requestType = requestType;
    }
    public Request(RequestType requestType) {
        this.requestType = requestType;
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

//each request has a type (enum)
// //so the server can navigate the request to the right controller by identifying the requestType

    public enum RequestType{
       //menu related requests
        DISPLAY_MENU,
        UPDATE_PRICE,
        //login related requests
        CHECK_USER
        //delivery related requests
    }
}
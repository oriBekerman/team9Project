package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Request<T> implements Serializable {

    private T data;
    private ReqCategory category;
    private RequestType requestType;

    // Constructor with category, requestType, and data
    public Request(ReqCategory category, RequestType requestType, T data) {
        this.data = data;
        this.category = category;
        this.requestType = requestType;
    }

    // Constructor with only requestType (assuming it's for general use without category/data)
    public Request(RequestType requestType) {
        this.requestType = requestType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ReqCategory getCategory() {
        return category;
    }

    public void setCategory(ReqCategory category) {
        this.category = category;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}

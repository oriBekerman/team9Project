package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Message implements Serializable {
    private String action;
    private Object object;

    public Message(String action, Object object) {
        this.action = action;
        this.object = object;
    }

    public String getAction() {
        return action;
    }

    public Object getObject() {
        return object;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}

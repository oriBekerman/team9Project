package il.cshaifasweng.OCSFMediatorExample.client.Events;

public class UserLoginFailedEvent {
    private String message;

    public UserLoginFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

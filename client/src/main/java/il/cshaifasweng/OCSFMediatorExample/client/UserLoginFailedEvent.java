package il.cshaifasweng.OCSFMediatorExample.client;

public class UserLoginFailedEvent {
    private String message;

    public UserLoginFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

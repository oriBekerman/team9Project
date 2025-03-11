package il.cshaifasweng.OCSFMediatorExample.client.Events;

public class UserLoginSuccessEvent {
    private String username;
    private String authorization;

    public UserLoginSuccessEvent(String username, String authorization) {
        this.username = username;
        this.authorization = authorization;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthorization() {
        return authorization;
    }
}

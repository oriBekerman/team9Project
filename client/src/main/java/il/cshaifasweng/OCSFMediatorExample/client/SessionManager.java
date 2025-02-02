package il.cshaifasweng.OCSFMediatorExample.client;

public class SessionManager {
    private static SessionManager instance;
    private String username;
    private String authorization;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUser(String username, String authorization) {
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

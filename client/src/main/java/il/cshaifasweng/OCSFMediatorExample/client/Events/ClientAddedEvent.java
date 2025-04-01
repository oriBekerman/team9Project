package il.cshaifasweng.OCSFMediatorExample.client.Events;

public class ClientAddedEvent {
    private String successMessage;

    public ClientAddedEvent(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getMessage() {
        return successMessage;
    }
}

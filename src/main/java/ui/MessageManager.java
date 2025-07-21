package ui;

public class MessageManager {

    private String currentMessage;
    private long messageSetTime;
    private final long DISPLAY_DURATION = 2000;

    public void showMessage(String message) {
        this.currentMessage = message;
        this.messageSetTime = System.currentTimeMillis();
    }

    public String getMessage() {
        if (currentMessage == null) return null;

        if (System.currentTimeMillis() - messageSetTime > DISPLAY_DURATION) {
            currentMessage = null;
        }

        return currentMessage;
    }

    public boolean isMessageVisible() {
        return currentMessage != null;
    }
}

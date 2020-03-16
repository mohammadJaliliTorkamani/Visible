package dev.aban.visible.model;

public class PasswordRecoverResponse {
    private boolean sent;
    private String message;

    public PasswordRecoverResponse(boolean sent, String message) {
        this.sent = sent;
        this.message = message;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

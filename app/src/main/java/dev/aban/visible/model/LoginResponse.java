package dev.aban.visible.model;

public class LoginResponse {
    private boolean successful;
    private String token;

    public LoginResponse(boolean successful, String token) {
        this.successful = successful;
        this.token = token;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

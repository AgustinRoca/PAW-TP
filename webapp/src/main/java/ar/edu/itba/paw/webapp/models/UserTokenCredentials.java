package ar.edu.itba.paw.webapp.models;

public class UserTokenCredentials {
    private String username;
    private String token;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

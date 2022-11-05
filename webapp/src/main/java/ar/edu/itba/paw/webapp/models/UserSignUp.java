package ar.edu.itba.paw.webapp.models;

import ar.edu.itba.paw.models.User;

public abstract class UserSignUp {
    private User user;

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

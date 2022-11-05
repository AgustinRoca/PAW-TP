package ar.edu.itba.paw.interfaces;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MediCareException extends RuntimeException {
    private List<String> messages;

    public MediCareException(Collection<String> messages) {
        super();
        this.messages = new LinkedList<>(messages);
    }

    public MediCareException(String message) {
        super(message);
        this.messages = new LinkedList<>();
        this.messages.add(message);
    }

    public List<String> getMessages() {
        return this.messages;
    }
}

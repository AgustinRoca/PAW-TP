package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.MediCareException;

public class EmailAlreadyExistsException extends MediCareException {
    public EmailAlreadyExistsException() {
        super("El email ya esta asociado a otra cuenta");
    }
}

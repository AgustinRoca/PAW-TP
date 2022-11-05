package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.MediCareException;

public class InvalidMinutesException extends MediCareException {
    public InvalidMinutesException() {
        super("El profesional solicitado no puede atenderlo en la fecha seleccionada");
    }
}


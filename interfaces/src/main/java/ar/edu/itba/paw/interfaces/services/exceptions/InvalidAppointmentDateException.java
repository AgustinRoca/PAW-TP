package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.MediCareException;

public class InvalidAppointmentDateException extends MediCareException {
    public InvalidAppointmentDateException() {
        super("El profesional solicitado no puede atenderlo en la fecha seleccionada");
    }
}

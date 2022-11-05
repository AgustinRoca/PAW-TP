package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.MediCareException;

public class InvalidAppointmentAlreadyTakenException extends MediCareException {
    public InvalidAppointmentAlreadyTakenException() {
        super("No hay disponibilidad para la fecha seleccionada");
    }
}

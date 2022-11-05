package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.MediCareException;

public class AppointmentAlreadyCompletedException extends MediCareException {
    public AppointmentAlreadyCompletedException() {
        super("El turno ya ha sido finalizado");
    }
}

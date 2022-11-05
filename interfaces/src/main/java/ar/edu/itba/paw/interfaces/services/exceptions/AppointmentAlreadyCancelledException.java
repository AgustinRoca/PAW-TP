package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.MediCareException;

public class AppointmentAlreadyCancelledException extends MediCareException {
    public AppointmentAlreadyCancelledException() {
        super("El turno ya ha sido cancelado");
    }
}

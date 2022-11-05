package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.MediCareException;

public class InvalidAppointmentDurationException extends MediCareException {
    public InvalidAppointmentDurationException() {
        super("La duracion del turno es invalida");
    }
}

package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.MediCareException;

public class InvalidAppointmentStatusChangeException extends MediCareException {
    public InvalidAppointmentStatusChangeException(String fromStatus, String toStatus) {
        super("No se puede cambiar este turno del estado " + fromStatus + " al estado " + toStatus);
    }
}

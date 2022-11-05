package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.MediCareException;

public class NotEnoughPrivilegesException extends MediCareException {
    public NotEnoughPrivilegesException() {
        super("No tiene suficientes permisos para realizar esta accion");
    }
}

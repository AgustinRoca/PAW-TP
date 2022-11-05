package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.MediCareException;

public class InvalidEmailDomain extends MediCareException {
    public InvalidEmailDomain() {
        super("El dominio de la direccion de email no se encuentra soportado");
    }
}

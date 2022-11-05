package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.exceptions.APIErrorFactory.APIErrorBuilder;
import ar.edu.itba.paw.webapp.models.Constants;

public abstract class UnprocessableEntityException {
    public static APIErrorBuilder build() {
        return APIErrorFactory.buildError(Constants.UNPROCESSABLE_ENTITY_CODE, Constants.UNPROCESSABLE_ENTITY_DESCRIPTION);
    }
}

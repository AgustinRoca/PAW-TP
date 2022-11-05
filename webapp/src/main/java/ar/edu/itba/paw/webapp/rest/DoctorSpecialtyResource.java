package ar.edu.itba.paw.webapp.rest;

import ar.edu.itba.paw.interfaces.services.DoctorSpecialtyService;
import ar.edu.itba.paw.webapp.media_types.DoctorSpecialtyMIME;
import ar.edu.itba.paw.webapp.media_types.ErrorMIME;
import ar.edu.itba.paw.webapp.media_types.MIMEHelper;
import ar.edu.itba.paw.webapp.rest.utils.GenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/specialties")
@Component
public class DoctorSpecialtyResource extends GenericResource {
    @Autowired
    private DoctorSpecialtyService doctorSpecialtyService;

    @GET
    @Produces({DoctorSpecialtyMIME.GET_LIST, ErrorMIME.ERROR})
    public Response getCollection(
            @Context HttpHeaders httpheaders) {
        MIMEHelper.assertServerType(httpheaders, DoctorSpecialtyMIME.GET_LIST);

        return Response
                .ok()
                .entity(this.doctorSpecialtyService.list())
                .type(DoctorSpecialtyMIME.GET_LIST)
                .build();
    }
}

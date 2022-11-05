package ar.edu.itba.paw.webapp.rest;

import ar.edu.itba.paw.interfaces.services.LocalityService;
import ar.edu.itba.paw.models.Locality;
import ar.edu.itba.paw.webapp.media_types.ErrorMIME;
import ar.edu.itba.paw.webapp.media_types.LocalityMIME;
import ar.edu.itba.paw.webapp.media_types.MIMEHelper;
import ar.edu.itba.paw.webapp.rest.utils.GenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Optional;

@Path("/localities")
@Component
public class LocalityResource extends GenericResource {
    @Autowired
    private LocalityService localityService;

    @GET
    @Path("")
    @Produces({LocalityMIME.GET_LIST, ErrorMIME.ERROR})
    public Response getAllCollection(@Context HttpHeaders httpheaders) {
        MIMEHelper.assertServerType(httpheaders, LocalityMIME.GET_LIST);

        Collection<Locality> localities = this.localityService.list();

        return Response
                .ok()
                .entity(localities)
                .type(LocalityMIME.GET_LIST)
                .build();
    }

    @GET
    @Path("{id}")
    @Produces({LocalityMIME.GET, ErrorMIME.ERROR})
    public Response getEntityById(
            @Context HttpHeaders httpheaders,
            @PathParam("id") Integer id) {
        MIMEHelper.assertServerType(httpheaders, LocalityMIME.GET);

        if (id == null) throw this.missingPathParams();

        Optional<Locality> localityOptional = this.localityService.findById(id);
        if (!localityOptional.isPresent()) throw this.notFound();

        return Response.ok(localityOptional.get()).type(LocalityMIME.GET).build();
    }
}

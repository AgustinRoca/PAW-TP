package ar.edu.itba.paw.webapp.rest;

import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.webapp.media_types.CountryMIME;
import ar.edu.itba.paw.webapp.media_types.ErrorMIME;
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
import java.util.Optional;

@Path("/countries")
@Component
public class CountryResource extends GenericResource {
    @Autowired
    private CountryService countryService;

    @GET
    @Produces({CountryMIME.GET_LIST, ErrorMIME.ERROR})
    public Response getCollection(
            @Context HttpHeaders httpheaders) {
        MIMEHelper.assertServerType(httpheaders, CountryMIME.GET_LIST);

        return Response
                .ok()
                .entity(this.countryService.list())
                .type(CountryMIME.GET_LIST)
                .build();
    }

    @GET
    @Path("{id}")
    @Produces({CountryMIME.GET, ErrorMIME.ERROR})
    public Response getEntity(
            @Context HttpHeaders httpheaders,
            @PathParam("id") String id) {
        MIMEHelper.assertServerType(httpheaders, CountryMIME.GET);

        if (id == null || id.isEmpty()) throw this.missingPathParams();

        Optional<Country> countryOptional = this.countryService.findById(id);
        if (!countryOptional.isPresent())
            throw this.notFound();

        return Response.ok(countryOptional.get()).type(CountryMIME.GET).build();
    }
}

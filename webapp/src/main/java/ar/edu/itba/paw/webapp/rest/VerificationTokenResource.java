package ar.edu.itba.paw.webapp.rest;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.VerificationTokenService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerificationToken;
import ar.edu.itba.paw.webapp.media_types.ErrorMIME;
import ar.edu.itba.paw.webapp.rest.utils.GenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/verify")
@Component
public class VerificationTokenResource extends GenericResource {
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private UserService userService;

    @POST
    @Path("{token}")
    @Produces({MediaType.WILDCARD, ErrorMIME.ERROR})
    public Response getEntity(
            @Context HttpHeaders httpheaders,
            @PathParam("token") String token) {
        if (token == null) throw this.missingPathParams();

        Optional<VerificationToken> verificationTokenOptional = this.verificationTokenService.findByToken(token);
        if (!verificationTokenOptional.isPresent()) throw this.notFound();

        Optional<User> userOptional = this.userService.findByVerificationTokenId(verificationTokenOptional.get().getId());
        if (!userOptional.isPresent()) {
            this.verificationTokenService.remove(verificationTokenOptional.get().getId());
            throw this.notFound();
        }

        this.userService.verify(userOptional.get(), token);

        return Response.noContent().build();
    }
}
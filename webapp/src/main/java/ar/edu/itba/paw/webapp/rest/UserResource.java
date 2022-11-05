package ar.edu.itba.paw.webapp.rest;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.exceptions.ConflictException;
import ar.edu.itba.paw.webapp.exceptions.UnprocessableEntityException;
import ar.edu.itba.paw.webapp.media_types.*;
import ar.edu.itba.paw.webapp.models.*;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import ar.edu.itba.paw.webapp.rest.utils.GenericAuthenticationResource;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;

@Path("/users")
@Component
public class UserResource extends GenericAuthenticationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private LocalityService localityService;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private UserService userService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private String baseUrl;
    @Autowired
    private String apiPath;

    @POST
    @Produces({UserMIME.ME, ErrorMIME.ERROR})
    @Consumes(UserMIME.CREATE_DOCTOR)
    public Response createDoctor(
            DoctorSignUp doctorSignUp,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @Context HttpHeaders httpheaders) {
        this.assertCreateUserConditions(httpheaders, request, doctorSignUp.getUser().getEmail());

        Optional<Locality> locality = this.localityService.findById(doctorSignUp.getLocalityId());
        if (!locality.isPresent()) {
            throw this.unprocessableEntity(ErrorConstants.USER_CREATE_NONEXISTENT_LOCALITY);
        }

        User user = this.copyUser(doctorSignUp);

        Office office = new Office();
        office.setLocality(locality.get());
        office.setStreet(doctorSignUp.getStreet());
        office.setName("Consultorio de " + user.getFirstName() + " " + user.getSurname());
        office.setPhone(doctorSignUp.getUser().getPhone());
        office.setPhone(doctorSignUp.getUser().getEmail());

        Doctor doctor = new Doctor();
        doctor.setOffice(office);
        doctor.setDoctorSpecialties(Collections.emptyList());
        doctor.setRegistrationNumber(doctorSignUp.getRegistrationNumber());
        doctor.setPhone(office.getPhone());
        doctor.setEmail(office.getEmail());

        User newUser = this.userService.createAsDoctor(user, doctor);
        this.finishSignUp(request, response, doctorSignUp, newUser);
        return Response
                .created(this.joinPaths(this.baseUrl, this.apiPath, "/users/" + newUser.getId()))
                .entity(UserMeFactory.withDoctors(newUser, Collections.singletonList(doctor)))
                .type(UserMIME.ME)
                .build();
    }

    @POST
    @Produces({UserMIME.ME, ErrorMIME.ERROR})
    @Consumes(UserMIME.CREATE_PATIENT)
    public Response createPatient(
            PatientSignUp patientSignUp,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @Context HttpHeaders httpheaders) {
        this.assertCreateUserConditions(httpheaders, request, patientSignUp.getUser().getEmail());

        User newUser = this.userService.create(this.copyUser(patientSignUp));
        this.finishSignUp(request, response, patientSignUp, newUser);
        return Response
                .created(this.joinPaths(this.baseUrl, this.apiPath, "/users/" + newUser.getId()))
                .entity(UserMeFactory.withPatients(newUser, Collections.emptyList()))
                .type(UserMIME.ME)
                .build();
    }

    @GET
    @Path("{id}")
    @Produces({UserMIME.GET, ErrorMIME.ERROR})
    public Response getEntity(
            @Context HttpHeaders httpheaders,
            @Context HttpServletRequest request,
            @PathParam("id") Integer id) {
        MIMEHelper.assertServerType(httpheaders, UserMIME.GET);

        if (id == null) throw this.missingPathParams();

        return Response.ok(this.assertUserNotFound(request)).type(UserMIME.GET).build();
    }

    @GET
    @Path("{id}/picture")
    @Produces({PictureMIME.IMAGES})
    public Response getProfilePicture(
            @Context HttpHeaders httpheaders,
            @PathParam("id") Integer id) {

        byte[] picture;
        String mime;
        if (id == null) {
            picture = this.getDefaultProfilePicture();
            mime = PictureMIME.SVG;
        } else {
            Optional<User> userOptional = this.userService.findById(id);
            if (!userOptional.isPresent() || userOptional.get().getProfilePictureId() == null) {
                picture = this.getDefaultProfilePicture();
                mime = PictureMIME.SVG;
            } else {
                Optional<Picture> pictureObject = this.pictureService.findById(userOptional.get().getProfilePictureId());
                if (!pictureObject.isPresent()) {
                    picture = this.getDefaultProfilePicture();
                    mime = PictureMIME.SVG;
                } else {
                    picture = pictureObject.get().getData();
                    mime = pictureObject.get().getMimeType();
                }
            }
        }

        return Response
                .ok(picture)
                .type(mime)
                .build();
    }

    @POST
    @Path("{id}/picture")
    @Produces({ApplicationMIME.WILDCARD, ErrorMIME.ERROR})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response setProfilePicture(
            @Context HttpHeaders httpheaders,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @FormDataParam("picture") InputStream pictureFile,
            @FormDataParam("picture") FormDataBodyPart pictureDetails,
            @PathParam("id") Integer id) {

        if (id == null) throw this.missingPathParams();

        User user = this.assertUserUnauthorized(request);

        Optional<User> userPath = this.userService.findById(id);
        if (!userPath.isPresent())
            throw this.notFound();
        if (!userPath.get().getId().equals(user.getId()))
            throw this.forbidden();

        if (!pictureDetails.getMediaType().toString().toLowerCase().startsWith("image"))
            throw this.error(Status.BAD_REQUEST).withReason(ErrorConstants.INVALID_IMAGE_TYPE).getError();

        Picture picture = new Picture();
        try {
            picture.setData(IOUtils.toByteArray(pictureFile));
        } catch (IOException e) {
            throw this.error(Status.BAD_REQUEST).withReason(ErrorConstants.INVALID_IMAGE_TYPE).getError();
        }
        picture.setSize((long) picture.getData().length);
        picture.setName(pictureDetails.getName());
        picture.setMimeType(pictureDetails.getMediaType().toString());

        if (picture.getSize() > Constants.MAX_PROFILE_PICTURE_SIZE) {
            throw UnprocessableEntityException
                    .build()
                    .withReason(ErrorConstants.IMAGE_TOO_BIG)
                    .getError();
        }

        user = this.pictureService.changeProfilePic(user, picture);

        return Response
                .created(this.joinPaths(this.baseUrl, this.apiPath, "/users/" + user.getId().toString() + "/picture"))
                .build();
    }

    @GET
    @Produces({UserMIME.ME, ErrorMIME.ERROR})
    public Response getLoggedUser(
            @Context HttpServletRequest request,
            @Context HttpHeaders httpheaders) {
        MIMEHelper.assertServerType(httpheaders, UserMIME.ME);

        User user = this.assertUserUnauthorized(request);

        UserMe userMe;
        if (this.isDoctor()) {
            userMe = UserMeFactory.withDoctors(user, this.doctorService.findByUser(user));
        } else {
            userMe = UserMeFactory.withPatients(user, this.patientService.findByUser(user));
        }

        return Response
                .ok()
                .entity(userMe)
                .type(UserMIME.ME)
                .build();
    }

    @PUT
    @Path("{id}")
    @Produces({UserMIME.GET, ErrorMIME.ERROR})
    @Consumes(UserMIME.UPDATE)
    public Response updateEntity(
            User user,
            @Context HttpServletRequest request,
            @Context HttpHeaders httpheaders,
            @PathParam("id") Integer id) {
        MIMEHelper.assertServerType(httpheaders, UserMIME.GET);

        if (id == null || user == null) throw this.missingBodyParams();

        boolean modifyUser = false;
        User savedUser = this.assertUserNotFound(request);
        if (user.getEmail() != null) {
            savedUser.setEmail(user.getEmail());
            modifyUser = true;
        } if (user.getPhone() != null) {
            savedUser.setPhone(user.getPhone());
            if (!modifyUser) modifyUser = true;
        } if (user.getSurname() != null) {
            savedUser.setSurname(user.getSurname());
            if (!modifyUser) modifyUser = true;
        } if (user.getFirstName() != null) {
            savedUser.setFirstName(user.getFirstName());
            if (!modifyUser) modifyUser = true;
        }

        if (modifyUser)
            this.userService.update(savedUser);

        if (user.getPassword() != null)
            this.userService.updatePassword(savedUser, user.getPassword());

        return Response.status(Status.OK).entity(savedUser).type(UserMIME.GET).build();
    }

    private User copyUser(UserSignUp userSignUp) {
        User user = new User();

        user.setFirstName(userSignUp.getUser().getFirstName());
        user.setSurname(userSignUp.getUser().getSurname());
        user.setPhone(userSignUp.getUser().getPhone());
        user.setEmail(userSignUp.getUser().getEmail());
        user.setPassword(userSignUp.getUser().getPassword());

        return user;
    }

    private void finishSignUp(HttpServletRequest request, HttpServletResponse response, UserSignUp userSignUp, User newUser) {
        this.userService.generateVerificationToken(newUser, request.getLocale(), "/verify");

        UserCredentials credentials = new UserCredentials();
        credentials.setUsername(userSignUp.getUser().getEmail());
        credentials.setPassword(userSignUp.getUser().getPassword());

        this.createJWTCookies(credentials, newUser, response, null, LOGGER);
    }

    private byte[] getDefaultProfilePicture() {
        try {
            return IOUtils.toByteArray(new FileInputStream(new ClassPathResource("/defaultProfilePic.svg").getFile()));
        } catch (IOException e) {
            LOGGER.error("Could not found defaultProfilePic.svg");
            return null;
        }
    }

    private void assertCreateUserConditions(HttpHeaders httpheaders, HttpServletRequest request, String email) {
        MIMEHelper.assertServerType(httpheaders, UserMIME.ME);

        Optional<User> userOptional = this.getUser(request);
        if (userOptional.isPresent()) throw this.forbidden();

        if (this.userService.findByUsername(email).isPresent())
            throw ConflictException.build().withReason(ErrorConstants.USER_EMAIL_USED).getError();
    }
}

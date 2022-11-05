package ar.edu.itba.paw.webapp.rest;

import ar.edu.itba.paw.interfaces.MediCareException;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.WorkdayService;
import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Workday;
import ar.edu.itba.paw.webapp.media_types.ErrorMIME;
import ar.edu.itba.paw.webapp.media_types.MIMEHelper;
import ar.edu.itba.paw.webapp.media_types.WorkdayMIME;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import ar.edu.itba.paw.webapp.rest.utils.GenericResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Path("/workdays")
@Component
public class WorkdayResource extends GenericResource {
    @Autowired
    private WorkdayService workdayService;
    @Autowired
    private DoctorService doctorService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkdayResource.class);

    @GET
    @Produces({WorkdayMIME.GET_LIST, ErrorMIME.ERROR})
    public Response getCollection(
            @Context HttpServletRequest request,
            @Context HttpHeaders httpheaders) {
        MIMEHelper.assertServerType(httpheaders, WorkdayMIME.GET_LIST);

        User user = this.assertUserUnauthorized(request);
        if (!this.isDoctor()) throw this.forbidden();
        return Response
                .ok()
                .entity(this.workdayService.findByUser(user))
                .type(WorkdayMIME.GET_LIST)
                .build();
    }

    @POST
    @Produces({WorkdayMIME.GET_LIST, ErrorMIME.ERROR})
    @Consumes(WorkdayMIME.CREATE_LIST)
    public Response createEntities(
            Collection<Workday> workdays,
            @Context HttpServletRequest request,
            @Context HttpHeaders httpheaders) {
        MIMEHelper.assertServerType(httpheaders, WorkdayMIME.GET_LIST);

        if (workdays == null || workdays.isEmpty()) throw this.missingBodyParams();

        if (!this.isDoctor()) throw this.forbidden();
        User user = this.assertUserUnauthorized(request);
        Optional<Doctor> doctor = this.doctorService.findByUser(user).stream().findFirst();
        if (!doctor.isPresent()) {
            throw this.forbidden();
        }

        for (Workday workday : workdays) {
            if (workday.getStartHour() > workday.getEndHour()
                    || ((workday.getStartHour().equals(workday.getEndHour())) && (workday.getStartHour() > workday.getEndHour()))) {

                throw this.unprocessableEntity(ErrorConstants.DATE_FROM_IS_AFTER_TO);
            }

            workday.setDoctor(doctor.get());
        }

        Collection<Workday> newWorkdays;
        try {
            newWorkdays = this.workdayService.create(workdays);
        } catch (MediCareException e) {
            throw this.unprocessableEntity(ErrorConstants.WORKDAY_CREATE_OVERLAPS);
        }

        return Response
                .status(Status.CREATED)
                .entity(newWorkdays)
                .type(WorkdayMIME.GET_LIST)
                .build();
    }

    @GET
    @Path("{id}")
    @Produces({WorkdayMIME.GET, ErrorMIME.ERROR})
    public Response getEntity(
            @Context HttpHeaders httpheaders,
            @Context HttpServletRequest request,
            @PathParam("id") Integer id) {
        MIMEHelper.assertServerType(httpheaders, WorkdayMIME.GET);

        if (id == null) throw this.missingPathParams();

        User user = this.assertUserUnauthorized(request);
        if (!this.isDoctor()) throw this.forbidden();

        List<Doctor> doctors = this.doctorService.findByUser(user);
        if (doctors.isEmpty()) {
            LOGGER.error("Couldnt find a doctor for this user. This situation should be impossible");
            throw this.forbidden();
        }

        Optional<Workday> workdayOptional = this.workdayService.findById(id);
        if (!workdayOptional.isPresent()) throw this.notFound();

        if (!doctors.contains(workdayOptional.get().getDoctor()))
            throw this.notFound();

        return Response
                .ok()
                .entity(workdayOptional.get())
                .type(WorkdayMIME.GET)
                .build();
    }

    @DELETE
    @Path("{id}")
    @Produces({MediaType.WILDCARD, ErrorMIME.ERROR})
    public Response deleteEntity(
            @Context HttpHeaders httpheaders,
            @Context HttpServletRequest request,
            @PathParam("id") Integer id) {
        if (id == null) throw this.missingPathParams();

        User user = this.assertUserUnauthorized(request);
        if (!this.isDoctor()) throw this.forbidden();

        try {
            this.workdayService.remove(id, user);
        } catch (MediCareException | IllegalArgumentException e){
            throw this.notFound();
        }

        return Response
                .status(Status.NO_CONTENT)
                .build();
    }
}

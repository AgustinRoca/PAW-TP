package ar.edu.itba.paw.webapp.rest;

import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.Patient;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.media_types.AppointmentMIME;
import ar.edu.itba.paw.webapp.media_types.ErrorMIME;
import ar.edu.itba.paw.webapp.media_types.MIMEHelper;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import ar.edu.itba.paw.webapp.rest.utils.GenericResource;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
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
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

@Path("/appointments")
@Component
public class AppointmentResource extends GenericResource {
    private static final long MAX_DAYS_APPOINTMENTS = 62;
    private static final int MIN_YEAR = 2000;
    private static final int MAX_YEAR = 2100;

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;

    @GET
    @Produces({AppointmentMIME.GET_LIST, ErrorMIME.ERROR})
    public Response getEntity(
            @Context HttpHeaders httpheaders,
            @Context HttpServletRequest request,
            @QueryParam("from_year") Integer fromYear,
            @QueryParam("from_month") Integer fromMonth,
            @QueryParam("from_day") Integer fromDay,
            @QueryParam("to_year") Integer toYear,
            @QueryParam("to_month") Integer toMonth,
            @QueryParam("to_day") Integer toDay)
    {
        MIMEHelper.assertServerType(httpheaders, AppointmentMIME.GET_LIST);

        User user = this.assertUserUnauthorized(request);
        if (!user.getVerified())
            throw this.forbidden();

        Collection<Doctor> doctors;
        Collection<Patient> patients;
        if (this.isDoctor()) {
            patients = Collections.emptyList();
            doctors = this.doctorService.findByUser(user);
            if (doctors.isEmpty())
                return Response.ok(Collections.emptyList()).type(AppointmentMIME.GET_LIST).build();
        } else {
            doctors = Collections.emptyList();
            patients = this.patientService.findByUser(user);
            if (patients.isEmpty())
                return Response.ok(Collections.emptyList()).type(AppointmentMIME.GET_LIST).build();
        }

        if (fromYear == null || fromMonth == null || fromDay == null || toYear == null || toMonth == null || toDay == null)
            throw this.missingQueryParams();
        if (fromYear < MIN_YEAR || toYear > MAX_YEAR)
            throw this.invalidQueryParams();

        LocalDateTime dateFrom, dateTo;
        try {
            dateFrom = new LocalDateTime(fromYear, fromMonth, fromDay, 0, 0);
            dateTo = new LocalDateTime(toYear, toMonth, toDay, 23, 59, 59, 999);
        } catch (Exception e) {
            throw this.invalidQueryParams();
        }

        long daysBetween = Days.daysBetween(dateFrom.toLocalDate(), dateTo.toLocalDate()).getDays();
        if (daysBetween > MAX_DAYS_APPOINTMENTS) {
            throw this.unprocessableEntity(ErrorConstants.DATE_RANGE_TOO_BROAD);
        } else if (dateTo.isBefore(dateFrom)) {
            throw this.unprocessableEntity(ErrorConstants.DATE_FROM_IS_AFTER_TO);
        }

        if (this.isDoctor()) {
            return Response
                    .ok(this.appointmentService.findPendingAppointmentsOfDoctorsInDateInterval(doctors, dateFrom, dateTo))
                    .type(AppointmentMIME.GET_LIST)
                    .build();
        } else {
            return Response
                    .ok(this.appointmentService.findPendingAppointmentsOfPatientsInDateInterval(patients, dateFrom, dateTo))
                    .type(AppointmentMIME.GET_LIST)
                    .build();
        }
    }

    @POST
    @Produces({AppointmentMIME.GET, ErrorMIME.ERROR})
    @Consumes(AppointmentMIME.CREATE)
    public Response createEntity(
            Appointment appointment,
            @Context HttpServletRequest request,
            @Context HttpHeaders httpheaders) {
        MIMEHelper.assertServerType(httpheaders, AppointmentMIME.GET);

        User user = this.assertUserUnauthorized(request);
        if (appointment == null || appointment.getFromDate() == null)
            throw this.missingBodyParams();
        if (appointment.getFromDate().isBefore(LocalDateTime.now())) {
            throw this.unprocessableEntity(ErrorConstants.DATE_FROM_IS_AFTER_TO);
        }
        if (this.isDoctor())
            throw this.error(Status.FORBIDDEN).getError();

        Optional<Doctor> doctorOptional = this.doctorService.findById(appointment.getDoctor().getId());
        if (!doctorOptional.isPresent()) {
            throw this.unprocessableEntity(ErrorConstants.APPOINTMENT_CREATE_NONEXISTENT_DOCTOR);
        }

        Appointment newAppointment = new Appointment();
        newAppointment.setDoctor(doctorOptional.get());
        newAppointment.setMotive(appointment.getMotive());
        newAppointment.setMessage(appointment.getMessage());
        newAppointment.setFromDate(appointment.getFromDate());

        if (request.getLocale() != null)
            newAppointment.setLocale(request.getLocale());
        else
            newAppointment.setLocale(Locale.ENGLISH);

        return Response
                .status(Status.CREATED)
                .entity(this.appointmentService.create(newAppointment, user))
                .type(AppointmentMIME.GET)
                .build();
    }

    @GET
    @Path("{id}")
    @Produces({AppointmentMIME.GET, ErrorMIME.ERROR})
    public Response getEntity(
            @Context HttpHeaders httpheaders,
            @Context HttpServletRequest request,
            @PathParam("id") Integer id) {
        MIMEHelper.assertServerType(httpheaders, AppointmentMIME.GET);

        if (id == null) throw this.missingPathParams();

        User user = this.assertUserUnauthorized(request);
        if (!user.getVerified())
            throw this.forbidden();

        Collection<Doctor> doctors;
        Collection<Patient> patients;
        if (this.isDoctor()) {
            patients = Collections.emptyList();
            doctors = this.doctorService.findByUser(user);
            if (doctors.isEmpty())
                throw this.notFound();
        } else {
            doctors = Collections.emptyList();
            patients = this.patientService.findByUser(user);
            if (patients.isEmpty())
                throw this.notFound();
        }

        Optional<Appointment> appointmentOptional = this.appointmentService.findById(id);
        if (!appointmentOptional.isPresent())
            throw this.notFound();

        if (this.isDoctor()) {
            if (!doctors.contains(appointmentOptional.get().getDoctor()))
                throw this.notFound();
        } else {
            if (!patients.contains(appointmentOptional.get().getPatient()))
                throw this.notFound();
        }

        return Response.ok(appointmentOptional.get()).type(AppointmentMIME.GET).build();
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

        if (!user.getVerified())
            throw this.forbidden();

        Optional<Appointment> appointmentOptional = this.appointmentService.findById(id);
        if (!appointmentOptional.isPresent())
            throw this.notFound();

        if (this.isDoctor()) {
            if (!user.getId().equals(appointmentOptional.get().getDoctor().getUser().getId())) {
                throw this.notFound();
            }
        } else {
            if (!user.getId().equals(appointmentOptional.get().getPatient().getUser().getId())) {
                throw this.notFound();
            }
        }

        this.appointmentService.remove(appointmentOptional.get().getId(), user, appointmentOptional.get().getLocale());

        return Response.noContent().build();
    }
}

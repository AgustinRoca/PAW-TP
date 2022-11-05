package ar.edu.itba.paw.webapp.rest;

import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.media_types.AppointmentTimeSlotMIME;
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
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/doctors/{doctorId}/appointmentTimeSlots")
@Component
public class AppointmentTimeSlotResource extends GenericResource {
    private static final long MAX_DAYS_APPOINTMENTS = 7;

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private DoctorService doctorService;

    @GET
    @Produces({AppointmentTimeSlotMIME.GET_LIST, ErrorMIME.ERROR})
    public Response getCollection(
            @Context HttpHeaders httpheaders,
            @Context HttpServletRequest request,
            @PathParam("doctorId") Integer doctorId,
            @QueryParam("from_year") Integer fromYear,
            @QueryParam("from_month") Integer fromMonth,
            @QueryParam("from_day") Integer fromDay,
            @QueryParam("to_year") Integer toYear,
            @QueryParam("to_month") Integer toMonth,
            @QueryParam("to_day") Integer toDay) {
        MIMEHelper.assertServerType(httpheaders, AppointmentTimeSlotMIME.GET_LIST);

        Optional<User> user = this.getUser(request);
        if (!user.isPresent() || !user.get().getVerified())
            throw this.forbidden();

        if (fromYear == null || fromMonth == null || fromDay == null || toYear == null || toMonth == null || toDay == null || doctorId == null)
            throw this.missingQueryParams();

        Optional<Doctor> doctorOptional = this.doctorService.findById(doctorId);
        if (!doctorOptional.isPresent()) {
            throw this.unprocessableEntity(ErrorConstants.APPOINTMENT_TIME_SLOT_GET_NONEXISTENT_DOCTOR);
        }

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

        return Response
                .ok(this.appointmentService.findAvailableTimeslotsInDateInterval(doctorOptional.get(), dateFrom, dateTo))
                .type(AppointmentTimeSlotMIME.GET_LIST)
                .build();
    }
}

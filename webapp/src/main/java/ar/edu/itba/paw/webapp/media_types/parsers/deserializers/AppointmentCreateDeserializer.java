package ar.edu.itba.paw.webapp.media_types.parsers.deserializers;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.webapp.exceptions.UnprocessableEntityException;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.LocalDateTime;

import javax.ws.rs.BadRequestException;

public class AppointmentCreateDeserializer extends JsonDeserializer<Appointment> {
    public static final AppointmentCreateDeserializer instance = new AppointmentCreateDeserializer();
    private static final int MIN_YEAR = (new LocalDateTime()).getYear() - 2;
    private static final int MAX_YEAR = (new LocalDateTime()).getYear() + 2;

    private static final int MIN_HOUR = 0;
    private static final int MAX_HOUR = 23;
    private static final int MIN_MINUTE = 0;
    private static final int MAX_MINUTE = 59;

    private static final int MIN_DAY = 1;
    private static final int MAX_DAY = 31;
    private static final int MIN_MONTH = 1;
    private static final int MAX_MONTH = 12;


    private AppointmentCreateDeserializer() {}

    @Override
    public Appointment fromJson(JsonNode o) {
        if (!(o instanceof ObjectNode)) {
            throw new BadRequestException();
        }

        ObjectNode jsonObject = (ObjectNode) o;
        JsonNode node;

        Appointment appointment = new Appointment();

        ObjectNode dateNode = this.getObjectNonNull(
                jsonObject,
                "date_from",
                ErrorConstants.APPOINTMENT_CREATE_MISSING_DATE_FROM,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM
        );

        int year = this.getIntegerNonNull(
                dateNode,
                "year",
                integer -> integer >= MIN_YEAR && integer <= MAX_YEAR,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM
        );
        int month = this.getIntegerNonNull(
                dateNode,
                "month",
                integer -> integer >= MIN_MONTH && integer <= MAX_MONTH,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM
        );
        int day = this.getIntegerNonNull(
                dateNode,
                "day",
                integer -> integer >= MIN_DAY && integer <= MAX_DAY,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM
        );
        int hour = this.getIntegerNonNull(
                dateNode,
                "hour",
                integer -> integer >= MIN_HOUR && integer <= MAX_HOUR,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM
        );
        int minute = this.getIntegerNonNull(
                dateNode,
                "minute",
                integer -> integer >= MIN_MINUTE && integer <= MAX_MINUTE,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM
        );

        try {
            appointment.setFromDate(new LocalDateTime(year, month, day, hour, minute));
        } catch (Exception e) {
            throw UnprocessableEntityException
                    .build()
                    .withReason(ErrorConstants.APPOINTMENT_CREATE_INVALID_DATE_FROM)
                    .getError();
        }

        String s = this.getStringNull(jsonObject, "motive", ErrorConstants.APPOINTMENT_CREATE_INVALID_MOTIVE);
        if (s != null) appointment.setMotive(s);

        s = this.getStringNull(jsonObject, "message", ErrorConstants.APPOINTMENT_CREATE_INVALID_MESSAGE);
        if (s != null) appointment.setMessage(s);

        Doctor doctor = new Doctor();

        doctor.setId(this.getIntegerNonNull(
                jsonObject,
                "doctorId",
                ErrorConstants.APPOINTMENT_CREATE_MISSING_DOCTOR_ID,
                ErrorConstants.APPOINTMENT_CREATE_INVALID_DOCTOR_ID
        ));
        appointment.setDoctor(doctor);

        return appointment;
    }
}

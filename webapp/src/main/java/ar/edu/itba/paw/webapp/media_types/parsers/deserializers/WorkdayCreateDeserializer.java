package ar.edu.itba.paw.webapp.media_types.parsers.deserializers;

import ar.edu.itba.paw.models.Workday;
import ar.edu.itba.paw.models.WorkdayDay;
import ar.edu.itba.paw.webapp.exceptions.UnprocessableEntityException;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.BadRequestException;

public class WorkdayCreateDeserializer extends JsonDeserializer<Workday> {
    public static final WorkdayCreateDeserializer instance = new WorkdayCreateDeserializer();

    private static final int MIN_HOUR = 0;
    private static final int MAX_HOUR = 23;
    private static final int MIN_MINUTE = 0;
    private static final int MAX_MINUTE = 59;

    private WorkdayCreateDeserializer() {}

    @Override
    public Workday fromJson(JsonNode o) {
        if (!(o instanceof ObjectNode)) {
            throw new BadRequestException();
        }

        ObjectNode jsonObject = (ObjectNode) o;

        Workday workday = new Workday();
        JsonNode node = jsonObject.get("day");
        if (node != null) {
            try {
                workday.setDay(WorkdayDay.valueOf(node.asText()));
            } catch (Exception e) {
                throw UnprocessableEntityException
                        .build()
                        .withReason(ErrorConstants.WORKDAY_CREATE_INVALID_DAY)
                        .getError();
            }
        } else {
            throw UnprocessableEntityException
                    .build()
                    .withReason(ErrorConstants.WORKDAY_CREATE_MISSING_DAY)
                    .getError();
        }

        ObjectNode startTime = this.getObjectNonNull(
                jsonObject,
                "start",
                ErrorConstants.WORKDAY_CREATE_MISSING_START,
                ErrorConstants.WORKDAY_CREATE_INVALID_START
        );
        ObjectNode endTime = this.getObjectNonNull(
                jsonObject,
                "end",
                ErrorConstants.WORKDAY_CREATE_MISSING_END,
                ErrorConstants.WORKDAY_CREATE_INVALID_END
        );

        workday.setStartHour(this.getIntegerNonNull(
                startTime,
                "hour",
                integer -> integer >= MIN_HOUR && integer <= MAX_HOUR,
                ErrorConstants.WORKDAY_CREATE_MISSING_START_HOUR,
                ErrorConstants.WORKDAY_CREATE_INVALID_START_HOUR
        ));
        workday.setStartMinute(this.getIntegerNonNull(
                startTime,
                "minute",
                integer -> integer >= MIN_MINUTE && integer <= MAX_MINUTE,
                ErrorConstants.WORKDAY_CREATE_MISSING_START_MINUTE,
                ErrorConstants.WORKDAY_CREATE_INVALID_START_MINUTE
        ));

        workday.setEndHour(this.getIntegerNonNull(
                endTime,
                "hour",
                integer -> integer >= MIN_HOUR && integer <= MAX_HOUR,
                ErrorConstants.WORKDAY_CREATE_MISSING_END_HOUR,
                ErrorConstants.WORKDAY_CREATE_INVALID_END_HOUR
        ));
        workday.setEndMinute(this.getIntegerNonNull(
                endTime,
                "minute",
                integer -> integer >= MIN_MINUTE && integer <= MAX_MINUTE,
                ErrorConstants.WORKDAY_CREATE_MISSING_END_MINUTE,
                ErrorConstants.WORKDAY_CREATE_INVALID_END_MINUTE
        ));

        return workday;
    }
}

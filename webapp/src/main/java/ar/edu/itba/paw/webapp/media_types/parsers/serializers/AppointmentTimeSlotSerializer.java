package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentTimeSlot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AppointmentTimeSlotSerializer extends JsonSerializer<AppointmentTimeSlot> {
    public static final AppointmentTimeSlotSerializer instance = new AppointmentTimeSlotSerializer();

    private AppointmentTimeSlotSerializer() {}

    @Override
    public JsonNode toJson(AppointmentTimeSlot timeSlot) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("hour", timeSlot.getDate().getHourOfDay());
        jsonObject.put("minute", timeSlot.getDate().getMinuteOfHour());
        jsonObject.put("duration", Appointment.DURATION);

        return jsonObject;
    }

    @Override
    public ArrayNode toJsonArray(Collection<AppointmentTimeSlot> appointmentTimeSlots) {
        ArrayNode jsonArray = JsonNodeFactory.instance.arrayNode();

        Map<LocalDate, ArrayNode> timeSlotsPerDay = new HashMap<>();
        for (AppointmentTimeSlot appointmentTimeSlot : appointmentTimeSlots) {
            ArrayNode transformedTimeSlots = timeSlotsPerDay.computeIfAbsent(appointmentTimeSlot.getDate().toLocalDate(), k -> JsonNodeFactory.instance.arrayNode());
            transformedTimeSlots.add(this.toJson(appointmentTimeSlot));
        }

        for (Map.Entry<LocalDate, ArrayNode> dateList : timeSlotsPerDay.entrySet()) {
            ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();
            jsonObject.replace("date", this.transformDate(dateList.getKey()));
            jsonObject.replace("timeslots", dateList.getValue());
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

    private ObjectNode transformDate(LocalDate date) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("year", date.getYear());
        jsonObject.put("month", date.getMonthOfYear());
        jsonObject.put("day", date.getDayOfMonth());

        return jsonObject;
    }
}

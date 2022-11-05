package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.Workday;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WorkdaySerializer extends JsonSerializer<Workday> {
    public static final WorkdaySerializer instance = new WorkdaySerializer();

    private WorkdaySerializer() {}

    @Override
    public JsonNode toJson(Workday workday) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("id", workday.getId());
        jsonObject.replace("start", this.timeToJson(workday.getStartHour(), workday.getStartMinute()));
        jsonObject.replace("end", this.timeToJson(workday.getEndHour(), workday.getEndMinute()));
        jsonObject.put("day", workday.getDay().name());
        jsonObject.put("doctorId", workday.getDoctor().getId());

        return jsonObject;
    }

    private ObjectNode timeToJson(int hour, int minute) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("hour", hour);
        jsonObject.put("minute", minute);

        return jsonObject;
    }
}

package ar.edu.itba.paw.models;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDateTime;

import java.util.Calendar;

public enum WorkdayDay {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(0);

    private int dow;

    WorkdayDay(int dow) {
        this.dow = dow;
    }

    public static WorkdayDay from(Calendar calendar) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return MONDAY;
            case Calendar.TUESDAY:
                return TUESDAY;
            case Calendar.WEDNESDAY:
                return WEDNESDAY;
            case Calendar.THURSDAY:
                return THURSDAY;
            case Calendar.FRIDAY:
                return FRIDAY;
            case Calendar.SATURDAY:
                return SATURDAY;
            case Calendar.SUNDAY:
                return SUNDAY;
            default:
                return null;
        }
    }

    public static WorkdayDay from(LocalDateTime localDate) {
        switch (localDate.getDayOfWeek()) {
            case DateTimeConstants.MONDAY:
                return MONDAY;
            case DateTimeConstants.TUESDAY:
                return TUESDAY;
            case DateTimeConstants.WEDNESDAY:
                return WEDNESDAY;
            case DateTimeConstants.THURSDAY:
                return THURSDAY;
            case DateTimeConstants.FRIDAY:
                return FRIDAY;
            case DateTimeConstants.SATURDAY:
                return SATURDAY;
            case DateTimeConstants.SUNDAY:
                return SUNDAY;
        }
        return null;
    }

    public static WorkdayDay from(int dow) {
        switch (dow) {
            case DateTimeConstants.MONDAY:
                return MONDAY;
            case DateTimeConstants.TUESDAY:
                return TUESDAY;
            case DateTimeConstants.WEDNESDAY:
                return WEDNESDAY;
            case DateTimeConstants.THURSDAY:
                return THURSDAY;
            case DateTimeConstants.FRIDAY:
                return FRIDAY;
            case DateTimeConstants.SATURDAY:
                return SATURDAY;
            case DateTimeConstants.SUNDAY:
                return SUNDAY;
        }
        return null;
    }

    public int toInteger() {
        return this.dow;
    }
}

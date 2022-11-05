package ar.edu.itba.paw.models;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.Objects;

public class AppointmentTimeSlot {
    private LocalDateTime date;

    public LocalDateTime getDate() {
        return this.date;
    }

    public LocalDateTime getToDate() {
        return this.date.plusMinutes(Appointment.DURATION);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentTimeSlot)) return false;
        AppointmentTimeSlot that = (AppointmentTimeSlot) o;
        if (this.date == null && that.date != null) return false;
        if (this.date != null && that.date == null) return false;
        if (that.date == null) return false;
        return this.date.toDateTime(DateTimeZone.UTC).getMillis() == that.date.toDateTime(DateTimeZone.UTC).getMillis();
    }

    @Override
    public int hashCode() {
        if (this.date == null)
            return Objects.hashCode(null);
        return Objects.hashCode(this.date.toDateTime(DateTimeZone.UTC).getMillis());
    }
}

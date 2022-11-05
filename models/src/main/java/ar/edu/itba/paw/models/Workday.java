package ar.edu.itba.paw.models;

import org.joda.time.LocalTime;

import javax.persistence.*;

@Entity
@Table(
        name = "workday",
        indexes = {
                @Index(columnList = "workday_id", name = "workday_workday_id_uindex", unique = true),
                @Index(columnList = "day", name = "workday_day_index")
        }
)
public class Workday extends GenericModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workday_workday_id_seq")
    @SequenceGenerator(sequenceName = "workday_workday_id_seq", name = "workday_workday_id_seq", allocationSize = 1)
    @Column(name = "workday_id")
    private Integer id;
    @Column(name = "start_hour", nullable = false)
    private Integer startHour;
    @Column(name = "end_hour", nullable = false)
    private Integer endHour;
    @Column(name = "start_minute", nullable = false)
    private Integer startMinute;
    @Column(name = "end_minute", nullable = false)
    private Integer endMinute;
    @JoinColumn(name = "day", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkdayDay day;
    @JoinColumn(name = "doctor_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Doctor doctor;

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Integer getStartHour() {
        return this.startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getEndHour() {
        return this.endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public WorkdayDay getDay() {
        return this.day;
    }

    public void setDay(WorkdayDay day) {
        this.day = day;
    }

    public Integer getStartMinute() {
        return this.startMinute;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public Integer getEndMinute() {
        return this.endMinute;
    }

    public void setEndMinute(Integer endMinute) {
        this.endMinute = endMinute;
    }

    public LocalTime getStartTime(){
        return new LocalTime(startHour, startMinute);
    }

    public LocalTime getEndTime(){
        return new LocalTime(endHour, endMinute);
    }

    @Override
    protected boolean isSameType(Object o) {
        return o instanceof Workday;
    }
}

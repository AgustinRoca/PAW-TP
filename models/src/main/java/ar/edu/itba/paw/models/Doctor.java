package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;

@Entity
@Table(
        name = "doctor",
        indexes = {
                @Index(columnList = "doctor_id", name = "doctor_doctor_id_uindex", unique = true),
                @Index(columnList = "user_id", name = "doctor_user_id_index")
        }
)
public class Doctor extends GenericModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doctor_doctor_id_seq")
    @SequenceGenerator(sequenceName = "doctor_doctor_id_seq", name = "doctor_doctor_id_seq", allocationSize = 1)
    @Column(name = "doctor_id")
    private Integer id;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "registration_number")
    private Integer registrationNumber;
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    @JoinColumn(name = "office_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Office office;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "system_doctor_specialty_doctor",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    @OrderBy("name ASC")
    private Collection<DoctorSpecialty> doctorSpecialties = new LinkedList<>();

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRegistrationNumber() {
        return this.registrationNumber == null ? 0 : this.registrationNumber;
    }

    public void setRegistrationNumber(Integer registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Office getOffice() {
        return this.office;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public Collection<DoctorSpecialty> getDoctorSpecialties() {
        return this.doctorSpecialties;
    }

    public void setDoctorSpecialties(Collection<DoctorSpecialty> doctorSpecialties) {
        this.doctorSpecialties = doctorSpecialties;
    }

    @Override
    protected boolean isSameType(Object o) {
        return o instanceof Doctor;
    }
}

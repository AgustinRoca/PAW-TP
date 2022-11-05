package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(
        name = "system_doctor_specialty",
        indexes = {
                @Index(columnList = "specialty_id", name = "specialty_specialty_id_uindex", unique = true)
        }
)
public class DoctorSpecialty extends GenericModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_doctor_specialty_specialty_id_seq")
    @SequenceGenerator(sequenceName = "system_doctor_specialty_specialty_id_seq", name = "system_doctor_specialty_specialty_id_seq", allocationSize = 1)
    @Column(name = "specialty_id")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected boolean isSameType(Object o) {
        return o instanceof DoctorSpecialty;
    }
}

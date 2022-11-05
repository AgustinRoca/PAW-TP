package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(
        name = "patient",
        indexes = {
                @Index(columnList = "patient_id", name = "patient_patient_id_uindex", unique = true),
        }
)
public class Patient extends GenericModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_patient_id_seq")
    @SequenceGenerator(sequenceName = "patient_patient_id_seq", name = "patient_patient_id_seq", allocationSize = 1)
    @Column(name = "patient_id")
    private Integer id;
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User user;
    @JoinColumn(name = "office_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Office office;

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Office getOffice() {
        return this.office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    @Override
    protected boolean isSameType(Object o) {
        return o instanceof Patient;
    }
}

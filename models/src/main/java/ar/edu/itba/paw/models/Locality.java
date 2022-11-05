package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(
        name = "system_locality",
        indexes = {
                @Index(columnList = "locality_id", name = "system_locality_locality_id_uindex", unique = true),
        }
)
public class Locality extends GenericModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_locality_locality_id_seq")
    @SequenceGenerator(sequenceName = "system_locality_locality_id_seq", name = "system_locality_locality_id_seq", allocationSize = 1)
    @Column(name = "locality_id")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

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

    public Province getProvince() {
        return this.province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    @Override
    protected boolean isSameType(Object o) {
        return o instanceof Locality;
    }
}

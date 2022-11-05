package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(
        name = "system_province",
        indexes = {
                @Index(columnList = "province_id", name = "system_province_province_id_uindex", unique = true),
        }
)
public class Province extends GenericModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_province_province_id_seq")
    @SequenceGenerator(sequenceName = "system_province_province_id_seq", name = "system_province_province_id_seq", allocationSize = 1)
    @Column(name = "province_id")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER, optional = false
//            ,
//            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE}
    )
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

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

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    protected boolean isSameType(Object o) {
        return o instanceof Province;
    }
}

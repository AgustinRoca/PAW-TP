package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(
        name = "office",
        indexes = {
                @Index(columnList = "office_id", name = "office_office_id_uindex", unique = true),
        }
)
public class Office extends GenericModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_office_id_seq")
    @SequenceGenerator(sequenceName = "office_office_id_seq", name = "office_office_id_seq", allocationSize = 1)
    @Column(name = "office_id")
    private Integer id;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "street", nullable = false)
    private String street;
    @Column(name = "url")
    private String url;
    @JoinColumn(name = "locality_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Locality locality;

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

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Locality getLocality() {
        return this.locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected boolean isSameType(Object o) {
        return o instanceof Office;
    }
}

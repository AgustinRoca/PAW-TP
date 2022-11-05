package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(
        name = "system_country",
        indexes = {
                @Index(columnList = "country_id", name = "system_country_country_id_uindex", unique = true),
        }
)
public class Country extends GenericModel<String> {
    @Id
    @Column(name = "country_id")
    private String id;
    @Column(name = "name", nullable = false)
    private String name;

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
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
        return o instanceof Country;
    }
}

package ar.edu.itba.paw.models;

import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(
        name = "verification_token",
        indexes = {
                @Index(columnList = "verification_token_id", name = "verification_token_id_uindex", unique = true),
        }
)
public class VerificationToken extends GenericModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_token_verification_token_id_seq")
    @SequenceGenerator(sequenceName = "verification_token_verification_token_id_seq", name = "verification_token_verification_token_id_seq", allocationSize = 1)
    @Column(name = "verification_token_id")
    private Integer id;
    @Column(name = "token", columnDefinition = "text")
    private String token;
    @Column(name = "created_date")
    private DateTime createdDate;

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DateTime getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    protected boolean isSameType(Object o) {
        return o instanceof VerificationToken;
    }
}

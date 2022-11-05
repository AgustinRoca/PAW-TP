package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(columnList = "users_id", name = "user_users_id_uindex", unique = true),
                @Index(columnList = "email", name = "user_email_uindex", unique = true),
                @Index(columnList = "verification_token_id", name = "users_verification_token_id_index", unique = true),
                @Index(columnList = "email", name = "user_email_uindex", unique = true),
        }
)
public class User extends GenericModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_users_id_seq")
    @SequenceGenerator(sequenceName = "users_users_id_seq", name = "users_users_id_seq", allocationSize = 1)
    @Column(name = "users_id")
    private Integer id;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "surname", nullable = false)
    private String surname;
    @Column(name = "verified")
    private Boolean verified = false;
    @JoinColumn(name = "verification_token_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private VerificationToken verificationToken;
    @JoinColumn(name = "profile_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Picture profilePicture;
    @Column(name = "profile_id", insertable = false, updatable = false)
    private Integer profilePictureId;
    @Column(name = "phone")
    private String phone;

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Picture getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(Picture profilePicture) {
        this.profilePicture = profilePicture;
        if (profilePicture == null) {
            this.profilePictureId = null;
        } else {
            this.profilePictureId = profilePicture.getId();
        }
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Boolean getVerified() {
        return this.verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public VerificationToken getVerificationToken() {
        return this.verificationToken;
    }

    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getDisplayName() {
        return this.firstName + " " + this.surname;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getProfilePictureId() {
        return this.profilePictureId;
    }

    @Override
    protected boolean isSameType(Object o) {
        return o instanceof User;
    }
}

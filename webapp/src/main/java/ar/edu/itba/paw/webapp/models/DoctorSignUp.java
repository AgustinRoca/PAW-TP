package ar.edu.itba.paw.webapp.models;

public class DoctorSignUp extends UserSignUp {
    private int localityId;
    private Integer registrationNumber;
    private String street;

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getRegistrationNumber() {
        return this.registrationNumber;
    }

    public void setRegistrationNumber(Integer registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getLocalityId() {
        return this.localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }
}

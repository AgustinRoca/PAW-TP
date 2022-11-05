package ar.edu.itba.paw.webapp.auth;

public enum UserRole {
    ANONYMOUS,
    UNVERIFIED,
    PATIENT,
    DOCTOR;

    public static String[] names() {
        String[] roles = new String[UserRole.values().length];
        int i = 0;
        for (UserRole userRole : UserRole.values()) {
            roles[i++] = userRole.name();
        }
        return roles;
    }

    public String getAsRole() {
        return "ROLE_" + this.name();
    }
}

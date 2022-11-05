package ar.edu.itba.paw.webapp.models.error;

import ar.edu.itba.paw.webapp.models.Constants;

public abstract class ErrorConstants {
    public static final APISubError APPOINTMENT_CREATE_MISSING_DATE_FROM
            = missingField(1, "date_from");
    public static final APISubError APPOINTMENT_CREATE_INVALID_DATE_FROM
            = invalidField(2, "date_from");
    public static final APISubError APPOINTMENT_CREATE_INVALID_MOTIVE
            = invalidField(3, "motive");
    public static final APISubError APPOINTMENT_CREATE_INVALID_MESSAGE
            = invalidField(4, "message");
    public static final APISubError APPOINTMENT_CREATE_MISSING_DOCTOR_ID
            = missingField(5, "doctorId");
    public static final APISubError APPOINTMENT_CREATE_INVALID_DOCTOR_ID
            = invalidField(6, "doctorId");
    public static final APISubError APPOINTMENT_CREATE_NONEXISTENT_DOCTOR
            = nonExistentEntity(7, "Doctor", "doctorId");

    public static final APISubError APPOINTMENT_TIME_SLOT_GET_NONEXISTENT_DOCTOR
            = nonExistentEntity(8, "Doctor", "doctor_id");

    public static final APISubError LOCALITY_GET_NONEXISTENT_PROVINCE
            = nonExistentEntity(9, "Province", "provinceId");

    public static final APISubError PROVINCE_GET_NONEXISTENT_COUNTRY
            = nonExistentEntity(10, "Country", "countryId");

    public static final APISubError DOCTOR_PAGINATION_INVALID_NAME
            = invalidField(11, "name");
    public static final APISubError DOCTOR_PAGINATION_MISSING_PAGE
            = missingField(12, "page");
    public static final APISubError DOCTOR_PAGINATION_INVALID_PAGE
            = invalidField(13, "page");
    public static final APISubError DOCTOR_PAGINATION_INVALID_PER_PAGE
            = invalidField(14, "per_page");
    public static final APISubError DOCTOR_PAGINATION_INVALID_SPECIALTIES
            = invalidField(15, "specialties");
   public static final APISubError DOCTOR_PAGINATION_INVALID_LOCALITIES
            = invalidField(16, "localities");

    public static final APISubError DOCTOR_UPDATE_INVALID_PHONE
            = invalidField(17, "phone");
    public static final APISubError DOCTOR_UPDATE_INVALID_EMAIL
            = invalidField(18, "email");
    public static final APISubError DOCTOR_UPDATE_INVALID_SPECIALTIES
            = invalidField(19, "specialtyIds");
    public static final APISubError DOCTOR_UPDATE_SOME_INVALID_SPECIALTIES
            = new APISubError(20, "Some of the specialties provided could not be found");
    public static final APISubError DOCTOR_UPDATE_EMPTY_SPECIALTIES
            = new APISubError(21, "You need to provide at least one specialty");

    public static final APISubError USER_CREATE_MISSING_EMAIL
            = missingField(22, "email");
    public static final APISubError USER_CREATE_INVALID_EMAIL
            = invalidField(23, "email");
    public static final APISubError USER_CREATE_MISSING_FIRST_NAME
            = missingField(24, "firstName");
    public static final APISubError USER_CREATE_INVALID_FIRST_NAME
            = invalidField(25, "firstName");
    public static final APISubError USER_CREATE_MISSING_SURNAME
            = missingField(26, "surname");
    public static final APISubError USER_CREATE_INVALID_SURNAME
            = invalidField(27, "surname");
    public static final APISubError USER_CREATE_MISSING_PASSWORD
            = missingField(28, "password");
    public static final APISubError USER_CREATE_INVALID_PASSWORD
            = invalidField(29, "password");
    public static final APISubError USER_CREATE_INVALID_PHONE
            = invalidField(30, "phone");

    public static final APISubError USER_CREATE_DOCTOR_INVALID_REGISTRATION_NUMBER
            = invalidField(31, "registrationNumber");
    public static final APISubError USER_CREATE_DOCTOR_MISSING_LOCALITY_ID
            = missingField(32, "localityId");
    public static final APISubError USER_CREATE_DOCTOR_INVALID_LOCALITY_ID
            = invalidField(33, "localityId");
    public static final APISubError USER_CREATE_NONEXISTENT_LOCALITY
            = nonExistentEntity(34, "Locality", "localityId");
    public static final APISubError USER_CREATE_DOCTOR_MISSING_STREET
            = missingField(35, "street");
    public static final APISubError USER_CREATE_DOCTOR_INVALID_STREET
            = invalidField(36, "street");

    public static final APISubError USER_UPDATE_INVALID_EMAIL
            = invalidField(37, "email");
    public static final APISubError USER_UPDATE_INVALID_FIRST_NAME
            = invalidField(38, "firstName");
    public static final APISubError USER_UPDATE_INVALID_SURNAME
            = invalidField(39, "surname");
    public static final APISubError USER_UPDATE_INVALID_PHONE
            = invalidField(40, "phone");

    public static final APISubError WORKDAY_CREATE_INVALID_DAY
            = invalidField(41, "day");
    public static final APISubError WORKDAY_CREATE_MISSING_DAY
            = missingField(42, "day");
    public static final APISubError WORKDAY_CREATE_MISSING_START
            = missingField(43, "start");
    public static final APISubError WORKDAY_CREATE_INVALID_START
            = invalidField(44, "start");
    public static final APISubError WORKDAY_CREATE_MISSING_END
            = missingField(45, "end");
    public static final APISubError WORKDAY_CREATE_INVALID_END
            = invalidField(46, "end");
    public static final APISubError WORKDAY_CREATE_MISSING_START_HOUR
            = missingField(47, "start.hour");
    public static final APISubError WORKDAY_CREATE_INVALID_START_HOUR
            = invalidField(48, "start.hour");
    public static final APISubError WORKDAY_CREATE_MISSING_START_MINUTE
            = missingField(49, "start.minute");
    public static final APISubError WORKDAY_CREATE_INVALID_START_MINUTE
            = invalidField(50, "start.minute");
    public static final APISubError WORKDAY_CREATE_MISSING_END_HOUR
            = missingField(51, "end.hour");
    public static final APISubError WORKDAY_CREATE_INVALID_END_HOUR
            = invalidField(52, "end.hour");
    public static final APISubError WORKDAY_CREATE_MISSING_END_MINUTE
            = missingField(53, "end.minute");
    public static final APISubError WORKDAY_CREATE_INVALID_END_MINUTE
            = invalidField(54, "end.minute");

    public static final APISubError DATE_RANGE_TOO_BROAD
            = new APISubError(55, "The selected range date is too broad");
    public static final APISubError DATE_FROM_IS_AFTER_TO
            = new APISubError(56, "The start date is after the end one");

    public static final APISubError MISSING_BODY_PARAMS
            = new APISubError(57, "Some of the required body parameters are missing");
    public static final APISubError MISSING_PATH_PARAMS
            = new APISubError(58, "Some of the required path parameters are missing or are invalid");
    public static final APISubError MISSING_QUERY_PARAMS
            = new APISubError(59, "Some of the required query parameters are missing");
    public static final APISubError INVALID_QUERY_PARAMS
            = new APISubError(60, "Some of the passed query parameters are invalid");

    public static final APISubError USER_EMAIL_USED
            = new APISubError(61, "The email specified is already being used");

    public static final APISubError MISSING_INVALID_REFRESH_TOKEN
            = new APISubError(62, "A valid Refresh Token cookie was expected with the name: \"" + Constants.REFRESH_TOKEN_COOKIE_NAME + "\"");

    public static final APISubError MISSING_INVALID_JSON_BODY
            = new APISubError(63, "Missing or invalid JSON body");

    public static final APISubError INVALID_IMAGE_TYPE
            = new APISubError(64, "Invalid image type");
    public static final APISubError IMAGE_TOO_BIG
            = new APISubError(65, "The supplied image is too big");
    public static final APISubError WORKDAY_CREATE_OVERLAPS
            = new APISubError(66, "The time range overlaps with an existing workday");
    public static final APISubError USER_UPDATE_INVALID_PASSWORD
            = invalidField(67, "password");
    public static final APISubError LOGIN_INVALID_CREDENTIALS
            = invalidField(68, "Invalid username or password");

    public static final APISubError UNAUTHORIZED
            = invalidField(401, "Unauthorized");
    public static final APISubError FORBIDDEN
            = invalidField(403, "Forbidden");
    public static final APISubError NOT_FOUND
            = invalidField(404, "Not found");
    public static final APISubError INTERNAL_SERVER_ERROR
            = new APISubError(500, "Internal server error");

    private static APISubError invalidField(int code, String field) {
        return new APISubError(code, "Invalid \"" + field + "\" field");
    }

    private static APISubError missingField(int code, String field) {
        return new APISubError(code, "Missing \"" + field + "\" field");
    }

    private static APISubError nonExistentEntity(int code, String entityName, String field) {
        return new APISubError(
                code,
                "Provided id for \""
                        + entityName +
                        "\" entity in field \""
                        + field +
                        "\" does not exist"
        );
    }
}

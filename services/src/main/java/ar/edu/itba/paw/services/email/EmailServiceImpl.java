package ar.edu.itba.paw.services.email;

import ar.edu.itba.paw.interfaces.daos.AppointmentDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.User;
import org.apache.commons.text.StringSubstitutor;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.joda.time.DateTimeConstants.*;

@Component
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private MessageSource messageSource;

    /* Cant include AppointmentService because of circular injections. Only needing findAllAppointmentsToNotifyUpTo
     * method, so we can use the DAO without worrying.
     */
    @Autowired
    private AppointmentDao appointmentDao;
    @Autowired
    private String baseUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private Timer timer = new Timer();
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    private static final String MESSAGE_CANCEL_SOURCE_PREFIX = "appointment.cancel.email";
    private static final String MESSAGE_CANCEL_SOURCE_SUBJECT_PREFIX = MESSAGE_CANCEL_SOURCE_PREFIX + ".subject";
    private static final String MESSAGE_CANCEL_SOURCE_BODY_PREFIX = MESSAGE_CANCEL_SOURCE_PREFIX + ".body";

    private static final String MESSAGE_NEW_APPOINTMENT_SOURCE_PREFIX = "appointment.new.email";
    private static final String MESSAGE_NEW_APPOINTMENT_SOURCE_SUBJECT_PREFIX = MESSAGE_NEW_APPOINTMENT_SOURCE_PREFIX + ".subject";
    private static final String MESSAGE_NEW_APPOINTMENT_SOURCE_BODY_PREFIX = MESSAGE_NEW_APPOINTMENT_SOURCE_PREFIX + ".body";

    private static final String MESSAGE_APPOINTMENT_NOTIFICATION_SOURCE_PREFIX = "appointment.notification.email";
    private static final String MESSAGE_APPOINTMENT_NOTIFICATION_SOURCE_DOCTOR_SUBJECT_PREFIX = MESSAGE_APPOINTMENT_NOTIFICATION_SOURCE_PREFIX + ".doctor.subject";
    private static final String MESSAGE_APPOINTMENT_NOTIFICATION_SOURCE_PATIENT_SUBJECT_PREFIX = MESSAGE_APPOINTMENT_NOTIFICATION_SOURCE_PREFIX + ".patient.subject";
    private static final String MESSAGE_APPOINTMENT_NOTIFICATION_DOCTOR_SOURCE_BODY_PREFIX = MESSAGE_APPOINTMENT_NOTIFICATION_SOURCE_PREFIX + ".doctor.body";
    private static final String MESSAGE_APPOINTMENT_NOTIFICATION_PATIENT_SOURCE_BODY_PREFIX = MESSAGE_APPOINTMENT_NOTIFICATION_SOURCE_PREFIX + ".patient.body";

    private static final String MESSAGE_CONFIRMATION_SOURCE_PREFIX = "signup.confirmation.email";
    private static final String MESSAGE_CONFIRMATION_SOURCE_SUBJECT_PREFIX = MESSAGE_CONFIRMATION_SOURCE_PREFIX + ".subject";
    private static final String MESSAGE_CONFIRMATION_SOURCE_BODY_PREFIX = MESSAGE_CONFIRMATION_SOURCE_PREFIX + ".body";

    private static final String MESSAGE_SOURCE_DISCLAIMER = "email.disclaimer";

    private void sendEmail(String to, String subject, String html) throws MessagingException{
        InternetAddress[] parsed;
        try {
            parsed = InternetAddress.parse(to);
        } catch (AddressException e) {
            throw new IllegalArgumentException("Not valid email: " + to, e);
        }

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        mailMessage.setSubject(subject, "UTF-8");

        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, "UTF-8");
        helper.setFrom(EmailSender.getEmailUser());
        helper.setTo(parsed);
        helper.setText(html, true);

        javaMailSender.send(mailMessage);
        LOGGER.info("Email sent to {} with subject: {}", to, subject);
    }

    @Async
    @Override
    public void sendCancelledAppointmentNotificationEmail(Appointment appointment, boolean isDoctorCancelling) {
        String subject = this.messageSource.getMessage(MESSAGE_CANCEL_SOURCE_SUBJECT_PREFIX, null, appointment.getLocale());
        String userTitle = isDoctorCancelling ? this.messageSource.getMessage("doctor", null, appointment.getLocale())
                : this.messageSource.getMessage("patient", null, appointment.getLocale());
        String dowMessage;
        switch (appointment.getFromDate().getDayOfWeek()) {
            case MONDAY:
                dowMessage = "Monday";
                break;
            case TUESDAY:
                dowMessage = "Tuesday";
                break;
            case WEDNESDAY:
                dowMessage = "Wednesday";
                break;
            case THURSDAY:
                dowMessage = "Thursday";
                break;
            case FRIDAY:
                dowMessage = "Friday";
                break;
            case SATURDAY:
                dowMessage = "Saturday";
                break;
            case SUNDAY:
                dowMessage = "Sunday";
                break;
            default:
                dowMessage = null;
        }
        String month;
        switch (appointment.getFromDate().getMonthOfYear()) {
            case JANUARY:
                month = "January";
                break;
            case FEBRUARY:
                month = "February";
                break;
            case MARCH:
                month = "March";
                break;
            case APRIL:
                month = "April";
                break;
            case MAY:
                month = "May";
                break;
            case JUNE:
                month = "June";
                break;
            case JULY:
                month = "July";
                break;
            case AUGUST:
                month = "August";
                break;
            case SEPTEMBER:
                month = "September";
                break;
            case OCTOBER:
                month = "October";
                break;
            case NOVEMBER:
                month = "November";
                break;
            case DECEMBER:
                month = "December";
                break;
            default:
                month = null;
        }

        String html;
        User userCancelling = isDoctorCancelling? appointment.getDoctor().getUser() : appointment.getPatient().getUser();
        User userCancelled = isDoctorCancelling? appointment.getPatient().getUser() : appointment.getPatient().getUser();
        try {
            html = getCancellingHTML(baseUrl,
                    userCancelling,
                    userTitle, appointment, this.messageSource.getMessage(dowMessage, null, appointment.getLocale()),
                    this.messageSource.getMessage(month, null, appointment.getLocale()),
                    baseUrl + (isDoctorCancelling?"/mediclist/1":""),
                    isDoctorCancelling, appointment.getLocale());
        } catch (IOException e) {
            LOGGER.error("Couldn't send cancelling appointment email to user: {}, to notify appointment: {} caused by: {}",
                    userCancelled, appointment, e.getMessage());
            return;
        }

        try {
            sendEmail(isDoctorCancelling? appointment.getPatient().getUser().getEmail() : appointment.getDoctor().getUser().getEmail(),
                    subject, html);
        } catch (MessagingException e) {
            LOGGER.error("Couldn't send cancelled appointment email to: {}, to notify appointment: {}",
                    userCancelled, appointment);
        }
    }

    @Async
    @Override
    public void sendCancelledAppointmentNotificationEmails(List<Appointment> appointments, boolean isDoctorCancelling) {
        for (Appointment appointment : appointments){
            sendCancelledAppointmentNotificationEmail(appointment, isDoctorCancelling);
        }
    }

    @Async
    @Override
    public void sendNewAppointmentNotificationEmail(Appointment appointment) {
        String subject = this.messageSource.getMessage(MESSAGE_NEW_APPOINTMENT_SOURCE_SUBJECT_PREFIX, null, appointment.getLocale());
        String dowMessage;
        switch (appointment.getFromDate().getDayOfWeek()) {
            case MONDAY:
                dowMessage = "Monday";
                break;
            case TUESDAY:
                dowMessage = "Tuesday";
                break;
            case WEDNESDAY:
                dowMessage = "Wednesday";
                break;
            case THURSDAY:
                dowMessage = "Thursday";
                break;
            case FRIDAY:
                dowMessage = "Friday";
                break;
            case SATURDAY:
                dowMessage = "Saturday";
                break;
            case SUNDAY:
                dowMessage = "Sunday";
                break;
            default:
                dowMessage = null;
        }
        String month;
        switch (appointment.getFromDate().getMonthOfYear()) {
            case JANUARY:
                month = "January";
                break;
            case FEBRUARY:
                month = "February";
                break;
            case MARCH:
                month = "March";
                break;
            case APRIL:
                month = "April";
                break;
            case MAY:
                month = "May";
                break;
            case JUNE:
                month = "June";
                break;
            case JULY:
                month = "July";
                break;
            case AUGUST:
                month = "August";
                break;
            case SEPTEMBER:
                month = "September";
                break;
            case OCTOBER:
                month = "October";
                break;
            case NOVEMBER:
                month = "November";
                break;
            case DECEMBER:
                month = "December";
                break;
            default:
                month = null;
        }
        String html;
        try {
            html = getNewAppointmentHTML(
                    baseUrl, appointment.getDoctor().getUser(), appointment.getPatient().getUser(), appointment,
                    this.messageSource.getMessage(dowMessage, null, appointment.getLocale()),
                    this.messageSource.getMessage(month, null, appointment.getLocale()),
                    appointment.getLocale(), appointment.getMotive(), appointment.getMessage());
        } catch (IOException e) {
            LOGGER.error("Couldn't send new appointment email to doctor: {}, to notify appointment: {} caused by: {}",
                    appointment.getDoctor(), appointment, e.getMessage());
            return;
        }

        try {
            sendEmail(appointment.getDoctor().getEmail(), subject, html);
        } catch (MessagingException e) {
            LOGGER.error("Couldn't send new appointment email to: {}, to notify appointment: {}",
                    appointment.getDoctor().getEmail(), appointment);
        }
    }

    @Async
    @Override
    public void sendEmailConfirmationEmail(User user, String token, String confirmationPageRelativeUrl, Locale locale){

        String subject = this.messageSource.getMessage(MESSAGE_CONFIRMATION_SOURCE_SUBJECT_PREFIX, null, locale);
        String confirmationUrl;
        try {
            confirmationUrl = confirmationPageRelativeUrl + "/" + URLEncoder.encode(token, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error converting token to UTF-8");
        }
        String html;
        try {
            html = getConfirmationHTML(baseUrl, confirmationUrl, user, locale);
        } catch (IOException e) {
            LOGGER.error("Couldn't send verification token email to user: {}, the generated token was: {}, caused by: {}",
                    user, token, e.getMessage());
            return;
        }
        try {
            sendEmail(user.getEmail(), subject, html);
        } catch (MessagingException e) {
            LOGGER.error("Couldn't send email confirmation mail to: {}", user.getEmail());
        }
    }

    @Async
    @Override
    public void scheduleNotifyAppointmentEmail(Appointment appointment) {
        String doctorSubject = this.messageSource.getMessage(MESSAGE_APPOINTMENT_NOTIFICATION_SOURCE_DOCTOR_SUBJECT_PREFIX, null, appointment.getLocale());
        String patientSubject = this.messageSource.getMessage(MESSAGE_APPOINTMENT_NOTIFICATION_SOURCE_PATIENT_SUBJECT_PREFIX, null, appointment.getLocale());
        String dowMessage;
        switch (appointment.getFromDate().getDayOfWeek()) {
            case MONDAY:
                dowMessage = "Monday";
                break;
            case TUESDAY:
                dowMessage = "Tuesday";
                break;
            case WEDNESDAY:
                dowMessage = "Wednesday";
                break;
            case THURSDAY:
                dowMessage = "Thursday";
                break;
            case FRIDAY:
                dowMessage = "Friday";
                break;
            case SATURDAY:
                dowMessage = "Saturday";
                break;
            case SUNDAY:
                dowMessage = "Sunday";
                break;
            default:
                dowMessage = null;
        }
        String month;
        switch (appointment.getFromDate().getMonthOfYear()) {
            case JANUARY:
                month = "January";
                break;
            case FEBRUARY:
                month = "February";
                break;
            case MARCH:
                month = "March";
                break;
            case APRIL:
                month = "April";
                break;
            case MAY:
                month = "May";
                break;
            case JUNE:
                month = "June";
                break;
            case JULY:
                month = "July";
                break;
            case AUGUST:
                month = "August";
                break;
            case SEPTEMBER:
                month = "September";
                break;
            case OCTOBER:
                month = "October";
                break;
            case NOVEMBER:
                month = "November";
                break;
            case DECEMBER:
                month = "December";
                break;
            default:
                month = null;
        }
        String doctorHtml;
        try {
            doctorHtml = getNotifyingDoctorHtml(
                    baseUrl, appointment.getDoctor().getUser(), appointment.getPatient().getUser(), appointment,
                    this.messageSource.getMessage(dowMessage, null, appointment.getLocale()),
                    this.messageSource.getMessage(month, null, appointment.getLocale()),
                    appointment.getLocale(), appointment.getMotive(), appointment.getMessage());
        } catch (IOException e) {
            LOGGER.error("Couldn't send notifying appointment email to doctor: {}, to notify appointment: {} caused by: {}",
                    appointment.getDoctor(), appointment, e.getMessage());
            return;
        }
        String patientHtml;
        try {
            patientHtml = getNotifyingPatientHtml(
                    baseUrl, appointment.getDoctor().getUser(), appointment.getPatient().getUser(), appointment,
                    this.messageSource.getMessage(dowMessage, null, appointment.getLocale()),
                    this.messageSource.getMessage(month, null, appointment.getLocale()),
                    appointment.getLocale(), appointment.getMotive(), appointment.getMessage());
        } catch (IOException e) {
            LOGGER.error("Couldn't send notifying appointment email to patient: {}, to notify appointment: {} caused by: {}",
                    appointment.getPatient(), appointment, e.getMessage());
            return;
        }

        String finalDoctorHtml = doctorHtml;
        String finalPatientHtml = patientHtml;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    sendEmail(appointment.getDoctor().getEmail(), doctorSubject, finalDoctorHtml);
                    LOGGER.info("Notification email sent to: {} for appointment {}", appointment.getDoctor(), appointment);
                } catch (MessagingException e) {
                    LOGGER.error("Couldn't send notifying appointment email to doctor: {}, to notify appointment: {}",
                            appointment.getDoctor().getEmail(), appointment);
                }
                try {
                    sendEmail(appointment.getPatient().getUser().getEmail(), patientSubject, finalPatientHtml);
                    LOGGER.info("Notification email sent to: {} for appointment {}", appointment.getPatient(), appointment);
                } catch (MessagingException e) {
                    LOGGER.error("Couldn't send notifying appointment email to patient: {}, to notify appointment: {}",
                            appointment.getDoctor().getEmail(), appointment);
                }
                appointment.setWasNotificationEmailSent(true);
                appointmentDao.update(appointment);
            }
        }, appointment.getFromDate().minusDays(1).toDate());
        LOGGER.info("Notification email scheduled to: {} at {}", appointment.getPatient(), appointment.getFromDate().minusDays(1).toDate());
    }

    @PostConstruct
    @Async
    @Override
    public void initScheduleEmails() {
        scheduler.scheduleAtFixedRate(this::scheduleNotifyAppointmentEmails, 0, 1, TimeUnit.DAYS);
    }

    private void scheduleNotifyAppointmentEmails(){
        // Clean up email sender timer
        timer.cancel();
        timer.purge();
        timer = new Timer();
        LocalDateTime now = LocalDateTime.now();
        // Notify 24hs earlier than the appointment, so we need appointments from now to 2 days from now
        List<Appointment> appointments = appointmentDao.findAllAppointmentsToNotifyUpTo(now.plusDays(2));
        LOGGER.info("Found {} emails to notify in the next 24hs", appointments.size());
        for (Appointment appointment: appointments){
            scheduleNotifyAppointmentEmail(appointment);
        }
        LOGGER.info("Scheduled {} notification emails", appointments.size());
    }

    private String getCancellingHTML(String baseUrl, User userCancelling, String userTitle, Appointment appointment, String dow, String month, String link, boolean isCancellingDoctor, Locale locale) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("baseUrl", baseUrl);
        values.put("year", String.valueOf(DateTime.now().getYear()));
        values.put("body", this.messageSource.getMessage(MESSAGE_CANCEL_SOURCE_BODY_PREFIX + ".body",
                new Object[]{
                        userTitle,
                        userCancelling.getDisplayName(),
                        dow,
                        appointment.getFromDate().getDayOfMonth(),
                        month,
                        Integer.toString(appointment.getFromDate().getYear()),
                        ((appointment.getFromDate().getHourOfDay() < 10) ? "0" : "") + appointment.getFromDate().getHourOfDay(),
                        ((appointment.getFromDate().getMinuteOfHour() < 10) ? "0" : "") + appointment.getFromDate().getMinuteOfHour(),
                        ((appointment.getToDate().getHourOfDay() < 10) ? "0" : "") + appointment.getToDate().getHourOfDay(),
                        ((appointment.getToDate().getMinuteOfHour() < 10) ? "0" : "") + appointment.getToDate().getMinuteOfHour()
                },
                locale));
        values.put("disclaimer", this.messageSource.getMessage(MESSAGE_SOURCE_DISCLAIMER, null, locale));
        values.put("title", this.messageSource.getMessage(MESSAGE_CANCEL_SOURCE_BODY_PREFIX + ".title", null, locale));
        values.put("link", link);
        if(isCancellingDoctor) {
            values.put("buttonText", this.messageSource.getMessage(MESSAGE_CANCEL_SOURCE_BODY_PREFIX + ".buttonText.toPatient", null, locale));
        } else {
            values.put("buttonText", this.messageSource.getMessage(MESSAGE_CANCEL_SOURCE_BODY_PREFIX + ".buttonText.toDoctor", null, locale));
        }
        EmailFormatter emailFormatter = new EmailFormatter();
        String html;
        try {
            html = emailFormatter.format(emailFormatter.getHTMLFromFilename("email/cancel.html"));
        } catch (IOException e){
            throw new IOException("Can't find HTML file cancel.html");
        }
        StringSubstitutor substitutor = new StringSubstitutor(values, "${", "}");
        return substitutor.replace(html);
    }

    private String getNewAppointmentHTML(String baseUrl, User doctor, User patient, Appointment appointment, String dow, String month, Locale locale, String motive, String comment) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("baseUrl", baseUrl);
        values.put("year", String.valueOf(DateTime.now().getYear()));
        values.put("body", this.messageSource.getMessage(MESSAGE_NEW_APPOINTMENT_SOURCE_BODY_PREFIX + ".body",
                new Object[]{
                        doctor.getFirstName(),
                        patient.getDisplayName(),
                        dow,
                        appointment.getFromDate().getDayOfMonth(),
                        month,
                        Integer.toString(appointment.getFromDate().getYear()),
                        ((appointment.getFromDate().getHourOfDay() < 10) ? "0" : "") + appointment.getFromDate().getHourOfDay(),
                        ((appointment.getFromDate().getMinuteOfHour() < 10) ? "0" : "") + appointment.getFromDate().getMinuteOfHour(),
                        ((appointment.getToDate().getHourOfDay() < 10) ? "0" : "") + appointment.getToDate().getHourOfDay(),
                        ((appointment.getToDate().getMinuteOfHour() < 10) ? "0" : "") + appointment.getToDate().getMinuteOfHour(),
                        motive
                },
                locale));
        values.put("comment", this.messageSource.getMessage(MESSAGE_NEW_APPOINTMENT_SOURCE_BODY_PREFIX + ".comment", new Object[]{comment}, locale));
        values.put("link", baseUrl);
        values.put("buttonText", this.messageSource.getMessage(MESSAGE_NEW_APPOINTMENT_SOURCE_BODY_PREFIX + ".buttonText", new Object[]{comment}, locale));
        values.put("disclaimer", this.messageSource.getMessage(MESSAGE_SOURCE_DISCLAIMER, null, locale));
        values.put("title", this.messageSource.getMessage(MESSAGE_NEW_APPOINTMENT_SOURCE_BODY_PREFIX + ".title", null, locale));

        EmailFormatter emailFormatter = new EmailFormatter();
        String html;
        if (comment != null && !comment.isEmpty()) {
            try {
                html = emailFormatter.format(emailFormatter.getHTMLFromFilename("email/newAppointmentWithComment.html"));
            } catch (IOException e){
                throw new IOException("Can't find HTML file newAppointmentWithComment.html");
            }
        } else {
            try {
                html = emailFormatter.format(emailFormatter.getHTMLFromFilename("email/newAppointment.html"));
            } catch (IOException e){
                throw new IOException("Can't find HTML file newAppointment.html");
            }
        }
        StringSubstitutor substitutor = new StringSubstitutor(values, "${", "}");
        return substitutor.replace(html);
    }

    private String getNotifyingDoctorHtml(String baseUrl, User doctor, User patient, Appointment appointment, String dow, String month, Locale locale, String motive, String comment) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("baseUrl", baseUrl);
        values.put("year", String.valueOf(DateTime.now().getYear()));
        values.put("body", this.messageSource.getMessage(MESSAGE_APPOINTMENT_NOTIFICATION_DOCTOR_SOURCE_BODY_PREFIX + ".body",
                new Object[]{
                        doctor.getFirstName(),
                        patient.getDisplayName(),
                        dow,
                        appointment.getFromDate().getDayOfMonth(),
                        month,
                        Integer.toString(appointment.getFromDate().getYear()),
                        ((appointment.getFromDate().getHourOfDay() < 10) ? "0" : "") + appointment.getFromDate().getHourOfDay(),
                        ((appointment.getFromDate().getMinuteOfHour() < 10) ? "0" : "") + appointment.getFromDate().getMinuteOfHour(),
                        ((appointment.getToDate().getHourOfDay() < 10) ? "0" : "") + appointment.getToDate().getHourOfDay(),
                        ((appointment.getToDate().getMinuteOfHour() < 10) ? "0" : "") + appointment.getToDate().getMinuteOfHour(),
                        motive
                },
                locale));
        values.put("comment", this.messageSource.getMessage(MESSAGE_APPOINTMENT_NOTIFICATION_DOCTOR_SOURCE_BODY_PREFIX + ".comment", new Object[]{comment}, locale));
        values.put("link", baseUrl);
        values.put("buttonText", this.messageSource.getMessage(MESSAGE_APPOINTMENT_NOTIFICATION_DOCTOR_SOURCE_BODY_PREFIX + ".buttonText", new Object[]{comment}, locale));
        values.put("disclaimer", this.messageSource.getMessage(MESSAGE_SOURCE_DISCLAIMER, null, locale));
        values.put("title", this.messageSource.getMessage(MESSAGE_APPOINTMENT_NOTIFICATION_DOCTOR_SOURCE_BODY_PREFIX + ".title", null, locale));

        EmailFormatter emailFormatter = new EmailFormatter();
        String html;
        if (comment != null && !comment.isEmpty()) {
            try {
                html = emailFormatter.format(emailFormatter.getHTMLFromFilename("email/newAppointmentWithComment.html"));
            } catch (IOException e){
                throw new IOException("Can't find HTML file newAppointmentWithComment.html");
            }
        } else {
            try {
                html = emailFormatter.format(emailFormatter.getHTMLFromFilename("email/newAppointment.html"));
            } catch (IOException e){
                throw new IOException("Can't find HTML file newAppointment.html");
            }
        }
        StringSubstitutor substitutor = new StringSubstitutor(values, "${", "}");
        return substitutor.replace(html);
    }

    private String getNotifyingPatientHtml(String baseUrl, User doctor, User patient, Appointment appointment, String dow, String month, Locale locale, String motive, String comment) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("baseUrl", baseUrl);
        values.put("year", String.valueOf(DateTime.now().getYear()));
        values.put("body", this.messageSource.getMessage(MESSAGE_APPOINTMENT_NOTIFICATION_PATIENT_SOURCE_BODY_PREFIX + ".body",
                new Object[]{
                        patient.getDisplayName(),
                        doctor.getFirstName(),
                        dow,
                        appointment.getFromDate().getDayOfMonth(),
                        month,
                        Integer.toString(appointment.getFromDate().getYear()),
                        ((appointment.getFromDate().getHourOfDay() < 10) ? "0" : "") + appointment.getFromDate().getHourOfDay(),
                        ((appointment.getFromDate().getMinuteOfHour() < 10) ? "0" : "") + appointment.getFromDate().getMinuteOfHour(),
                        ((appointment.getToDate().getHourOfDay() < 10) ? "0" : "") + appointment.getToDate().getHourOfDay(),
                        ((appointment.getToDate().getMinuteOfHour() < 10) ? "0" : "") + appointment.getToDate().getMinuteOfHour(),
                        motive
                },
                locale));
        values.put("comment", this.messageSource.getMessage(MESSAGE_APPOINTMENT_NOTIFICATION_PATIENT_SOURCE_BODY_PREFIX + ".comment", new Object[]{comment}, locale));
        values.put("link", baseUrl);
        values.put("buttonText", this.messageSource.getMessage(MESSAGE_APPOINTMENT_NOTIFICATION_PATIENT_SOURCE_BODY_PREFIX + ".buttonText", new Object[]{comment}, locale));
        values.put("disclaimer", this.messageSource.getMessage(MESSAGE_SOURCE_DISCLAIMER, null, locale));
        values.put("title", this.messageSource.getMessage(MESSAGE_APPOINTMENT_NOTIFICATION_PATIENT_SOURCE_BODY_PREFIX + ".title", null, locale));

        EmailFormatter emailFormatter = new EmailFormatter();
        String html;
        if (comment != null && !comment.isEmpty()) {
            try {
                html = emailFormatter.format(emailFormatter.getHTMLFromFilename("email/newAppointmentWithComment.html"));
            } catch (IOException e){
                throw new IOException("Can't find HTML file newAppointmentWithComment.html");
            }
        } else {
            try {
                html = emailFormatter.format(emailFormatter.getHTMLFromFilename("email/newAppointment.html"));
            } catch (IOException e){
                throw new IOException("Can't find HTML file newAppointment.html");
            }
        }
        StringSubstitutor substitutor = new StringSubstitutor(values, "${", "}");
        return substitutor.replace(html);
    }

    private String getConfirmationHTML(String baseUrl, String url, User user, Locale locale) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("url", url);
        values.put("baseUrl", baseUrl);
        values.put("year", String.valueOf(DateTime.now().getYear()));
        values.put("button", this.messageSource.getMessage(MESSAGE_CONFIRMATION_SOURCE_BODY_PREFIX + ".button", null, locale));
        values.put("button_url", this.messageSource.getMessage(MESSAGE_CONFIRMATION_SOURCE_BODY_PREFIX + ".button_url", null, locale));
        values.put("body", this.messageSource.getMessage(MESSAGE_CONFIRMATION_SOURCE_BODY_PREFIX + ".body", null, locale));
        values.put("disclaimer", this.messageSource.getMessage(MESSAGE_SOURCE_DISCLAIMER, null, locale));
        values.put("title", this.messageSource.getMessage(MESSAGE_CONFIRMATION_SOURCE_BODY_PREFIX + ".title", new Object[]{user.getFirstName()}, locale));
        values.put("subtitle", this.messageSource.getMessage(MESSAGE_CONFIRMATION_SOURCE_BODY_PREFIX + ".subtitle", new Object[]{user.getFirstName()}, locale));

        EmailFormatter emailFormatter = new EmailFormatter();
        String html;
        try {
            html = emailFormatter.format(emailFormatter.getHTMLFromFilename("email/signup.html"));
        } catch (IOException e){
            throw new IOException("Can't find HTML file signup.html");
        }
        StringSubstitutor substitutor = new StringSubstitutor(values, "${", "}");
        return substitutor.replace(html);
    }

}

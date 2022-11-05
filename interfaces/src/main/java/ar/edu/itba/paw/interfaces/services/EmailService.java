package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.User;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.List;
import java.util.Locale;

public interface EmailService {

    void sendCancelledAppointmentNotificationEmail(Appointment appointment, boolean isDoctorCancelling);

    void sendCancelledAppointmentNotificationEmails(List<Appointment> appointments, boolean isDoctorCancelling);

    void sendNewAppointmentNotificationEmail(Appointment appointment);

    void sendEmailConfirmationEmail(User user, String token, String confirmationPageRelativeUrl, Locale locale);

    void scheduleNotifyAppointmentEmail(Appointment appointment);

    @PostConstruct
    void initScheduleEmails();

}

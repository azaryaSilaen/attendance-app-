package com.example.application.views.sendemail;

import com.example.application.data.entity.Students;
import com.example.application.data.service.SpringEmailService;
import com.example.application.data.service.StudentsService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.security.RolesAllowed;
import jakarta.mail.MessagingException;
import org.springframework.aop.AopInvocationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Sending Emails")
@Route(value = "sending-emails", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
@Uses(Icon.class)
public class SendEmailView extends Composite<VerticalLayout> {

    StudentsService service;
    SpringEmailService emailService;
    Notification notificationSuccess = new Notification();
    Notification notificationFailed = new Notification();
    TextField minimumAttendance = new TextField();
    int minAttendance = 80;

    Button sendEmailButton = new Button();
    //Button buttonSecondary = new Button();

    public SendEmailView(StudentsService service, SpringEmailService emailService) {
        this.service = service;
        this.emailService = emailService;

        notificationSuccess.setDuration(5000);
        notificationSuccess.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notificationFailed.setDuration(5000);
        notificationFailed.addThemeVariants(NotificationVariant.LUMO_ERROR);

        HorizontalLayout layoutRow = new HorizontalLayout();
        //VerticalLayout layoutColumn5 = new VerticalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        //VerticalLayout layoutColumn4 = new VerticalLayout();
        HorizontalLayout layoutRow3 = new HorizontalLayout();
        HorizontalLayout layoutRow4 = new HorizontalLayout();
        HorizontalLayout layoutRow5 = new HorizontalLayout();
        //VerticalLayout layoutColumn6 = new VerticalLayout();
        getContent().setWidthFull();
        getContent().addClassName(Padding.LARGE);
        layoutRow.setWidthFull();
        //layoutRow.setFlexGrow(1.0, layoutColumn5);
        //layoutColumn5.setWidth(null);
        layoutRow.setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth(null);

        layoutRow2.setWidthFull();
        layoutRow2.addClassName(Gap.LARGE);
        layoutRow2.setFlexGrow(1.0, layoutColumn3);
        layoutColumn3.setWidth(null);

        minimumAttendance.setLabel("minimum attendance (%)");
        minimumAttendance.setWidthFull();
        minimumAttendance.setAllowedCharPattern("[0-9]");
        minimumAttendance.setMinLength(1);
        minimumAttendance.setMaxLength(3);
        minimumAttendance.setRequiredIndicatorVisible(true);
        minimumAttendance.setMaxWidth("200px");
        minimumAttendance.setHelperText("integers only (ex. 80)");

        //layoutRow2.setFlexGrow(1.0, layoutColumn4);
        //layoutColumn4.setWidth(null);

        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidthFull();

        //layoutRow3.setFlexGrow(1.0, timeJoined);
        layoutRow4.addClassName(Gap.MEDIUM);

        sendEmailButton.setText("send email to inactive students");
        sendEmailButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //buttonSecondary.setText("Clear");

        final Paragraph[] outPut = new Paragraph[5];
        outPut[0] = new Paragraph("");

        //layoutRow.setFlexGrow(1.0, layoutColumn6);
        //layoutColumn6.setWidth(null);
        getContent().add(layoutRow);
        //layoutRow.add(layoutColumn5);
        layoutRow.add(layoutColumn2);
        //layoutColumn2.add(h3);
        layoutColumn2.add(layoutRow2);
        layoutRow2.add(layoutColumn3);
        layoutColumn3.add(minimumAttendance);
        minimumAttendance.setValue(String.valueOf(minAttendance));
        //layoutColumn3.add(schoolID);
        //layoutRow2.add(layoutColumn4);
        //layoutColumn4.add(grade);
        //layoutColumn4.add(layoutRow3);
        //layoutRow3.add(timeJoined);
        layoutColumn2.add(layoutRow4);
        layoutColumn2.add(layoutRow5);
        layoutRow4.add(sendEmailButton);
        sendingEmailButton();
        //layoutRow4.add(buttonSecondary);
        layoutRow5.add(outPut[0]);
        //layoutRow.add(layoutColumn6);


    }

    public void sendingEmailButton () {
        // Add a click listener to the button to send email to inactive students
        sendEmailButton.addClickListener(buttonClickEvent -> {
            if (!minimumAttendance.isEmpty()) {
                minAttendance = Integer.parseInt(minimumAttendance.getValue());
                int successCount = 0;
                int failedCount = 0;

                // Create an ArrayList to store inactive students
                ArrayList<Students> inactiveStudents = new ArrayList<Students>();
                // Iterate through the list of students to find inactive students and add them to the ArrayList
                for (int i = 0; i < getStudents().size(); i++) {
                    double attendanceRate = ((double) getStudents().get(i).getAttendance() /
                            (double) service.selectSpecificNumMeeting(getStudents().get(i).getClub())) * 100;

                    if (attendanceRate < minAttendance) {
                        inactiveStudents.add(getStudents().get(i));
                    }
                }

                // Iterate through the inactive students
                for (int i = 0; i < inactiveStudents.size(); i++) {
                    // Calculate attendance rate for the inactive student
                    double attendanceRate = ((double) inactiveStudents.get(i).getAttendance() /
                            (double) service.selectSpecificNumMeeting(inactiveStudents.get(i).getClub())) * 100;

                    // Construct email details
                    String studentEmail = getStudents().get(i).getStudentId() + "gmail.com";
                    String clubName = inactiveStudents.get(i).getClub() + " club";
                    String studentName = inactiveStudents.get(i).getName();
                    String idNumber = String.valueOf(inactiveStudents.get(i).getStudentId());
                    String subject = "Inactive in " + clubName;
                    String text = "Good morning " + studentName + " (ID: " + idNumber + ")"
                            + "\n\n" + "Your attendance rate for " + clubName + " is only " + (int) attendanceRate + "%"
                            + "\n" + "You must have minimum of " + minAttendance + "% attendance rate to be able to have a CAS experience for " + clubName
                            + "\n" + "Please attend more meetings and be active!"
                            + "\n\n" + "If you're no longer part of " + clubName + ", please contact your club's supervisor"
                            + "\n\n" + "Dear,"
                            + "\n" + "Admin";

                    // Send email to the inactive student
                    try {
                        emailService.sendEmail("68386@jisedu.or.id", subject, text);
                        successCount++;
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        failedCount++;
                    }

                }
                // Check if there were no failed email sending attempts
                if (failedCount == 0) {
                    // Display a success notification indicating the number of emails sent
                    notificationSuccess.setText(successCount + " emails sent");
                    notificationSuccess.open();
                }
                // If there were failed attempts and at least one successful attempt
                else if (successCount > 0) {
                    // Display both success and failure notifications
                    // Detailing the number of successful and failed emails sent
                    notificationSuccess.setText(successCount + " emails sent");
                    notificationSuccess.open();
                    notificationFailed.setText(failedCount + " emails FAILED to sent");
                    notificationFailed.open();
                }
                // If there were no successful attempts
                else {
                    // Display only the failure notification with the number of failed emails sent
                    notificationFailed.setText(failedCount + " emails FAILED to sent");
                    notificationFailed.open();
                }

                // If the minimum attendance field is not filled out
            }
                else {
                    // Display a notification prompting the user to fill out the field
                    notificationFailed.setText("Please fill out the minimum attendance field");
                    notificationFailed.open();
                }

        });
    }

    /**
     * Checks if the provided user credentials are valid based on certain criteria.
     *
     * @param username The username to validate.
     * @param grade The grade of the user.
     * @param schoolID The school ID of the user.
     * @param timeJoined The date the user joined.
     * @return True if the credentials are valid, false otherwise.
     */
    public boolean isValid(String username, int grade, int schoolID, LocalDate timeJoined) {
        // Check if the username contains only letters
        String nonSpaceUsername = username.replaceAll(" ","");
        char[] userChars = nonSpaceUsername.toCharArray();
        for (char c : userChars) {
            if (Character.isDigit(c)){
                return false;
            }
        }

        // Check if the grade is within the valid range (9-12)
        if (grade < 9 || grade > 12) {
            return false;
        }

        // Check if the school ID is within the valid range (10000-99999)
        if (!(schoolID >= 10000 && schoolID <= 99999)) {
            return false;
        }

        // Check if the join date is not in the future
        if (timeJoined.isAfter(LocalDate.now())) {
            return false;
        }

        // All criteria are met, so the credentials are considered valid
        return true;
    }

    public boolean isValid(String username, int grade, int schoolID) {
        // Check if the username contains only letters
        String nonSpaceUsername = username.replaceAll(" ","");
        char[] userChars = nonSpaceUsername.toCharArray();
        for (char c : userChars) {
            if (Character.isDigit(c)){
                return false;
            }
        }

        // Check if the grade is within the valid range (9-12)
        if (grade < 9 || grade > 12) {
            return false;
        }

        // Check if the school ID is within the valid range (10000-99999)
        if (!(schoolID >= 10000 && schoolID <= 99999)) {
            return false;
        }

        // All criteria are met, so the credentials are considered valid
        return true;
    }

    private List<Students> getStudents() {
        List<Students> empty = new ArrayList<>();
        try {
            return service.findAll();
        } catch (AopInvocationException e) {
            return empty;
        }

    }
}

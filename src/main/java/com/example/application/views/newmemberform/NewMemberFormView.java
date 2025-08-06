package com.example.application.views.newmemberform;

import com.example.application.data.entity.Students;
import com.example.application.data.entity.User;
import com.example.application.data.service.StudentsRepository;
import com.example.application.data.service.StudentsService;
import com.example.application.data.service.UserRepository;
import com.example.application.security.AuthenticatedUser;
import com.example.application.security.UserDetailsServiceImpl;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Optional;

@PageTitle("New Member Form")
@Route(value = "new-member-form", layout = MainLayout.class)
@RolesAllowed({"SUPERVISOR", "OFFICER"})
@Uses(Icon.class)
public class NewMemberFormView extends Composite<VerticalLayout> {

    StudentsService service;


    public NewMemberFormView(StudentsService service) {
        this.service = service;

        Notification notification = new Notification();
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.TOP_CENTER);

        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn5 = new VerticalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        VerticalLayout layoutColumn4 = new VerticalLayout();
        HorizontalLayout layoutRow3 = new HorizontalLayout();
        HorizontalLayout layoutRow4 = new HorizontalLayout();
        HorizontalLayout layoutRow5 = new HorizontalLayout();
        Button buttonPrimary = new Button();
        Button buttonSecondary = new Button();
        VerticalLayout layoutColumn6 = new VerticalLayout();
        getContent().setWidthFull();
        getContent().addClassName(Padding.LARGE);
        layoutRow.setWidthFull();
        layoutRow.setFlexGrow(1.0, layoutColumn5);
        layoutColumn5.setWidth(null);
        layoutRow.setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth(null);

        TextField name = new TextField();
        TextField schoolID = new TextField();
        ComboBox grade = new ComboBox();
        grade.setItems(9, 10, 11, 12);
        DatePicker timeJoined = new DatePicker();
        h3.setText("Personal Information");

        layoutRow2.setWidthFull();
        layoutRow2.addClassName(Gap.LARGE);
        layoutRow2.setFlexGrow(1.0, layoutColumn3);
        layoutColumn3.setWidth(null);

        name.setLabel("powerschool name");
        name.setWidthFull();
        name.setRequiredIndicatorVisible(true);
        name.setHelperText("Format: Firstname Lastname (ex. Will Smith)");

        schoolID.setLabel("school ID");
        schoolID.setWidthFull();
        schoolID.setRequiredIndicatorVisible(true);
        schoolID.setAllowedCharPattern("[0-9]");
        schoolID.setMinLength(5);
        schoolID.setMaxLength(5);
        schoolID.setHelperText("Format: 5 digits integer (ex. 12345)");

        layoutRow2.setFlexGrow(1.0, layoutColumn4);
        layoutColumn4.setWidth(null);

        grade.setLabel("grade");
        grade.setWidthFull();
        grade.setRequiredIndicatorVisible(true);

        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidthFull();

        timeJoined.setLabel("time joined");
        timeJoined.setValue(LocalDate.now());
        timeJoined.setRequiredIndicatorVisible(true);
        timeJoined.setMax(LocalDate.now());

        layoutRow3.setFlexGrow(1.0, timeJoined);
        layoutRow4.addClassName(Gap.MEDIUM);

        buttonPrimary.setText("Submit");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonSecondary.setText("Clear");

        final Paragraph[] outPut = new Paragraph[5];
        outPut[0] = new Paragraph("");

        layoutRow.setFlexGrow(1.0, layoutColumn6);
        layoutColumn6.setWidth(null);
        getContent().add(layoutRow);
        layoutRow.add(layoutColumn5);
        layoutRow.add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(layoutRow2);
        layoutRow2.add(layoutColumn3);
        layoutColumn3.add(name);
        layoutColumn3.add(schoolID);
        layoutRow2.add(layoutColumn4);
        layoutColumn4.add(grade);
        layoutColumn4.add(layoutRow3);
        layoutRow3.add(timeJoined);
        layoutColumn2.add(layoutRow4);
        layoutColumn2.add(layoutRow5);
        layoutRow4.add(buttonPrimary);
        layoutRow4.add(buttonSecondary);
        layoutRow5.add(outPut[0]);
        layoutRow.add(layoutColumn6);

        //retrieving username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        //all supervisor and officer's username are clubName(OFFICER) or clubName(SUPERVISOR)
        //There for this is the best way to retrieve clubName from username
        String club = username.substring(0, username.indexOf("("));

        //submit button clicked
        buttonPrimary.addClickListener(buttonClickEvent -> {
            //clear text
            layoutRow5.remove(outPut[0]);

            //schoolID and grade are textfield but we want integer
                int intID = Integer.parseInt(schoolID.getValue());
                int intGrade = (int)grade.getValue();

                //checking if format is invalid
                if (!isValid(name.getValue(), intGrade, intID, timeJoined.getValue())) {
                    outPut[0] = new Paragraph("WARNING: please fill out with the correct format");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                //checking if same id number is present in the same club
                else if (service.searchByStudentIdAndClub(intID, club) != null) {
                    outPut[0] = new Paragraph("WARNING: another student has the same student ID, please fix it");
                    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                }
                //successfully applying to be member
                else {
                    outPut[0] = new Paragraph("Welcome " + name.getValue() + "!" + " You're now member of "
                            + club + " club");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    //save new member to student database
                    service.save(new Students(intID,name.getValue(),intGrade,club,0,
                            "TBF",String.valueOf(timeJoined.getValue())));

                    //reset field
                    name.setValue("");
                    grade.setValue("");
                    schoolID.setValue("");
                    timeJoined.setValue(LocalDate.now());
                }
                //add paragraph to show result
            layoutRow5.add(outPut[0]);
            notification.setText(outPut[0].getText());
            notification.open();
        });

        //click clear button
        buttonSecondary.addClickListener(buttonClickEvent -> {
            layoutRow5.remove(outPut[0]);
            name.setValue("");
            grade.setValue("");
            schoolID.setValue("");
            timeJoined.setValue(LocalDate.now());
        });

        /*
            if (name.isEmpty() | grade.isEmpty() | schoolID.isEmpty() | timeJoined.isEmpty()) {
                outPut[0] = new Paragraph("WARNING: please fill out all the fields");
            }

                 */


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
}

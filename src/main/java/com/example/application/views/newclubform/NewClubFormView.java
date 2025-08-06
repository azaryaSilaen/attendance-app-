package com.example.application.views.newclubform;

import com.example.application.data.entity.Clubs;
import com.example.application.data.entity.User;
import com.example.application.data.service.ClubsService;
import com.example.application.data.service.UserService;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@PageTitle("New Club Form")
@Route(value = "new-club-form", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
@Uses(Icon.class)
public class NewClubFormView extends Composite<VerticalLayout> {

    UserService service;
    ClubsService clubsService;


    public NewClubFormView(UserService service, ClubsService clubsService) {
        this.service = service;
        this.clubsService = clubsService;

        Notification notification = new Notification();
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.TOP_CENTER);

        H3 h3 = new H3();

        //creating layout
        VerticalLayout layoutColumn2 = new VerticalLayout();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        VerticalLayout layoutColumn4 = new VerticalLayout();
        VerticalLayout layoutColumn5 = new VerticalLayout();
        VerticalLayout layoutColumn6 = new VerticalLayout();

        HorizontalLayout layoutRowStart = new HorizontalLayout();
        HorizontalLayout layoutRow = new HorizontalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        HorizontalLayout layoutRow3 = new HorizontalLayout();
        HorizontalLayout layoutRow4 = new HorizontalLayout();
        HorizontalLayout layoutRow5 = new HorizontalLayout();

        //creating button
        Button buttonPrimary = new Button();
        Button buttonSecondary = new Button();

        getContent().setWidthFull();
        getContent().addClassName(Padding.LARGE);
        layoutRow.setWidthFull();
        layoutRow.setFlexGrow(1.0, layoutColumn5);
        layoutColumn5.setWidth(null);
        layoutRow.setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth(null);

        TextField clubName = new TextField();
        TextField supervisorUsername = new TextField();
        TextField supervisorPassword = new TextField();
        TextField officerUsername = new TextField();
        TextField officerPassword = new TextField();
        h3.setText("add new club (or update password for existing one)");

        layoutRow2.setWidthFull();
        layoutRow2.addClassName(Gap.LARGE);
        layoutRow2.setFlexGrow(1.0, layoutColumn3);
        layoutColumn3.setWidth(null);

        clubName.setLabel("club name");
        //clubName.setWidthFull();
        clubName.setRequiredIndicatorVisible(true);

        supervisorUsername.setLabel("username (SUPERVISOR)");
        supervisorUsername.setWidthFull();
        supervisorUsername.setReadOnly(true);

        supervisorPassword.setLabel("password (SUPERVISOR)");
        supervisorPassword.setWidthFull();
        supervisorPassword.setRequiredIndicatorVisible(true);

        officerUsername.setLabel("username (OFFICER)");
        officerUsername.setWidthFull();
        officerUsername.setReadOnly(true);

        officerPassword.setLabel("password (OFFICER)");
        officerPassword.setWidthFull();
        officerPassword.setRequiredIndicatorVisible(true);

        layoutRow2.setFlexGrow(1.0, layoutColumn4);
        layoutColumn4.setWidth(null);


        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidthFull();

        layoutRow3.setFlexGrow(1.0, officerPassword);
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
        layoutColumn2.add(clubName);
        layoutColumn2.add(layoutRow2);

        layoutRow2.add(layoutColumn3);
        layoutColumn3.add(supervisorUsername);
        layoutColumn3.add(supervisorPassword);

        layoutRow2.add(layoutColumn4);
        layoutColumn4.add(officerUsername);
        layoutColumn4.add(layoutRow3);
        layoutRow3.add(officerPassword);

        layoutColumn2.add(layoutRow4);
        layoutColumn2.add(layoutRow5);
        layoutRow4.add(buttonPrimary);
        layoutRow4.add(buttonSecondary);
        layoutRow5.add(outPut[0]);
        layoutRow.add(layoutColumn6);

        clubName.addValueChangeListener( e -> {
            if (e.getValue().equals("")) {
                supervisorUsername.setValue("");
                officerUsername.setValue("");
            }
            else {
                String clubUsername = e.getValue().replaceAll(" ", "");
                supervisorUsername.setValue(clubUsername+"(SUPERVISOR)");
                officerUsername.setValue(clubUsername+"(OFFICER)");
            }
        });

        //submit button clicked
        buttonPrimary.addClickListener(buttonClickEvent -> {
            layoutRow5.remove(outPut[0]);

            if (clubName.isEmpty()) {
                outPut[0] = new Paragraph("WARNING: PLEASE FILL THE NAME OF THE CLUB");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            else {

                //encode/encrypt password to be added to the database
                String supervisorPassCrypt = new BCryptPasswordEncoder().encode(supervisorPassword.getValue());
                String officerPassCrypt = new BCryptPasswordEncoder().encode(officerPassword.getValue());

                //creating new users
                User newSupervisor = new User(supervisorUsername.getValue(), supervisorPassCrypt, "SUPERVISOR", clubName.getValue());
                User newOfficer = new User(officerUsername.getValue(), officerPassCrypt, "OFFICER", clubName.getValue());


                outPut[0] = new Paragraph("club " + clubName.getValue() + " has successfully been created!");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                //updating/adding database with new users
                service.update(newSupervisor);
                service.update(newOfficer);

                //updating/adding database with new club
                clubsService.update(new Clubs(clubName.getValue(), 0, 0, 0));

            }
            notification.setText(String.valueOf(outPut[0].getText()));
            layoutRow5.add(outPut[0]);
            //reset
            notification.open();
            clubName.setValue("");
            supervisorUsername.setValue("");
            officerUsername.setValue("");
            supervisorPassword.setValue("");
            officerPassword.setValue("");
        });

        buttonSecondary.addClickListener(buttonClickEvent -> {
            layoutRow5.remove(outPut[0]);
            clubName.setValue("");
            supervisorUsername.setValue("");
            officerUsername.setValue("");
            supervisorPassword.setValue("");
            officerPassword.setValue("");
        });




    }
}

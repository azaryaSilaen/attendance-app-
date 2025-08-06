package com.example.application.views.scan;

import com.example.application.data.entity.Students;
import com.example.application.data.service.ClubsService;
import com.example.application.data.service.StudentsService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;

@PageTitle("Scan")
@Route(value = "scan", layout = MainLayout.class)
@RolesAllowed({"SUPERVISOR", "OFFICER"})
@JsModule("vaadin-zxing-reader.js")
@NpmPackage(value = "@zxing/browser", version = "^0.1.1")
public class ScanView extends VerticalLayout {

    ClubsService service;
    StudentsService studentsService;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    String club = username.substring(0, username.indexOf("("));
    static String lastMeeting;
    static int verified = 0;
    static boolean newMeeting;
    Notification notification = new Notification();
    static ArrayList<String> failedAttendances = new ArrayList<>();
    static ArrayList<Integer> successAttendances = new ArrayList<>();

    public ScanView(ClubsService service, StudentsService studentsService) {

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        this.service = service;
        this.studentsService = studentsService;
        // Retrieve information about the club's attendance for display or processing
        // Get the details of the last meeting attended by the club
        lastMeeting = service.selectLastMeeting(club);
        // Count the number of verified attendance records for the current date and club
        verified = studentsService.countLastAttendance(String.valueOf(LocalDate.now()), club);
        setSpacing(false);

        //final Paragraph[] success = {new Paragraph("nothing to see here")}; //starting paragraph
        Button startMeeting = new Button();
        startMeeting.setWidth("200px");
        startMeeting.setHeight("100px");

        //checking whether club has conducted a meeting today
        if (lastMeeting.equals(String.valueOf(LocalDate.now()))) {
            //if yes, then it's not a new meeting
            // don't increase num of meeting
            startMeeting.setText("continue Attendance?");
            newMeeting = false;
            startMeeting.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        }
        else {
            //if no, then it's a new meeting
            // increase num of meeting
            startMeeting.setText("start meeting?");
            newMeeting = true;
            startMeeting.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }
        var layout = new VerticalLayout(startMeeting);
        layout.setSpacing(false);
        layout.setSizeFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);
        add(layout);

        ZXingVaadinReader zXingVaadin = new ZXingVaadinReader();
        zXingVaadin.setFrom(Constants.From.camera); //scan using camera
        zXingVaadin.setId("video"); //id needs to be 'video' if From.camera.
        zXingVaadin.setWidth("350"); //pixel
        zXingVaadin.setStyle("border : 1px solid gray");
        final Paragraph[] result = new Paragraph[5];
        result[0] = new Paragraph("nothing to see here");
        final Paragraph[] numVerified = new Paragraph[5];
        numVerified[0] = new Paragraph(verified + "/" + service.countStudents(club) + " has been verified");

        startMeeting.addClickListener(e ->{
            failedAttendances.clear();
            successAttendances.clear();

            //making sure that number of meeting and last meeting is updated
            if (newMeeting) {
                service.updateLastMeeting(String.valueOf(LocalDate.now()), club);
                service.updateNumMeetings(club);
            }

            add(zXingVaadin);
            add(numVerified[0]);
            remove(layout);

            // QR scanner event listener
            zXingVaadin.addValueChangeListener(f -> {
                int studentID = 0; // Initialize student ID variable
                boolean alreadyCheckWrong = false; // Initialize flag for already checked wrong input
                remove(result[0], numVerified[0]); // Remove previous results from UI

                // Attempt to parse the scanned value as an integer to get the student ID
                try {
                    studentID = Integer.parseInt(f.getValue());
                } catch (NumberFormatException ignored) {
                    // Ignore if the scanned value is not a valid integer
                }
                Students studentFound = studentsService.searchByStudentIdAndClub(studentID,club);
                if (studentFound == null) {

                    //ensuring that the application don't just kept returning the same message
                    //even though the information is already retrieve beforehand (only temporary storing)
                    for (String failedAttendance : failedAttendances) {
                        if (failedAttendance.equals(f.getValue())) {
                            alreadyCheckWrong = true;
                            break;
                        }
                    }

                    //first time wrong code
                    if (!alreadyCheckWrong) {
                        result[0] = new Paragraph("you are not member of this club, please sign up first");
                        failedAttendances.add(f.getValue());
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                }
                else {
                    //student is part of club and haven't took their attendance
                    //used to prevent double counting
                    if (!studentsService.selectLastAttendance(studentID,club).equals(String.valueOf(LocalDate.now())))
                    {
                        result[0] = new Paragraph("Hello " + studentFound.getName() + " :)");
                        studentsService.updateAttendance(Integer.parseInt(f.getValue()),club);
                        studentsService.updateLastAttendance(String.valueOf(LocalDate.now()), Integer.parseInt(f.getValue()),club);
                        verified++;
                        successAttendances.add(studentID);
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }
                    else {
                        result[0] = new Paragraph("you're already previously verified");
                        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                    }

                }
                numVerified[0] = new Paragraph(verified + "/" + service.countStudents(club) + " has been verified");
                System.out.println("==QR=====:"+f.getValue());
                //pop up information
                notification.setText(result[0].getText());
                notification.setDuration(5000);
                notification.setPosition(Notification.Position.TOP_CENTER);

                if (!alreadyCheckWrong) notification.open();
                add(numVerified[0]);
            });


            /*
            Button buttonPrimary = new Button();
            buttonPrimary.setText("done?");
            buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttonPrimary.setWidth("250");
            add(buttonPrimary);

            buttonPrimary.addClickListener(e ->{
                add(zXingVaadin);
                remove(success[0]);
            });

            Button buttonDelete = new Button();
            buttonDelete.setText("delete");
            buttonDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            add(buttonDelete);

            buttonDelete.addClickListener(e ->{
                remove(zXingVaadin);
                remove(success[0]);
            });

             */
        });




        /*
        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        H2 header = new H2("This place intentionally left empty");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

         */


    }

}

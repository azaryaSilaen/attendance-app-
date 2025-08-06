package com.example.application.views.memberslist;

import com.example.application.data.entity.Students;
import com.example.application.data.service.StudentsService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.AopInvocationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Members List")
@Route(value = "members-list", layout = MainLayout.class)
@RolesAllowed({"SUPERVISOR", "OFFICER"})
public class MembersListView extends Div {

    //create gridPro
    private GridPro<Students> grid;
    private GridListDataView<Students> gridListDataView;

    //create each column
    private Grid.Column<Students> idColumn;
    private Grid.Column<Students> nameColumn;
    private Grid.Column<Students> gradeColumn;
    private Grid.Column<Students> attendanceColumn;
    private Grid.Column<Students> statusColumn;
    private Grid.Column<Students> dateJoinColumn;
    private Grid.Column<Students> deleteColumn;

    //div object
    private static Div hint;

    //notification
    Notification notification = new Notification();

    //retrieve and later check role of user
    static String role;

    //access student database through service class through JPA repository
    StudentsService service;

    double minAttendance = 80;

    // private String img;
    //    private int id;
    //    private String name;
    //    private int grade;
    //    private String club;
    //    private double attendance;
    //    private String status;
    //    private String dateJoin;

    public MembersListView(StudentsService service) {

        // Retrieve the currently authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Iterate through the user's granted authorities to determine their role
        //this is as each user could have many roles (though in this context, not)
        for (GrantedAuthority permission : auth.getAuthorities()) {
            role = permission.getAuthority();
        }
        this.service = service;

        hint = new Div();
        hint.setText("No members");
        hint.getStyle().set("padding", "var(--lumo-size-l)")
                .set("text-align", "center").set("font-style", "italic")
                .set("color", "var(--lumo-contrast-70pct)");

        addClassName("members-list-view");
        setSizeFull();
        createGrid();
        add(hint, grid);

        if (role.equals("ROLE_SUPERVISOR")) {

            notification.setDuration(5000);
            notification.setPosition(Notification.Position.TOP_CENTER);

            // If the authenticated user has the role of "ROLE_SUPERVISOR"
            // Add a listener to handle property changes in the grid
            grid.addItemPropertyChangedListener(g -> {
                // Retrieve the modified student object from the grid
                Students newStudent = g.getItem();
                if (!isValid(newStudent.getName(), newStudent.getGrade(), newStudent.getStudentId())) {
                    notification.setText("WARNING: please fill out with the correct format");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                else {
                    service.update(newStudent);
                    notification.setText("changes has been made");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }

                notification.open();
            });
        }

        /*
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(
                new Text("Are you sure to delete this student?"),
                new HtmlComponent("br"),  new HtmlComponent("br"),
                new Text("This action is irreversible")
        );

        Button confirmButton = new Button("confirm");
        confirmButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        confirmButton.setAriaLabel("Close");
        Button declineButton = new Button("decline");
        declineButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        declineButton.setAriaLabel("undo");


        HorizontalLayout layout = new HorizontalLayout(text, confirmButton, declineButton);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.open();

         */

         /*
            grid.addSelectionListener(e ->{
                Set<Students> students = e.getAllSelectedItems();
                confirmButton.addClickListener(f -> {
                    for (Students s: students) {
                        service.delete(s.getId());
                        grid.getDataProvider().refreshAll();
                    }
                    notification.close();
                });
                declineButton.addClickListener(event -> {
                    notification.close();
                });
            });

             */

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

    private void refreshGrid() {
        if (getStudents().size() > 0) {
            grid.setVisible(true);
            hint.setVisible(false);
            grid.setItems(getStudents());
        } else {
            grid.setVisible(false);
            hint.setVisible(true);
        }
    }

    private void createGrid() {
        // Create the grid component
        createGridComponent();

        // Check if the authenticated user has the role of "ROLE_SUPERVISOR"
        if (role.equals("ROLE_SUPERVISOR")) {
            // If so, add supervisor-specific columns to the grid
            addSupervisorColumnsToGrid();
        } else {
            // Otherwise, add default columns to the grid
            addColumnsToGrid();
        }
        addFiltersToGrid();
        refreshGrid();
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        List<Students> students = getStudents();
        gridListDataView = grid.setItems(students);
    }

    private void addColumnsToGrid() {
        createIdColumn();
        createNameColumn();
        createGradeColumn();
        createAttendanceColumn();
        createStatusColumn();
        createDateJoinColumn();
    }

    private void addSupervisorColumnsToGrid() {
        createEditIdColumn();
        createEditNameColumn();
        createEditGradeColumn();
        createAttendanceColumn();
        createStatusColumn();
        createDateJoinColumn();
        createDeleteColumn();
    }

    //number
    /**
     * Creates a column for displaying student IDs in the grid.
     */
    private void createIdColumn() {
        // Create a new grid column using a ComponentRenderer to display student IDs.
        idColumn = grid.addColumn(new ComponentRenderer<>(student -> {
                    // Create a horizontal layout to center-align the content.
                    HorizontalLayout hl = new HorizontalLayout();
                    hl.setAlignItems(Alignment.CENTER);

                    // Create a Span element to display the student ID.
                    Span span = new Span();
                    span.setClassName("name"); // Apply CSS class if needed.
                    span.setText(String.valueOf(student.getStudentId()));

                    // Add the Span to the horizontal layout.
                    hl.add(span);

                    return hl; // Return the horizontal layout as the cell content.
                })).setComparator(students -> students.getStudentId()) // Set a comparator for sorting.
                .setHeader("Student ID").setWidth("100px"); // Set the column header text.
    }

    private void createEditIdColumn() {
        // Create a new grid column using a ComponentRenderer to display student IDs.
        idColumn = grid.addEditColumn(Students::getStudentId, new ComponentRenderer<>(student -> {
                    // Create a horizontal layout to center-align the content.
                    HorizontalLayout hl = new HorizontalLayout();
                    hl.setAlignItems(Alignment.CENTER);

                    // Create a Span element to display the student ID.
                    Span span = new Span();
                    span.setClassName("name"); // Apply CSS class if needed.
                    span.setText(String.valueOf(student.getStudentId()));

                    // Add the Span to the horizontal layout.
                    hl.add(span);

                    return hl; // Return the horizontal layout as the cell content.
                })).text((item, newValue) -> item.setStudentId(Integer.parseInt(newValue))).setComparator(students -> students.getStudentId()) // Set a comparator for sorting.
                .setHeader("Student ID").setWidth("100px"); // Set the column header text.
    }


    //create column for name
    private void createNameColumn() {
        nameColumn = grid.addColumn(new ComponentRenderer<>(student -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("name");
            span.setText(student.getName());
            hl.add(span);
            return hl;
        })).setComparator(students -> students.getName()).setHeader("Name").setAutoWidth(true);
    }

    //editable version for supervisor
    private void createEditNameColumn() {
        nameColumn = grid.addEditColumn(Students::getName, new ComponentRenderer<>(student -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("name");
            span.setText(student.getName());
            hl.add(span);
            return hl;
        })).text((item, newValue) -> item.setName(newValue)).setComparator(students -> students.getName())
                .setHeader("Name").setAutoWidth(true);
    }


    //create column for student's grade
    private void createGradeColumn() {
        gradeColumn = grid.addColumn(new ComponentRenderer<>(student -> {
            Span span = new Span();
            String gradeText = String.valueOf(student.getGrade());
            span.setText(gradeText);
            span.getElement().setAttribute("theme", "badge " + student.getGrade());
            return span;
        })).setComparator(student -> student.getGrade()).setHeader("Grade").setWidth("75px");
    }


    //editable version for supervisor
    private void createEditGradeColumn() {
        gradeColumn = grid.addEditColumn(Students::getGrade, new ComponentRenderer<>(student -> {
                    Span span = new Span();
                    String gradeText = String.valueOf(student.getGrade());
                    span.setText(gradeText);
                    span.getElement().setAttribute("theme", "badge " + student.getGrade());
                    return span;
                })).select((item, newValue) -> item.setGrade(Integer.parseInt(newValue)), Arrays.asList("9","10","11","12"))
                .setComparator(student -> student.getGrade()).setHeader("Grade").setWidth("75px");

    }


    //create column for student's attendance
    private void createAttendanceColumn() {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        attendanceColumn = grid.addColumn(new ComponentRenderer<>(student -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("Attendance");
            double attendanceRate = ((double)student.getAttendance()/(double)service.selectSpecificNumMeeting(student.getClub()))*100;
            span.setText(numberFormat.format(attendanceRate) + "%");
            hl.add(span);
            return hl;
        })).setComparator(student -> numberFormat.format((double)student.getAttendance()/(double)service.selectSpecificNumMeeting(student.getClub())))
                .setHeader("Attendance").setWidth("100px");
    }

    //create column for student's status
    private void createStatusColumn() {
        statusColumn = grid.addColumn(new ComponentRenderer<>(student -> {
            Span span = new Span();
            String isActive = "";
            double attendanceRate = ((double)student.getAttendance()/(double)service.selectSpecificNumMeeting(student.getClub()))*100;
            if (attendanceRate >= minAttendance) {
                isActive = "active";
                span.getElement().setAttribute("theme", "badge " + "success");
            }
            else {
                isActive = "inactive";
                span.getElement().setAttribute("theme", "badge " + "warning");
            }
            service.updateStatus(isActive, student.getName(), student.getClub());
            student.setStatus(isActive);
            span.setText(student.getStatus());
            return span;
        })).setComparator(student -> student.getStatus()).setHeader("Status").setWidth("100px");
    }

    /*
    //combo
    private void createStatusColumn() {
        statusColumn = grid.addEditColumn(Students::getStatus, new ComponentRenderer<>(student -> {
                    Span span = new Span();
                    span.setText(student.getStatus());
                    span.getElement().setAttribute("theme", "badge " + student.getStatus().toLowerCase());
                    return span;
                })).select((item, newValue) -> item.setStatus(newValue), Arrays.asList("safe", "warning"))
                .setComparator(student -> student.getStatus()).setHeader("Status");
    }

     */

    //date
    private void createDateJoinColumn() {
        dateJoinColumn = grid
                .addColumn(new LocalDateRenderer<>(student -> LocalDate.parse(student.getDateJoin()),
                        () -> DateTimeFormatter.ofPattern("M/d/yyyy")))
                .setComparator(student -> student.getDateJoin()).setHeader("Joined").setWidth("100px");
    }

    //regular
    private void createDeleteColumn() {
        deleteColumn = grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, student) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> this.removeStudent(student));
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader("delete").setAutoWidth(true);
    }

    private void removeStudent(Students student) {
        if (student == null)
            return;
        service.deleteByStudentIdAndClub(student.getStudentId(), student.getClub());
        refreshGrid();
    }



    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        //number
        TextField idFilter = new TextField();
        idFilter.setPlaceholder("Filter");
        idFilter.setClearButtonVisible(true);
        idFilter.setWidth("100%");
        idFilter.setValueChangeMode(ValueChangeMode.EAGER);
        idFilter.addValueChangeListener(event -> gridListDataView.addFilter(students -> StringUtils
                .containsIgnoreCase(Double.toString(students.getStudentId()), idFilter.getValue())));
        filterRow.getCell(idColumn).setComponent(idFilter);

        //regular
        TextField nameFilter = new TextField();
        nameFilter.setPlaceholder("Filter");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setWidth("100%");
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(event -> gridListDataView
                .addFilter(student -> StringUtils.containsIgnoreCase(student.getName(), nameFilter.getValue())));
        filterRow.getCell(nameColumn).setComponent(nameFilter);

        ComboBox<String> gradeFilter = new ComboBox<>();
        gradeFilter.setItems(Arrays.asList("9","10","11","12"));
        gradeFilter.setPlaceholder("Filter");
        gradeFilter.setClearButtonVisible(true);
        gradeFilter.setWidth("100%");
        gradeFilter.addValueChangeListener(
                event -> gridListDataView.addFilter(students -> areGradesEqual(students, gradeFilter)));
        filterRow.getCell(gradeColumn).setComponent(gradeFilter);

        //number
        TextField attendanceFilter = new TextField();
        attendanceFilter.setPlaceholder("Filter");
        attendanceFilter.setClearButtonVisible(true);
        attendanceFilter.setWidth("100%");
        attendanceFilter.setValueChangeMode(ValueChangeMode.EAGER);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        attendanceFilter.addValueChangeListener(event -> gridListDataView.addFilter(students -> StringUtils
                .containsIgnoreCase(numberFormat.format(((double)students.getAttendance()/(double)service.selectSpecificNumMeeting(students.getClub()))*100), attendanceFilter.getValue())));
        filterRow.getCell(attendanceColumn).setComponent(attendanceFilter);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.setItems(Arrays.asList("active", "inactive"));
        statusFilter.setPlaceholder("Filter");
        statusFilter.setClearButtonVisible(true);
        statusFilter.setWidth("100%");
        statusFilter.addValueChangeListener(
                event -> gridListDataView.addFilter(students -> areStatusesEqual(students, statusFilter)));
        filterRow.getCell(statusColumn).setComponent(statusFilter);

        DatePicker dateJoinFilter = new DatePicker();
        dateJoinFilter.setPlaceholder("Filter");
        dateJoinFilter.setClearButtonVisible(true);
        dateJoinFilter.setWidth("100%");
        dateJoinFilter.addValueChangeListener(
                event -> gridListDataView.addFilter(students -> areDatesJoinEqual(students, dateJoinFilter)));
        filterRow.getCell(dateJoinColumn).setComponent(dateJoinFilter);
    }

    private boolean areStatusesEqual(Students student, ComboBox<String> statusFilter) {
        String statusFilterValue = statusFilter.getValue();
        if (statusFilterValue != null) {
            return StringUtils.equals(student.getStatus(), statusFilterValue);
        }
        return true;
    }

    private boolean areGradesEqual(Students student, ComboBox<String> gradeFilter) {
        String gradeFilterValue = gradeFilter.getValue();
        if (gradeFilterValue != null) {
            return StringUtils.equals(String.valueOf(student.getGrade()), gradeFilterValue);
        }
        return true;
    }

    private boolean areClubsEqual(Students student, ComboBox<String> clubFilter) {
        String clubFilterValue = clubFilter.getValue();
        if (clubFilterValue != null) {
            return StringUtils.equals(student.getClub(), clubFilterValue);
        }
        return true;
    }

    private boolean areDatesJoinEqual(Students students, DatePicker dateFilter) {
        LocalDate dateFilterValue = dateFilter.getValue();
        if (dateFilterValue != null) {
            LocalDate studentDate = LocalDate.parse(students.getDateJoin());
            return dateFilterValue.equals(studentDate);
        }
        return true;
    }

    // Method to retrieve a list of students from the database
    private List<Students> getStudents() {
        // Retrieve the currently authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Extract the username from the authentication object
        String username = auth.getName();
        // Extract the club name from the username as username format is "clubName(ROLE)")
        String club = username.substring(0, username.indexOf("("));
        // Initialize an empty list to hold the result
        List<Students> empty = new ArrayList<>();
        try {
            // Retrieve students associated with the club from the service layer
            return service.search(club);
        } catch (AopInvocationException e) {
            // If an exception occurs (e.g., database access issue), return an empty list
            return empty;
        }
    }

    /*
    private Students createStudent(int id, String name, int grade, String club, double attendance, String status, String dateJoin) {
        Students s = new Students();
        s.setId(id);
        s.setName(name);
        s.setGrade(grade);
        s.setClub(club);
        s.setAttendance(attendance);
        s.setStatus(status);
        s.setDateJoin(dateJoin);
        return s;
    }

     */


};

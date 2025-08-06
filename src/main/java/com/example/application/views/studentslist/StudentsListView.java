package com.example.application.views.studentslist;
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


@PageTitle("Students List")
@Route(value = "students-list", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class StudentsListView extends Div {




    private GridPro<Students> grid;
    private GridListDataView<Students> gridListDataView;

    private Grid.Column<Students> idColumn;
    private Grid.Column<Students> nameColumn;
    private Grid.Column<Students> gradeColumn;
    private Grid.Column<Students> clubColumn;
    private Grid.Column<Students> attendanceColumn;
    private Grid.Column<Students> statusColumn;
    private Grid.Column<Students> dateJoinColumn;


    private Grid.Column<Students> deleteColumn;

    public Button sendEmail = new Button("Send Email to Inactive?");

    Notification notification = new Notification();
    double minAttendance = 80;

    StudentsService service;
    private static Div hint;

    // private String img;
    //    private int id;
    //    private String name;
    //    private int grade;
    //    private String club;
    //    private double attendance;
    //    private String status;
    //    private String dateJoin;

    public double getMinAttendance() {
        return minAttendance;
    }
    public StudentsListView(StudentsService service) {

        notification.setDuration(5000);
        notification.setPosition(Notification.Position.TOP_CENTER);

        this.service = service;

        hint = new Div();
        hint.setText("No students");
        hint.getStyle().set("padding", "var(--lumo-size-l)")
                .set("text-align", "center").set("font-style", "italic")
                .set("color", "var(--lumo-contrast-70pct)");

        addClassName("students-list-view");
        setSizeFull();
        createGrid();
        add(hint, grid);

        grid.addItemPropertyChangedListener(e -> {
            Students newStudent = e.getItem();

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

    /*@WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    }

    @Configuration
    @EnableVaadin
    public static class MyConfiguration {
    }*/

    private void refreshGrid() {
        if (getStudents().size() > 0) {
            grid.setVisible(true);
            sendEmail.setVisible(true);
            hint.setVisible(false);
            grid.setItems(getStudents());
        } else {
            grid.setVisible(false);
            sendEmail.setVisible(false);
            hint.setVisible(true);
        }
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
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
        createClubColumn();
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
                .setHeader("Student ID").setWidth("125px"); // Set the column header text.
    }


    //regular
    private void createNameColumn() {
        nameColumn = grid.addEditColumn(Students::getName, new ComponentRenderer<>(student -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("name");
            span.setText(student.getName());
            hl.add(span);
            return hl;
        })).text((item, newValue) -> item.setName(newValue)).setComparator(students -> students.getName()).setHeader("Name").setAutoWidth(true);
    }

    //combo
    private void createGradeColumn() {
        gradeColumn = grid.addEditColumn(Students::getGrade, new ComponentRenderer<>(student -> {
                    Span span = new Span();
                    String gradeText = String.valueOf(student.getGrade());
                    span.setText(gradeText);
                    span.getElement().setAttribute("theme", "badge " + student.getGrade());
                    return span;
                })).select((item, newValue) -> item.setGrade(Integer.parseInt(newValue)), Arrays.asList("9","10","11","12"))
                .setComparator(student -> student.getGrade()).setHeader("Grade").setWidth("100px");

    }

    /*
    //combo
    private void createGradeColumn() {
        gradeColumn = grid.addEditColumn(Students::getGrade, new ComponentRenderer<>(student -> {
                    Span span = new Span();
                    String gradeText = String.valueOf(student.getGrade());
                    span.setText(gradeText);
                    span.getElement().setAttribute("theme", "badge " + student.getGrade());
                    return span;
                })).select((item, newValue) -> item.setGrade(Integer.parseInt(newValue)), Arrays.asList("9","10","11","12"))
                .setComparator(student -> student.getGrade()).setHeader("Grade");
    }

     */

    //combo
    private void createClubColumn() {
        clubColumn = grid.addColumn(new ComponentRenderer<>(student -> {
                    Span span = new Span();
                    span.setText(student.getClub());
                    span.getElement().setAttribute("theme", "badge " + student.getClub().toLowerCase());
                    return span;
                }))
                .setComparator(student -> student.getClub()).setHeader("Club").setAutoWidth(true);
    }

    //number
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
        })).setComparator(student -> numberFormat.format((double)student.getAttendance()/(double)service.selectSpecificNumMeeting(student.getClub()))).setHeader("Attendance")
                .setWidth("125px");
    }

    //combo
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
        })).setComparator(student -> student.getStatus()).setHeader("Status").setWidth("125px");
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

        ComboBox<String> clubFilter = new ComboBox<>();
        clubFilter.setItems(Arrays.asList("chess", "code"));
        clubFilter.setPlaceholder("Filter");
        clubFilter.setClearButtonVisible(true);
        clubFilter.setWidth("100%");
        clubFilter.addValueChangeListener(
                event -> gridListDataView.addFilter(students -> areClubsEqual(students, clubFilter)));
        filterRow.getCell(clubColumn).setComponent(clubFilter);

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

    private List<Students> getStudents() {
        List<Students> empty = new ArrayList<>();
        try {
            return service.findAll();
        } catch (AopInvocationException e) {
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

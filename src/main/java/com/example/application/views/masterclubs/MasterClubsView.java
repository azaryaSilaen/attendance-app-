package com.example.application.views.masterclubs;

import com.example.application.data.entity.Clubs;
import com.example.application.data.service.ClubsService;
import com.example.application.data.service.StudentsService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.AopInvocationException;

@PageTitle("Master Clubs")
@Route(value = "Master-clubs", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class MasterClubsView extends Div {

    private GridPro<Clubs> grid;
    private GridListDataView<Clubs> gridListDataView;

    private Grid.Column<Clubs> nameColumn;
    private Grid.Column<Clubs> numStudentsColumn;
    private Grid.Column<Clubs> numMeetingsColumn;
    private Grid.Column<Clubs> attendanceColumn;
    ClubsService service;

    public MasterClubsView(ClubsService service) {
        this.service = service;
        addClassName("master-clubs-view");
        setSizeFull();
        createGrid();
        add(grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        List<Clubs> clubs = getClubs();
        gridListDataView = grid.setItems(clubs);
    }

    private void addColumnsToGrid() {
        createNameColumn();
        createNumStudentsColumn();
        createNumMeetingsColumn();
        createAttendanceColumn();
    }

    private void createNameColumn() {
        nameColumn = grid.addColumn(new ComponentRenderer<>(Clubs -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("name");
            span.setText(Clubs.getName());
            hl.add(span);
            return hl;
        })).setComparator(Clubs -> Clubs.getName()).setHeader("Name")
                .setAutoWidth(true);
    }

    //combo
    private void createNumStudentsColumn() {
        numStudentsColumn = grid.addColumn(new ComponentRenderer<>(clubs -> {
            Span span = new Span();
            String numStudentsText = String.valueOf(clubs.getNumStudents());
            span.setText(numStudentsText);
            span.getElement().setAttribute("theme", "badge " + clubs.getNumStudents());
            return span;
        })).setComparator(clubs -> clubs.getNumStudents()).setHeader("total members").setWidth("50px");
    }

    //combo
    private void createNumMeetingsColumn() {
        numMeetingsColumn = grid.addColumn(new ComponentRenderer<>(clubs -> {
            Span span = new Span();
            String numMeetingsText = String.valueOf(clubs.getNumMeeting());
            span.setText(numMeetingsText);
            span.getElement().setAttribute("theme", "badge " + clubs.getNumMeeting());
            return span;
        })).setComparator(clubs -> clubs.getNumMeeting()).setHeader("total meetings").setWidth("50px");
    }

    //number
    private void createAttendanceColumn() {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        attendanceColumn = grid.addColumn(new ComponentRenderer<>(clubs -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("Attendance");
            double attendanceRate = (clubs.getAttendance()/((service.selectSpecificNumMeeting(clubs.getName()))*service.countStudents(clubs.getName())))*100;
            span.setText(numberFormat.format(attendanceRate) + "%");
            hl.add(span);
            return hl;
        })).setComparator(clubs -> numberFormat.format(clubs.getAttendance()/((service.selectSpecificNumMeeting(clubs.getName()))*service.countStudents(clubs.getName()))))
                .setHeader("Attendance rate").setWidth("50px");
    }

    /*
    private void createClientColumn() {
        clientColumn = grid.addColumn(new ComponentRenderer<>(client -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Image img = new Image(client.getImg(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(client.getClient());
            hl.add(img, span);
            return hl;
        })).setComparator(client -> client.getClient()).setHeader("Client");
    }

    private void createAmountColumn() {
        amountColumn = grid
                .addEditColumn(Client::getAmount,
                        new NumberRenderer<>(client -> client.getAmount(), NumberFormat.getCurrencyInstance(Locale.US)))
                .text((item, newValue) -> item.setAmount(Double.parseDouble(newValue)))
                .setComparator(client -> client.getAmount()).setHeader("Amount");
    }

    private void createStatusColumn() {
        statusColumn = grid.addEditColumn(Client::getClient, new ComponentRenderer<>(client -> {
            Span span = new Span();
            span.setText(client.getStatus());
            span.getElement().setAttribute("theme", "badge " + client.getStatus().toLowerCase());
            return span;
        })).select((item, newValue) -> item.setStatus(newValue), Arrays.asList("Pending", "Success", "Error"))
                .setComparator(client -> client.getStatus()).setHeader("Status");
    }

    private void createDateColumn() {
        dateColumn = grid
                .addColumn(new LocalDateRenderer<>(client -> LocalDate.parse(client.getDate()),
                        () -> DateTimeFormatter.ofPattern("M/d/yyyy")))
                .setComparator(client -> client.getDate()).setHeader("Date").setWidth("180px").setFlexGrow(0);
    }

     */

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField nameFilter = new TextField();
        nameFilter.setPlaceholder("Filter");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setWidth("100%");
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(event -> gridListDataView
                .addFilter(clubs -> StringUtils.containsIgnoreCase(clubs.getName(), nameFilter.getValue())));
        filterRow.getCell(nameColumn).setComponent(nameFilter);

        TextField numStudentsFilter = new TextField();
        numStudentsFilter.setPlaceholder("Filter");
        numStudentsFilter.setClearButtonVisible(true);
        numStudentsFilter.setWidth("100%");
        numStudentsFilter.setValueChangeMode(ValueChangeMode.EAGER);
        numStudentsFilter.addValueChangeListener(event -> gridListDataView.addFilter(clubs -> StringUtils
                .containsIgnoreCase(Double.toString(clubs.getNumStudents()), numStudentsFilter.getValue())));
        filterRow.getCell(numStudentsColumn).setComponent(numStudentsFilter);

        TextField numMeetingsFilter = new TextField();
        numMeetingsFilter.setPlaceholder("Filter");
        numMeetingsFilter.setClearButtonVisible(true);
        numMeetingsFilter.setWidth("100%");
        numMeetingsFilter.setValueChangeMode(ValueChangeMode.EAGER);
        numMeetingsFilter.addValueChangeListener(event -> gridListDataView.addFilter(clubs -> StringUtils
                .containsIgnoreCase(Double.toString(clubs.getNumMeeting()), numMeetingsFilter.getValue())));
        filterRow.getCell(numMeetingsColumn).setComponent(numMeetingsFilter);

        TextField attendanceFilter = new TextField();
        attendanceFilter.setPlaceholder("Filter");
        attendanceFilter.setClearButtonVisible(true);
        attendanceFilter.setWidth("100%");
        attendanceFilter.setValueChangeMode(ValueChangeMode.EAGER);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        attendanceFilter.addValueChangeListener(event -> gridListDataView.addFilter(clubs -> StringUtils
                .containsIgnoreCase(numberFormat.format(clubs.getAttendance()/((service.selectSpecificNumMeeting(clubs.getName()))*service.countStudents(clubs.getName()))), attendanceFilter.getValue())));
        filterRow.getCell(attendanceColumn).setComponent(attendanceFilter);

        /*
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.setItems(Arrays.asList("Pending", "Success", "Error"));
        statusFilter.setPlaceholder("Filter");
        statusFilter.setClearButtonVisible(true);
        statusFilter.setWidth("100%");
        statusFilter.addValueChangeListener(
                event -> gridListDataView.addFilter(client -> areStatusesEqual(client, statusFilter)));
        filterRow.getCell(statusColumn).setComponent(statusFilter);

        DatePicker dateFilter = new DatePicker();
        dateFilter.setPlaceholder("Filter");
        dateFilter.setClearButtonVisible(true);
        dateFilter.setWidth("100%");
        dateFilter.addValueChangeListener(
                event -> gridListDataView.addFilter(client -> areDatesEqual(client, dateFilter)));
        filterRow.getCell(dateColumn).setComponent(dateFilter);

         */
    }

    /*
    private boolean areStatusesEqual(Client client, ComboBox<String> statusFilter) {
        String statusFilterValue = statusFilter.getValue();
        if (statusFilterValue != null) {
            return StringUtils.equals(client.getStatus(), statusFilterValue);
        }
        return true;
    }

    private boolean areDatesEqual(Client client, DatePicker dateFilter) {
        LocalDate dateFilterValue = dateFilter.getValue();
        if (dateFilterValue != null) {
            LocalDate clientDate = LocalDate.parse(client.getDate());
            return dateFilterValue.equals(clientDate);
        }
        return true;
    }

     */

    private List<Clubs> getClubs() {
        // Initialize an empty list to hold clubs
        List<Clubs> empty = new ArrayList<>();

        // Check if there are any clubs in the database
        if (service.findAll() == null) {
            return empty; // If no clubs found, return an empty list
        } else {
            // If clubs are found, update all clubs in the service layer
            service.updateAll();
            // Return the list of all clubs
            return service.findAll();
        }
    }
};

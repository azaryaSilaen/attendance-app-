package com.example.application.views;

import com.example.application.data.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.about.AboutView;
import com.example.application.views.clubdashboard.ClubDashboardView;
import com.example.application.views.masterclubs.MasterClubsView;
import com.example.application.views.masterclubsdashboard.MasterClubsDashboardView;
import com.example.application.views.memberslist.MembersListView;
import com.example.application.views.newclubform.NewClubFormView;
import com.example.application.views.newmemberform.NewMemberFormView;
import com.example.application.views.scan.ScanView;
import com.example.application.views.sendemail.SendEmailView;
import com.example.application.views.studentslist.StudentsListView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Attendance App ");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(MasterClubsView.class)) {
            nav.addItem(new SideNavItem("Master Clubs", MasterClubsView.class, LineAwesomeIcon.TH_SOLID.create()));

        }
        if (accessChecker.hasAccess(NewMemberFormView.class)) {
            nav.addItem(new SideNavItem("New Member Form", NewMemberFormView.class, LineAwesomeIcon.USER.create()));

        }
        if (accessChecker.hasAccess(StudentsListView.class)) {
            nav.addItem(new SideNavItem("Students List", StudentsListView.class, LineAwesomeIcon.TH_SOLID.create()));

        }
        if (accessChecker.hasAccess(StudentsListView.class)) {
            nav.addItem(new SideNavItem("Sending Emails", SendEmailView.class, LineAwesomeIcon.ADDRESS_BOOK_SOLID.create()));

        }
        if (accessChecker.hasAccess(MembersListView.class)) {
            nav.addItem(new SideNavItem("Members List", MembersListView.class, LineAwesomeIcon.TH_SOLID.create()));

        }
        if (accessChecker.hasAccess(NewClubFormView.class)) {
            nav.addItem(new SideNavItem("New Club Form", NewClubFormView.class,
                    LineAwesomeIcon.USER.create()));

        }
        /*
        if (accessChecker.hasAccess(MasterClubsDashboardView.class)) {
            nav.addItem(new SideNavItem("Master Clubs Dashboard", MasterClubsDashboardView.class,
                    LineAwesomeIcon.CHART_AREA_SOLID.create()));

        }
        if (accessChecker.hasAccess(ClubDashboardView.class)) {
            nav.addItem(new SideNavItem("Club Dashboard", ClubDashboardView.class,
                    LineAwesomeIcon.CHART_AREA_SOLID.create()));

        }

         */
        if (accessChecker.hasAccess(ScanView.class)) {
            nav.addItem(new SideNavItem("Scan", ScanView.class, LineAwesomeIcon.ADDRESS_BOOK_SOLID.create()));

        }
        if (accessChecker.hasAccess(AboutView.class)) {
            nav.addItem(new SideNavItem("About", AboutView.class, LineAwesomeIcon.PENCIL_RULER_SOLID.create()));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getUsername());
            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getUsername());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}

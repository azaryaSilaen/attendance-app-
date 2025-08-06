package com.example.application.views.login;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * The "LoginView" class represents the login page of the Attendance App. This view allows users to
 * authenticate themselves and gain access to the application. It extends the Vaadin "LoginOverlay"
 * component and implements the "BeforeEnterObserver" interface to handle navigation events before
 * entering the view.
 *
 *
 * @see LoginOverlay
 * @see BeforeEnterObserver
 */
@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    //LoginOverlay loginOverlay = new LoginOverlay();
    private final AuthenticatedUser authenticatedUser;

    /**
     * Constructs a new "LoginView" instance.
     *
     * @param authenticatedUser The authenticated user context, used to check if a user is already logged in.
     */
    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        //context where HTTP POST is sent to
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        //creating default login form
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Attendance App ");
        i18n.getHeader().setDescription("Login using clubName(ROLE)");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);
        setForgotPasswordButtonVisible(false);

        //opened the view
        setOpened(true);

         /*
        //creating the default LoginView
        loginOverlay.setForgotPasswordButtonVisible(false);
        loginOverlay.setTitle("Attendance App ");
        loginOverlay.setDescription("Login using clubName(ROLE)");
        add(loginOverlay);
        loginOverlay.setOpened(true);

         */
    }

    /**
     * This method is called before entering the login view. It checks if a user is already logged in
     * by using the authenticated user context. If a user is logged in, the view is closed, and the user
     * is forwarded to the application's home page. Additionally, it checks if there is an error parameter
     * in the URL, indicating a failed login attempt.
     *
     * @param event The before-enter event triggered when navigating to this view.
     */

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in so cosed the LoginView and go to mainView
            setOpened(false);
            event.forwardTo("");
        }

        //checking if there's an error in the URL and if there is,
        // setError would be true and error message will be presented
        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}

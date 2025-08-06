package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class AboutView extends Composite<VerticalLayout> {

    public AboutView() {
        Paragraph introduction = new Paragraph();
        getContent().setHeightFull();
        getContent().setWidthFull();

        introduction.setText("Hey there, welcome to our my attendance application for clubs! \uD83C\uDF89 " +
                "I have spent 2 years working on this application and my hope through this application, " +
                "organizing club and members become so simple that " +
                "you can spend more time having fun with your crew and less time stressing about attendance. \n");

        Header heading1 = new Header();
        heading1.setText("Background");

        Paragraph backgroundText = new Paragraph();
        backgroundText.setText("I became aware of the attendance problem first through seeing the difficulty of it as someone who has been in a leadership position. " +
                "Every club uses its own method, such as Google Forms or spreadsheets, but they all result in inconsistencies, inefficiency, and difficulties in verifying attendance. " +
                "Without a centralized system, the school cannot authenticate attendance reports, leading to potential inaccuracies and misunderstandings. " +
                "I also eventually realized that this issue is particularly significant as the school needs to provide evidence of student participation for university applications. " +
                "Realizing all of these problems, I finally decided to create an attendance application to streamline the process and address these challenges as part of my CS IA project");

        Paragraph createdBy1 = new Paragraph();
        createdBy1.setText("Created by Azarya Krishna Hasian Silaen - class of 2024");

        Header heading2 = new Header();
        heading2.setText("Contacts (I appreciate all kinds of feedback!!)\n");

        Paragraph contactsText1 = new Paragraph();
        contactsText1.setText("Linkedin: Azarya Silaen");
        Paragraph contactsText2 = new Paragraph();
        contactsText2.setText("Instagram: @azaryakrishna");
        Paragraph contactsText3 = new Paragraph();
        contactsText3.setText("Discord: sibangpitung");

        getContent().setAlignSelf(FlexComponent.Alignment.START, introduction, heading1, backgroundText, createdBy1,
                heading2, contactsText1, contactsText2, contactsText3);
        introduction.getStyle().set("font-size", "var(--lumo-font-size-l)");
        heading1.getStyle().set("font-size", "var(--lumo-font-size-xl)");
        backgroundText.getStyle().set("font-size", "var(--lumo-font-size-l)");
        createdBy1.getStyle().set("font-size", "var(--lumo-font-size-l)");
        contactsText2.getStyle().set("font-size", "var(--lumo-font-size-l)");
        heading2.getStyle().set("font-size", "var(--lumo-font-size-xl)");
        contactsText1.getStyle().set("font-size", "var(--lumo-font-size-l)");
        contactsText2.getStyle().set("font-size", "var(--lumo-font-size-l)");
        contactsText3.getStyle().set("font-size", "var(--lumo-font-size-l)");
        getContent().add(introduction, heading1, backgroundText, createdBy1, heading2, contactsText1, contactsText2, contactsText3);
    }
}

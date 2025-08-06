package com.example.application.data.entity;/*
Author: Azarya Silaen
 */

import com.vaadin.flow.component.textfield.TextField;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clubs")
public class Clubs extends AbstractEntity  {

    private String name;        // Name of the student.
    private int numStudents;          // number of members/students in the club.
    private int numMeeting;          // number of meetings the club had.
    private double attendance;  // average Attendance percentage of the club.
    @Column(name = "last_meeting")
    private String lastMeeting;

    public Clubs(String name, int numStudents, int numMeeting, double attendance) {
       this.name = name;
       this.numStudents = numStudents;
       this.numMeeting = numMeeting;
       this.attendance = attendance;
       lastMeeting = "no meeting";
    }

    public Clubs() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public int getNumMeeting() {
        return numMeeting;
    }

    public void setNumMeeting(int numMeeting) {
        this.numMeeting = numMeeting;
    }

    public double getAttendance() {
        return attendance;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public String getLastMeeting() {
        return lastMeeting;
    }

    public void setLastMeeting(String lastMeeting) {
        this.lastMeeting = lastMeeting;
    }
}

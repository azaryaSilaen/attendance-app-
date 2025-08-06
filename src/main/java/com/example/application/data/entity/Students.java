package com.example.application.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Date;

/**
 * The `Students` class represents information about a student, including their
 * student ID, name, grade, club membership, attendance, status, and date of joining.
 */
@Entity
@Table(name = "students")
public class Students extends AbstractEntity {

    private int studentId;             // Unique identifier for the student.
    private String name;        // Name of the student.
    private int grade;          // Grade level of the student.
    private String club;        // Name of the club the student is a member of.
    private int attendance;  // number of meetings of the student.
    private String status;      // Status of the student (e.g., "Active," "Inactive").
    private String dateJoin;    // Date when the student joined the club.
    @Column(name = "last_attendance")
    private String lastAttendance;

    public Students(int studentId, String name, int grade, String club, int attendance, String status, String dateJoin) {

        this.studentId = studentId;
        this.name = name;
        this.grade = grade;
        this.club = club;
        this.attendance = attendance;
        this.status = status;
        this.dateJoin = dateJoin;
        lastAttendance = "no attendance";
    }

    public Students() {

    }

    /**
     * Get the unique identifier of the student.
     *
     * @return The student's ID.
     */
    public int getStudentId() {
        return studentId;

    }

    /**
     * Set the unique identifier of the student.
     *
     * @param studentId The student's ID.
     */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /**
     * Get the name of the student.
     *
     * @return The student's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the student.
     *
     * @param name The student's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the grade level of the student.
     *
     * @return The grade level of the student.
     */
    public int getGrade() {
        return grade;
    }

    /**
     * Set the grade level of the student.
     *
     * @param grade The grade level of the student.
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }

    /**
     * Get the name of the club the student is a member of.
     *
     * @return The name of the club.
     */
    public String getClub() {
        return club;
    }

    /**
     * Set the name of the club the student is a member of.
     *
     * @param club The name of the club.
     */
    public void setClub(String club) {
        this.club = club;
    }

    /**
     * Get the attendance percentage of the student.
     *
     * @return The student's attendance percentage.
     */
    public int getAttendance() {
        return attendance;
    }

    /**
     * Set the attendance percentage of the student.
     *
     * @param attendance The student's attendance percentage.
     */
    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    /**
     * Get the status of the student.
     *
     * @return The student's status (e.g., "Active," "Inactive").
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status of the student.
     *
     * @param status The student's status (e.g., "Active," "Inactive").
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the date when the student joined the club.
     *
     * @return The date of joining.
     */
    public String getDateJoin() {
        return dateJoin;
    }

    /**
     * Set the date when the student joined the club.
     *
     * @param dateJoin The date of joining.
     */
    public void setDateJoin(String dateJoin) {
        this.dateJoin = dateJoin;
    }

    public String getLastAttendance() {
        return lastAttendance;
    }

    public void setLastAttendance(String lastAttendance) {
        this.lastAttendance = lastAttendance;
    }
}

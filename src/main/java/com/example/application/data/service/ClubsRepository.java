package com.example.application.data.service;/*
Author: Azarya Silaen
 */

import com.example.application.data.entity.Clubs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ClubsRepository extends JpaRepository<Clubs, Long>, JpaSpecificationExecutor<Clubs> {

    @Modifying
    @Query("update Clubs c set c.numStudents = :studentsCount where c.name = :clubName")
    void updateCountMembers(int studentsCount, String clubName);

    @Modifying
    @Query("update Clubs c set c.attendance = :attendance where c.name = :clubName")
    void updateAttendance(double attendance, String clubName);

    @Modifying
    @Query("update Clubs c set c.numMeeting = c.numMeeting+1 where c.name = :clubName")
    void updateNumMeetings(String clubName);

    @Modifying
    @Query("update Clubs c set c.lastMeeting = :lastMeeting where c.name = :clubName")
    void updateLastMeetings(String lastMeeting, String clubName);

    @Query("select c.name from Clubs c ")
    List<String> selectName();

    @Query("select c.numMeeting from Clubs c where c.name = :clubName")
    Integer selectSpecificNumMeeting(String clubName);

    @Query("select c.lastMeeting from Clubs c where c.name = :clubName")
    String selectLastMeeting(String clubName);

}

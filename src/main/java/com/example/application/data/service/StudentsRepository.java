package com.example.application.data.service;/*
Author: Azarya Silaen
 */

import com.example.application.data.entity.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface StudentsRepository extends JpaRepository<Students, Long>, JpaSpecificationExecutor<Students> {

    @Query("select s.name from Students s where s.studentId = :studentId")
    String selectNameByStudentId(int studentId);

    Students searchByStudentIdAndClub(int studentId, String clubName);
    void deleteByStudentIdAndClub(int studentID, String clubName);

    /*
    @Query("select s from Students s " + "where lower(s.club) like lower(concat('%',:searchTerm,'%' )) ")
    List<Students> search(@Param("searchTerm") String searchTerm);
     */

    @Query("select s from Students s " + "where s.club = :searchTerm")
    List<Students> search(@Param("searchTerm") String searchTerm);

    @Query("select sum(s.attendance) from Students s where s.club = :clubName")
    Integer selectAttendance(@Param("clubName") String clubName);

    @Query("select count (s.lastAttendance) from Students s where s.lastAttendance = :lastAttendance and s.club = :clubName")
    Integer countLastAttendance(String lastAttendance, String clubName);

    @Modifying
    @Query("update Students s set s.attendance = s.attendance+1 where s.studentId = :studentID and s.club = :clubName")
    void updateAttendance(int studentID, String clubName);

    @Query("select s.attendance from Students s where s.studentId = :studentID and s.club = :clubName")
    Integer selectSpecificAttendance(int studentID, String clubName);

    @Modifying
    @Query("update Students s set s.lastAttendance = :lastAttendance where s.studentId = :studentID and s.club = :clubName")
    void updateLastAttendance(String lastAttendance, int studentID, String clubName);

    @Query("select s.lastAttendance from Students s where s.studentId = :studentID and s.club = :clubName")
    String selectLastAttendance(int studentID, String clubName);

    @Modifying
    @Query("update Students s set s.status = :status where s.name = :memberName and s.club = :clubName")
    void updateStatus(@Param("status")String status, @Param("memberName")String memberName, @Param("clubName")String clubName);

    List<Students> findAll ();
}

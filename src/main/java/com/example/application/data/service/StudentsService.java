package com.example.application.data.service;

import com.example.application.data.entity.Students;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentsService {

    private final StudentsRepository repository;
    private final ClubsRepository clubRepository;

    public StudentsService(StudentsRepository repository, ClubsRepository clubRepository) {
        this.repository = repository;
        this.clubRepository = clubRepository;
    }

    public void save(Students students) {
        repository.save(students);
    }

    public Optional<Students> get(Long id) {
        return repository.findById(id);
    }

    public Students update(Students entity) {
        return repository.save(entity);
    }

    public void deleteByStudentIdAndClub(int studentID, String clubName) {
        repository.deleteByStudentIdAndClub(studentID, clubName);
    }

    public Page<Students> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Students> list(Pageable pageable, Specification<Students> filter) {
        return repository.findAll(filter, pageable);
    }

    public Students searchByStudentIdAndClub(int studentId, String clubName) {
        return repository.searchByStudentIdAndClub(studentId,clubName);
    }
    public int count() {
        return (int) repository.count();
    }

    public List<Students> findAll() { return repository.findAll();
    }

    public int countLastAttendance(String lastAttendance, String clubName) {
        Integer x = repository.countLastAttendance(lastAttendance, clubName);
        if (x == null) {
            return 0;
        }
        else {
            return repository.countLastAttendance(lastAttendance, clubName);
        }

    }

    public int selectSpecificNumMeeting(String clubName) {
        return clubRepository.selectSpecificNumMeeting(clubName);
    }

    public int selectSpecificAttendance(int studentID, String clubName) {
        return repository.selectSpecificAttendance(studentID, clubName);
    }

    public int selectAttendance(String clubName) {
        return repository.selectAttendance(clubName);
    }

    public String selectLastAttendance(int studentID, String clubName) {
        return repository.selectLastAttendance(studentID, clubName);
    }

    public String selectNameById(int studentId) {
        return repository.selectNameByStudentId(studentId);
    }

    public void updateStatus(String status, String memberName, String clubName) {
        repository.updateStatus(status, memberName, clubName);
    }

    public void updateAttendance(int id, String clubName) {
        repository.updateAttendance(id, clubName);
    }

    public void updateLastAttendance(String lastAttendance, int id, String clubName) {
        repository.updateLastAttendance(lastAttendance, id, clubName);
    }

    public List<Students> search(String searchTerm) {
        return repository.search(searchTerm);
    }
}

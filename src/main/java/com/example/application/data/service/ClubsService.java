package com.example.application.data.service;

import com.example.application.data.entity.Clubs;
import com.example.application.data.entity.Students;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClubsService {

    private final ClubsRepository repository;
    private final StudentsRepository studentsRepository;

    public ClubsService(ClubsRepository repository, StudentsRepository studentsRepository) {
        this.repository = repository;
        this.studentsRepository = studentsRepository;
    }

    public Optional<Clubs> get(Long id) {
        return repository.findById(id);
    }

    public Clubs update(Clubs entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Clubs> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Clubs> list(Pageable pageable, Specification<Clubs> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    public int countStudents(String club) {
        return studentsRepository.search(club).size();
    }
    public List<String> selectName() {
        return repository.selectName();
    }
    /**
     * Updates the count of students for each club.
     * Retrieves the list of club names and iterates through them.
     * For each club, counts the number of students and updates the count in the repository.
     */
    public void updateStudentsCount() {
        // Retrieve the list of club names
        List<String> listOfClubs = selectName();

        // Iterate through the list of clubs
        for (int i = 0; i < listOfClubs.size(); i++) {
            // Count the number of students for the current club
            int studentCount = countStudents(listOfClubs.get(i));
            // Update the student count for the current club in the repository
            repository.updateCountMembers(studentCount, listOfClubs.get(i));
        }
    }


    /**
     * Updates the average attendance rate records for each club.
     * Retrieves the list of club names and iterates through them.
     * For each club, checks if attendance records exist.
     * If attendance records exist, updates the attendance in the repository.
     */
    public void updateAttendance() {
        // Retrieve the list of club names
        List<String> listOfClubs = selectName();

        // Iterate through the list of clubs
        for (int i = 0; i < listOfClubs.size(); i++) {
            double sum = 0;
            // Check if attendance records exist for the current club
            if (studentsRepository.selectAttendance(listOfClubs.get(i)) != null) {
                // If attendance records exist, update the attendance in the repository
                repository.updateAttendance(studentsRepository.selectAttendance(listOfClubs.get(i)), listOfClubs.get(i));
            }
        }

        /*
        // Iterate through the list of clubs
        for (int i = 0; i < listOfClubs.size(); i++) {
            double sumAttendance = 0;
            //iterate through students list to find members
            for (int j = 0; j < studentsList.size(); j++) {
                if (studentsList.get(j).getClub().equals(listOfClubs.get(i))) {
                    //add total numbers of attendance that a single student has had to sumAttendance
                    sumAttendance+= studentsList.get(i).getAttendance();
                }
            }
            //database store total numbers of attendances that all members has done for the specific club
            //for visualization in the Master Club page,
            // this number will be divided by number of meetings times number of members
            //multiply by 100 to find the percentage
            repository.updateAttendance(sumAttendance, listOfClubs.get(i));
        }
         */
    }

    public void updateNumMeetings(String clubName) {
        repository.updateNumMeetings(clubName);
    }

    public int selectSpecificNumMeeting(String clubName) {
        return repository.selectSpecificNumMeeting(clubName);
    }

    public String selectLastMeeting(String clubName) {
        return repository.selectLastMeeting(clubName);
    }

    public void updateLastMeeting(String lastMeeting, String clubName) {
        repository.updateLastMeetings(lastMeeting, clubName);
    }


    public void updateAll() {
        // Update the count of students
        updateStudentsCount();
        // Update attendance records
        updateAttendance();
    }

    public List<Clubs> findAll() {
        return repository.findAll();
    }
}

package com.codingnomads.demo_data;

import com.codingnomads.demo_data.models.*;
import com.codingnomads.demo_data.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@AllArgsConstructor
public class Initializer {
    private final CEORepository ceoRepository;
    private final CoursesRepository coursesRepository;
    private final TracksRepository tracksRepository;
    private final StudentsRepository studentsRepository;
    private final CodingNomadsRepository codingNomadsRepository;


    @Transactional
    public void initialize() {
        if (!ceoRepository.findAll().isEmpty()) {
            return;
        }

        Course java101 = Course.builder()
                .name("Java 101")
                .build();
        coursesRepository.save(java101);

        Course java201 = Course.builder()
                .name("Java 201")
                .build();
        coursesRepository.save(java201);

        Course java301 = Course.builder()
                .name("Java 301")
                .build();
        coursesRepository.save(java301);

        Course spring = Course.builder()
                .name("Spring Framework")
                .build();
        coursesRepository.save(spring);

        Student alice = Student.builder()
                .name("Alice")
                .courses(Arrays.asList(java101, java201, java301, spring))
                .build();
        studentsRepository.save(alice);

        Student bob = Student.builder()
                .name("Bob")
                .courses(Arrays.asList(java201, spring))
                .build();
        studentsRepository.save(bob);

        Student eve = Student.builder()
                .name("Eve")
                .courses(Arrays.asList(java101, spring))
                .build();
        studentsRepository.save(eve);

        Student malory = Student.builder()
                .name("Malory")
                .courses(Arrays.asList(spring))
                .build();
        studentsRepository.save(malory);

        Track track = Track.builder()
                .name("Java Enterprise")
                .courses(Arrays.asList(java101, java201, java301, spring))
                .build();
        tracksRepository.save(track);

        CodingNomads codingNomads = CodingNomads.builder()
                .tracks(Arrays.asList(track))
                .students(Arrays.asList(alice, bob, eve, malory))
                .build();
        codingNomadsRepository.save(codingNomads);

        CEO ceo = CEO.builder()
                .name("Ryan")
                .codingNomads(codingNomads)
                .build();
        ceoRepository.save(ceo);

    }

}

package com.codingnomads.demo_data;

import com.codingnomads.demo_data.models.CEO;
import com.codingnomads.demo_data.models.CodingNomads;
import com.codingnomads.demo_data.models.Course;
import com.codingnomads.demo_data.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DemoDataStartupRunner implements CommandLineRunner {
    private final Initializer initializer;

    private final CodingNomadsRepository codingNomadsRepository;
    private final CEORepository ceoRepository;
    private final CoursesRepository coursesRepository;
    private final TracksRepository tracksRepository;
    private final StudentsRepository studentsRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initializer.initialize();

        CodingNomads codingNomads = codingNomadsRepository.findAll().get(0);
        System.out.println(codingNomads);

        CEO ceo = ceoRepository.findAll().get(0);
        System.out.println(ceo);


        System.out.println("---");
        tracksRepository.findAll().forEach(System.out::println);
        coursesRepository.findAll().forEach(System.out::println);
        studentsRepository.findAll().forEach(System.out::println);

        System.out.println("---");
        coursesRepository.findById(1L).ifPresent(System.out::println);

        System.out.println("---");
        System.out.println(studentsRepository.findByName("alice"));

        System.out.println("---");
        studentsRepository.findByCourses_Id(1L).forEach(System.out::println);
        System.out.println("---");
        studentsRepository.findByCourses_Id(2L).forEach(System.out::println);

        System.out.println("---");
        studentsRepository.findByNameContainingIgnoreCase("ice").forEach(System.out::println);

        System.out.println("---");
        Course spring = coursesRepository.findByName("Spring Framework");
        System.out.println(spring);
        studentsRepository.findByCoursesIsContaining(spring).forEach(System.out::println);
    }
}

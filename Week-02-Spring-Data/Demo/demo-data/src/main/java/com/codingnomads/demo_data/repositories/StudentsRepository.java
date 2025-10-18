package com.codingnomads.demo_data.repositories;

import com.codingnomads.demo_data.models.Course;
import com.codingnomads.demo_data.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentsRepository extends JpaRepository<Student, Long> {
    Student findByName(String firstName);
    List<Student> findByCourses_Id(Long id);
    List<Student> findByNameContainingIgnoreCase(String name);

    List<Student> findByCoursesIsContaining(Course course);
}

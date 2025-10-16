package com.codingnomads.demo_data.repositories;

import com.codingnomads.demo_data.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursesRepository extends JpaRepository<Course, Long> {
    Course findByName(String name);
}

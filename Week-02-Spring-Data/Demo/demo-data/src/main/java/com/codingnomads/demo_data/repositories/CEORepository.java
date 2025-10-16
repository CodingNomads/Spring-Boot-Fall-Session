package com.codingnomads.demo_data.repositories;

import com.codingnomads.demo_data.models.CEO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CEORepository extends JpaRepository<CEO, Long> {
}

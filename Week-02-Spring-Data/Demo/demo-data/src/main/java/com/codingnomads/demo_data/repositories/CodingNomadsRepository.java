package com.codingnomads.demo_data.repositories;

import com.codingnomads.demo_data.models.CodingNomads;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodingNomadsRepository extends JpaRepository<CodingNomads, Long> {
}

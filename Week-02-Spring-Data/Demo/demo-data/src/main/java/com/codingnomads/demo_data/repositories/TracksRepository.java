package com.codingnomads.demo_data.repositories;

import com.codingnomads.demo_data.models.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TracksRepository extends JpaRepository<Track, Long> {
}

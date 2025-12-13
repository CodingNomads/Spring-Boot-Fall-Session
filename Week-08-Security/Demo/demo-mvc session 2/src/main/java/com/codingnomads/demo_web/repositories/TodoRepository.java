package com.codingnomads.demo_web.repositories;

import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query(value = "SELECT * FROM todos WHERE todo_list_id IS NULL", nativeQuery = true)
    List<Todo> findAllWithoutList();

    List<Todo> findAllByUser(User user);
}

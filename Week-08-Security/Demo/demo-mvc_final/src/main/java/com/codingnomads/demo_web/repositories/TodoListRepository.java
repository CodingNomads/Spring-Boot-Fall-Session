package com.codingnomads.demo_web.repositories;

import com.codingnomads.demo_web.models.TodoList;
import com.codingnomads.demo_web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoListRepository extends JpaRepository<TodoList, Long> {
    List<TodoList> findAllByUser(User user);

    Optional<TodoList> findByIdAndUser_Id(Long id, Long userId);
}

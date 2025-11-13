package com.codingnomads.demo_mvc.todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    List<Todo> findAll();
    Todo save(Todo todo);
    Optional<Todo> findById(Long id);
    void deleteById(Long id);
    boolean existsByTitleIgnoreCase(String title);
}

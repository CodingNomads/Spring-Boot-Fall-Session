package com.codingnomads.demo_mvc.todo;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTodoRepository implements TodoRepository {

    private final Map<Long, Todo> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public InMemoryTodoRepository() {
        // Seed a few examples
        save(new Todo(null, "Learn Spring MVC", false));
        save(new Todo(null, "Build a Thymeleaf page", true));
    }

    @Override
    public List<Todo> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(seq.incrementAndGet());
        }
        store.put(todo.getId(), todo);
        return todo;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public boolean existsByTitleIgnoreCase(String title) {
        if (title == null) return false;
        String needle = title.trim().toLowerCase();
        return store.values().stream().anyMatch(t -> t.getTitle() != null && t.getTitle().trim().toLowerCase().equals(needle));
    }
}

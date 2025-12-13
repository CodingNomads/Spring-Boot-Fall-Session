package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.exceptions.TodoNotFoundException;
import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.repositories.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;

    public List<Todo> getAllTodos(Boolean done) {
        if (Objects.isNull(done)) {
            return todoRepository.findAll();
        }

        return todoRepository.findAll().stream().filter(todo -> todo.isDone() == done).toList();
    }

    public List<Todo> getTodosWithoutList() {
        return todoRepository.findAllWithoutList();
    }

    public Todo getTodoById(Long id) {
        return todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException("todo is not found"));
    }

    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long id, Todo newTodo) {
        Todo currentTodo = getTodoById(id);
        currentTodo.setText(newTodo.getText());
        currentTodo.setDone(newTodo.isDone());

        return todoRepository.save(currentTodo);
    }

    public Todo markTodoDone(Long id) {
        Todo todo = getTodoById(id);
        todo.setDone(true);

        return todoRepository.save(todo);
    }

    public Todo markTodoUndone(Long id) {
        Todo todo = getTodoById(id);
        todo.setDone(false);

        return todoRepository.save(todo);
    }

    public Todo deleteTodo(Long id) {
        Todo todo = getTodoById(id);
        todoRepository.deleteById(id);

        return todo;
    }
}

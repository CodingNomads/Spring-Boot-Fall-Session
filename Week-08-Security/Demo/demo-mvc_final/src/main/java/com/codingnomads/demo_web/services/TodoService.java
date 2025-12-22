package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.exceptions.TodoNotFoundException;
import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.TodoListRepository;
import com.codingnomads.demo_web.repositories.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service layer for managing Todo items.
 * The Service layer is where business logic lives. It sits between the Controllers
 * and the Repositories.
 */
@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodoListRepository todoListRepository;
    private final UserService userService;

    /**
     * Retrieves all todos for the currently logged-in user.
     * @param done optional filter to show only completed or incomplete todos.
     */
    public List<Todo> getAllTodos(Boolean done) {
        User user = userService.getCurrentUser();

        if (Objects.isNull(done)) {
            return todoRepository.findAllByUser(user);
        }

        return todoRepository.findAllByUser(user).stream().filter(todo -> todo.isDone() == done).toList();
    }

    public List<Todo> getTodosWithoutList() {
        User user = userService.getCurrentUser();
        return todoRepository.findAllByUserIdAndNoList(user.getId());
    }

    public Todo getTodoById(Long id) {
        User user = userService.getCurrentUser();
        return todoRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new TodoNotFoundException("todo is not found"));
    }

    public Todo createTodo(Todo todo) {
        User user = userService.getCurrentUser();
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long id, Todo newTodo) {
        Todo currentTodo = getTodoById(id);
        currentTodo.setText(newTodo.getText());
        currentTodo.setDone(newTodo.isDone());

        return todoRepository.save(currentTodo);
    }

    public Todo setDone(Long id, boolean done) {
        Todo todo = getTodoById(id);
        todo.setDone(done);
        return todoRepository.save(todo);
    }

    public Todo markTodoDone(Long id) { return setDone(id, true); }

    public Todo markTodoUndone(Long id) { return setDone(id, false); }

    public Todo deleteTodo(Long id) {
        Todo todo = getTodoById(id);
        todoRepository.deleteById(id);

        return todo;
    }

    public void toggleDone(Long id) {
        Todo t = getTodoById(id);
        setDone(id, !t.isDone());
    }

    public void assignToList(Long todoId, Long listIdOrNull) {
        User user = userService.getCurrentUser();
        // Ensure todo belongs to current user (will throw if not)
        getTodoById(todoId);

        if (listIdOrNull == null) {
            todoRepository.unassignFromList(todoId, user.getId());
            return;
        }

        // Validate target list belongs to current user
        todoListRepository.findByIdAndUser_Id(listIdOrNull, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("List not found"));

        todoRepository.assignToList(todoId, listIdOrNull, user.getId());
    }
}

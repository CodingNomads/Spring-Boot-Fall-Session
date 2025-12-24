package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.exceptions.TodoNotFoundException;
import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.TodoListRepository;
import com.codingnomads.demo_web.repositories.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.debug("Fetching all todos for user: {}, filter done: {}", user.getUsername(), done);

        if (Objects.isNull(done)) {
            return todoRepository.findAllByUser(user);
        }

        return todoRepository.findAllByUser(user).stream().filter(todo -> todo.isDone() == done).toList();
    }

    public List<Todo> getTodosWithoutList() {
        User user = userService.getCurrentUser();
        log.debug("Fetching todos without list for user: {}", user.getUsername());
        return todoRepository.findAllByUserIdAndNoList(user.getId());
    }

    public Todo getTodoById(Long id) {
        User user = userService.getCurrentUser();
        log.debug("Fetching todo ID: {} for user: {}", id, user.getUsername());
        return todoRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> {
                    log.error("Todo ID: {} not found for user: {}", id, user.getUsername());
                    return new TodoNotFoundException("todo is not found");
                });
    }

    public Todo createTodo(Todo todo) {
        User user = userService.getCurrentUser();
        log.info("Creating todo for user: {} with text: {}", user.getUsername(), todo.getText());
        todo.setUser(user);
        Todo savedTodo = todoRepository.save(todo);
        log.info("Successfully created todo ID: {}", savedTodo.getId());
        return savedTodo;
    }

    public Todo updateTodo(Long id, Todo newTodo) {
        log.info("Updating todo ID: {}", id);
        Todo currentTodo = getTodoById(id);
        currentTodo.setText(newTodo.getText());
        currentTodo.setDone(newTodo.isDone());

        Todo updatedTodo = todoRepository.save(currentTodo);
        log.info("Successfully updated todo ID: {}", id);
        return updatedTodo;
    }

    public Todo setDone(Long id, boolean done) {
        log.info("Setting done status to {} for todo ID: {}", done, id);
        Todo todo = getTodoById(id);
        todo.setDone(done);
        return todoRepository.save(todo);
    }

    public Todo markTodoDone(Long id) { return setDone(id, true); }

    public Todo markTodoUndone(Long id) { return setDone(id, false); }

    public Todo deleteTodo(Long id) {
        log.info("Deleting todo ID: {}", id);
        Todo todo = getTodoById(id);
        todoRepository.deleteById(id);
        log.info("Successfully deleted todo ID: {}", id);
        return todo;
    }

    public void toggleDone(Long id) {
        log.info("Toggling done status for todo ID: {}", id);
        Todo t = getTodoById(id);
        setDone(id, !t.isDone());
    }

    public void assignToList(Long todoId, Long listIdOrNull) {
        User user = userService.getCurrentUser();
        log.info("Assigning todo ID: {} to list ID: {} for user: {}", todoId, listIdOrNull, user.getUsername());
        // Ensure todo belongs to current user (will throw if not)
        getTodoById(todoId);

        if (listIdOrNull == null) {
            log.info("Unassigning todo ID: {} from list", todoId);
            todoRepository.unassignFromList(todoId, user.getId());
            return;
        }

        // Validate target list belongs to current user
        todoListRepository.findByIdAndUser_Id(listIdOrNull, user.getId())
                .orElseThrow(() -> {
                    log.error("List ID: {} not found for user: {}", listIdOrNull, user.getUsername());
                    return new IllegalArgumentException("List not found");
                });

        todoRepository.assignToList(todoId, listIdOrNull, user.getId());
        log.info("Successfully assigned todo ID: {} to list ID: {}", todoId, listIdOrNull);
    }
}

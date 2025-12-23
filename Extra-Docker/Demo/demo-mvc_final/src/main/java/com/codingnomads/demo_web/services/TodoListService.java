package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.exceptions.TodoListNotFoundException;
import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.TodoList;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.TodoListRepository;
import com.codingnomads.demo_web.repositories.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoListService {
    private final TodoListRepository todoListRepository;
    private final TodoRepository todoRepository;
    private final TodoService todoService;
    private final UserService userService;

    public List<TodoList> getAllTodoLists() {
        User user = userService.getCurrentUser();
        if (user == null) {
            log.error("Attempted to fetch all todo lists without authentication");
            throw new IllegalStateException("Unauthenticated");
        }
        log.debug("Fetching all todo lists for user: {}", user.getUsername());
        return todoListRepository.findAllByUser(user).stream().toList();
    }

    public TodoList getTodoListById(Long id) {
        User user = userService.getCurrentUser();
        log.debug("Fetching todo list by ID: {} for user: {}", id, user.getUsername());
        return todoListRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> {
                    log.error("Todo list with ID: {} not found for user: {}", id, user.getUsername());
                    return new TodoListNotFoundException("todo is not found");
                });
    }

    public TodoList createTodoList(TodoList todoList) {
        User user = userService.getCurrentUser();
        if (user == null) {
            log.error("Attempted to create todo list without authentication");
            throw new IllegalStateException("Unauthenticated");
        }
        log.info("Creating new todo list: {} for user: {}", todoList.getName(), user.getUsername());
        todoList.setUser(user);
        TodoList savedList = todoListRepository.save(todoList);
        log.info("Successfully created todo list with ID: {}", savedList.getId());
        return savedList;
    }

    public TodoList updateTodoList(Long id, TodoList newTodoList) {
        log.info("Updating todo list ID: {}", id);
        TodoList currentTodo = getTodoListById(id);
        currentTodo.setName(newTodoList.getName());

        TodoList updatedList = todoListRepository.save(currentTodo);
        log.info("Successfully updated todo list ID: {}", id);
        return updatedList;
    }

    public TodoList deleteTodoList(Long id) {
        User user = userService.getCurrentUser();
        log.info("Attempting to delete todo list ID: {} for user: {}", id, user.getUsername());
        TodoList list = getTodoListById(id);
        long count = todoRepository.countByUserIdAndListId(user.getId(), id);
        if (count > 0) {
            log.warn("Cannot delete todo list ID: {} because it contains {} todos", id, count);
            throw new IllegalStateException("Cannot delete a list that has todos");
        }
        todoListRepository.deleteById(id);
        log.info("Successfully deleted todo list ID: {}", id);
        return list;
    }

    // Assignment of todos to lists is consolidated in TodoService.assignToList
}

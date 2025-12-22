package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.exceptions.TodoListNotFoundException;
import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.TodoList;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.TodoListRepository;
import com.codingnomads.demo_web.repositories.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoListService {
    private final TodoListRepository todoListRepository;
    private final TodoRepository todoRepository;
    private final TodoService todoService;
    private final UserService userService;

    public List<TodoList> getAllTodoLists() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Unauthenticated");
        }
        return todoListRepository.findAllByUser(user).stream().toList();
    }

    public TodoList getTodoListById(Long id) {
        User user = userService.getCurrentUser();
        return todoListRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new TodoListNotFoundException("todo is not found"));
    }

    public TodoList createTodoList(TodoList todoList) {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Unauthenticated");
        }
        todoList.setUser(user);
        return todoListRepository.save(todoList);
    }

    public TodoList updateTodoList(Long id, TodoList newTodoList) {
        TodoList currentTodo = getTodoListById(id);
        currentTodo.setName(newTodoList.getName());

        return todoListRepository.save(currentTodo);
    }

    public TodoList deleteTodoList(Long id) {
        TodoList list = getTodoListById(id);
        Long userId = userService.getCurrentUser().getId();
        long count = todoRepository.countByUserIdAndListId(userId, id);
        if (count > 0) {
            throw new IllegalStateException("Cannot delete a list that has todos");
        }
        todoListRepository.deleteById(id);
        return list;
    }

    // Assignment of todos to lists is consolidated in TodoService.assignToList
}

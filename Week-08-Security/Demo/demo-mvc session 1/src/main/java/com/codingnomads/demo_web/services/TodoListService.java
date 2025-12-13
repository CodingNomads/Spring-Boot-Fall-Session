package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.exceptions.TodoListNotFoundException;
import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.TodoList;
import com.codingnomads.demo_web.repositories.TodoListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoListService {
    private final TodoListRepository todoListRepository;
    private final TodoService todoService;

    public List<TodoList> getAllTodoLists() {
        return todoListRepository.findAll().stream().toList();
    }

    public TodoList getTodoListById(Long id) {
        return todoListRepository.findById(id).orElseThrow(() -> new TodoListNotFoundException("todo is not found"));
    }

    public TodoList createTodoList(TodoList todoList) {
        return todoListRepository.save(todoList);
    }

    public TodoList updateTodoList(Long id, TodoList newTodoList) {
        TodoList currentTodo = getTodoListById(id);
        currentTodo.setName(newTodoList.getName());

        return todoListRepository.save(currentTodo);
    }

    public TodoList deleteTodoList(Long id) {
        TodoList currentTodo = getTodoListById(id);
        todoListRepository.deleteById(id);

        return currentTodo;
    }

    public TodoList addTodo(Long id, Long todoId) {
        TodoList list = getTodoListById(id);
        Todo todo = todoService.getTodoById(todoId);

        list.getTodos().add(todo);

        return todoListRepository.save(list);
    }
}

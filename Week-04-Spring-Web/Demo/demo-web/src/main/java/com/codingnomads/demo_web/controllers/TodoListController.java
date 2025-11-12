package com.codingnomads.demo_web.controllers;

import com.codingnomads.demo_web.models.TodoList;
import com.codingnomads.demo_web.services.TodoListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class TodoListController {
    private final TodoListService todoListService;

    @GetMapping("")
    public List<TodoList> index() {
        return todoListService.getAllTodoLists();
    }

    @GetMapping("/{id}")
    public TodoList show(@PathVariable Long id) {
        return todoListService.getTodoListById(id);
    }

    @PostMapping(value = "")
    public TodoList create(@RequestBody TodoList todoList) {
        return todoListService.createTodoList(todoList);
    }

    @PutMapping("/{id}")
    public TodoList update(@PathVariable Long id, @RequestBody TodoList newTodoList) {
        return todoListService.updateTodoList(id, newTodoList);
    }

    @DeleteMapping("/{id}")
    public TodoList delete(@PathVariable Long id) {
        return todoListService.deleteTodoList(id);
    }

    @PostMapping("/{id}/todos/{taskId}")
    public TodoList addTaskToList(@PathVariable Long id, @PathVariable Long taskId) {
        return todoListService.addTodo(id, taskId);
    }

}

package com.codingnomads.demo_web.controllers.api;

import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.services.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Todos via an API.
 * This controller handles requests that return data (JSON) rather than HTML views.
 * It's mapped to "/api/todos".
 */
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    /**
     * GET /api/todos - Retrieve all todos.
     * Optional 'done' parameter allows filtering by completion status.
     */
    @GetMapping("")
    public List<Todo> index(@RequestParam(required = false, name = "done") Boolean done) {
        return todoService.getAllTodos(done);
    }

    /**
     * GET /api/todos with header X-Param=Count - Retrieve the total count of todos.
     * This demonstrates how to use request headers for routing/logic.
     */
    @RequestMapping(path = "", headers = "X-Param=Count", method = RequestMethod.GET)
    public int count() {
        return todoService.getAllTodos(null).size();
    }

    /**
     * GET /api/todos/{id} - Retrieve a specific todo by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Todo> show(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    /**
     * POST /api/todos - Create a new todo.
     * The @RequestBody annotation tells Spring to deserialize the JSON body into a Todo object.
     */
    @PostMapping(value = "")
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@RequestBody Todo todo) {
        return todoService.createTodo(todo);
    }

    /**
     * PUT /api/todos/{id} - Update an existing todo.
     */
    @PutMapping("/{id}")
    public Todo update(@PathVariable Long id, @RequestBody Todo todo) {
        return todoService.updateTodo(id, todo);
    }

    /**
     * PATCH /api/todos/{id}/done - Mark a todo as completed.
     */
    @PatchMapping("/{id}/done")
    public Todo markDone(@PathVariable Long id) {
        return todoService.setDone(id, true);
    }

    /**
     * PATCH /api/todos/{id}/undone - Mark a todo as incomplete.
     */
    @PatchMapping("/{id}/undone")
    public Todo markUndone(@PathVariable Long id) {
        return todoService.setDone(id, false);
    }

    /**
     * DELETE /api/todos/{id} - Remove a todo.
     */
    @DeleteMapping("/{id}")
    public Todo delete(@PathVariable Long id) {
        return todoService.deleteTodo(id);
    }

}

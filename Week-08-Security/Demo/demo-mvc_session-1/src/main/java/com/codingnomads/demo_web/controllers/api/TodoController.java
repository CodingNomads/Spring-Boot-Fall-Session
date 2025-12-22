package com.codingnomads.demo_web.controllers.api;

import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.services.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @GetMapping("")
    public List<Todo> index(@RequestParam(required = false, name = "done") Boolean done) {
        return todoService.getAllTodos(done);
    }

    @RequestMapping(path = "", headers = "X-Param=Count", method = RequestMethod.GET)
    public int count() {
        return todoService.getAllTodos(null).size();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> show(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @PostMapping(value = "")
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@RequestBody Todo todo) {
        return todoService.createTodo(todo);
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable Long id, @RequestBody Todo todo) {
        return todoService.updateTodo(id, todo);
    }

    @PatchMapping("/{id}/done")
    public Todo markDone(@PathVariable Long id) {
        return todoService.markTodoDone(id);
    }

    @PatchMapping("/{id}/undone")
    public Todo markUndone(@PathVariable Long id) {
        return todoService.markTodoUndone(id);
    }

    @DeleteMapping("/{id}")
    public Todo delete(@PathVariable Long id) {
        return todoService.deleteTodo(id);
    }

}

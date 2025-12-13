package com.codingnomads.demo_web.controllers.pages;

import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.services.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodosPageController {

    private final TodoService todoService;

    @GetMapping
    public String todosPage(Model model) {
        List<Todo> todos = todoService.getAllTodos(null);
        model.addAttribute("todos", todos);
        return "todos";
    }

    @GetMapping("/new")
    public String newTodoForm(Model model) {
        model.addAttribute("todo", new Todo());
        return "todo_new";
    }

    @PostMapping
    public String createTodo(@ModelAttribute("todo") Todo todo) {
        todoService.createTodo(todo);
        return "redirect:/todos";
    }
}

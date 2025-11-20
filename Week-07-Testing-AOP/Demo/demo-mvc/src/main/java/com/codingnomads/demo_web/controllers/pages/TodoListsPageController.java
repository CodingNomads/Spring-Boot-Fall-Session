package com.codingnomads.demo_web.controllers.pages;

import com.codingnomads.demo_web.models.TodoList;
import com.codingnomads.demo_web.services.TodoListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/todo-lists")
@RequiredArgsConstructor
public class TodoListsPageController {

    private final TodoListService todoListService;

    @GetMapping
    public String listTodoLists(Model model) {
        List<TodoList> todoLists = todoListService.getAllTodoLists();
        model.addAttribute("todoLists", todoLists);
        return "todo_lists";
    }

    @GetMapping("/new")
    public String newTodoListForm(Model model) {
        model.addAttribute("todoList", new TodoList());
        return "todo_list_new";
    }

    @PostMapping
    public String createTodoList(@ModelAttribute("todoList") TodoList todoList) {
        todoList.setTodos(null); // ensure simple creation with only name
        todoListService.createTodoList(todoList);
        return "redirect:/todos";
    }
}
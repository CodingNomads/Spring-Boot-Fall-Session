package com.codingnomads.demo_web.controllers.pages;

import com.codingnomads.demo_web.logging.Logged;
import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.TodoList;
import com.codingnomads.demo_web.services.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * MVC Controller for the Todo web interface.
 * This controller returns Thymeleaf template names (Strings) which are then rendered into HTML.
 */
@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
@Logged
public class TodosPageController {

    private final TodoService todoService;
    private final com.codingnomads.demo_web.services.TodoListService todoListService;

    /**
     * GET /todos - Displays the main todo page.
     * It gathers all necessary data (todo lists, todos, unlisted todos) and adds them to the 'Model'
     * so they can be accessed by the Thymeleaf template.
     */
    @GetMapping
    public String todosPage(@RequestParam(value = "filter", required = false, defaultValue = "all") String filter,
                            Model model) {
        Boolean done = switch (filter.toLowerCase()) {
            case "open", "undone" -> Boolean.FALSE;
            case "done" -> Boolean.TRUE;
            default -> null;
        };
        model.addAttribute("filter", filter.toLowerCase());
        List<TodoList> lists = todoListService.getAllTodoLists();
        model.addAttribute("todoLists", lists);

        // Build a per-list map of filtered todos (by "done" if provided)
        java.util.Map<Long, java.util.List<Todo>> listTodosByListId = new java.util.HashMap<>();
        for (var list : lists) {
            java.util.List<Todo> lt = list.getTodos() == null ? java.util.List.of() : list.getTodos();
            if (done != null) {
                lt = lt.stream().filter(t -> t != null && t.isDone() == done).toList();
            }
            listTodosByListId.put(list.getId(), lt);
        }
        model.addAttribute("listTodosByListId", listTodosByListId);

        // Unlisted todos, apply same filter
        List<Todo> unlisted = todoService.getTodosWithoutList();
        if (done != null) {
            unlisted = unlisted.stream().filter(t -> t != null && t.isDone() == done).toList();
        }
        model.addAttribute("unlistedTodos", unlisted);

        // Map current list selection per todo for pre-selecting the dropdown
        java.util.Map<Long, Long> todoListByTodoId = new java.util.HashMap<>();
        for (var list : lists) {
            if (list.getTodos() == null) continue;
            for (var t : list.getTodos()) {
                if (t != null && t.getId() != null) {
                    todoListByTodoId.put(t.getId(), list.getId());
                }
            }
        }
        model.addAttribute("todoListByTodoId", todoListByTodoId);

        // Returns the name of the template file: src/main/resources/templates/todos.html
        return "todos";
    }

    /**
     * GET /todos/new - Displays the form to create a new todo.
     */
    @GetMapping("/new")
    public String newTodoForm(Model model) {
        model.addAttribute("todo", new Todo());
        return "todo_new";
    }

    /**
     * POST /todos - Processes the creation of a new todo.
     * Redirects back to the main list after saving.
     */
    @PostMapping
    public String createTodo(@ModelAttribute("todo") Todo todo) {
        todoService.createTodo(todo);
        return "redirect:/todos";
    }

    @PostMapping("/{id}/toggle")
    public String toggleDone(@PathVariable Long id,
                             @RequestParam(value = "filter", required = false, defaultValue = "all") String filter) {
        todoService.toggleDone(id);
        return "redirect:/todos?filter=" + filter;
    }

    @PostMapping("/{id}/assign")
    public String assignList(@PathVariable Long id,
                             @RequestParam(value = "listId", required = false) Long listId,
                             @RequestParam(value = "filter", required = false, defaultValue = "all") String filter) {
        todoService.assignToList(id, listId);
        return "redirect:/todos?filter=" + filter;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(value = "filter", required = false, defaultValue = "all") String filter) {
        todoService.deleteTodo(id);
        return "redirect:/todos?filter=" + filter;
    }

    // ------- Todo List controls integrated into the same page controller -------

    @GetMapping("/lists/new")
    public String newTodoListForm(Model model) {
        model.addAttribute("todoList", new TodoList());
        return "todo_list_new";
    }

    @PostMapping("/lists")
    public String createTodoList(@ModelAttribute("todoList") TodoList todoList) {
        // ensure simple creation with only name
        todoList.setTodos(null);
        todoListService.createTodoList(todoList);
        return "redirect:/todos";
    }

    @PostMapping("/lists/{id}/delete")
    public String deleteTodoList(@PathVariable Long id,
                                 @RequestParam(value = "filter", required = false, defaultValue = "all") String filter,
                                 RedirectAttributes ra) {
        try {
            todoListService.deleteTodoList(id);
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/todos?filter=" + filter;
    }
}

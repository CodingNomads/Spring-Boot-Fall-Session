package com.codingnomads.demo_mvc.todo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

@Controller
@RequestMapping("/todos")
@SessionAttributes("draftTodo")
public class WebTodoController {

    private final TodoRepository repo;

    public WebTodoController(TodoRepository repo) {
        this.repo = repo;
    }

    // Example of @ModelAttribute method to pre-populate common data
    @ModelAttribute("pageTitle")
    public String pageTitle() {
        return "Todo List";
    }

    // Example of @ModelAttribute used with @SessionAttributes for a short multi-step flow
    @ModelAttribute("draftTodo")
    public Todo initDraft() {
        return new Todo();
    }

    @GetMapping
    public String list(Model model) {
        List<Todo> todos = repo.findAll();
        model.addAttribute("todos", todos);
        return "todos/list";
    }

    // Simple single-step create (existing example)
    @GetMapping("/new")
    public String showCreateForm(@ModelAttribute("draftTodo") Todo draftTodo, Model model) {
        if (draftTodo.getTitle() == null) {
            draftTodo.setTitle("");
        }
        model.addAttribute("todo", draftTodo);
        return "todos/create";
    }

    @PostMapping
    public String create(@ModelAttribute("todo") Todo todo, SessionStatus status) {
        repo.save(new Todo(null, todo.getTitle(), false));
        status.setComplete();
        return "redirect:/todos";
    }

    // ===== Wizard (multi-step) create flow =====
    @GetMapping("/wizard/new")
    public String wizardStep1(@ModelAttribute("draftTodo") Todo draftTodo, Model model) {
        model.addAttribute("todo", draftTodo);
        return "todos/wizard/step1";
    }

    // Save step1 entries in the session-scoped draft and go to step2 (review/duplicate check)
    @PostMapping("/wizard/step1")
    public String processStep1(@ModelAttribute("todo") Todo formTodo, @ModelAttribute("draftTodo") Todo draftTodo) {
        draftTodo.setTitle(formTodo.getTitle());
        draftTodo.setCompleted(formTodo.isCompleted());
        return "redirect:/todos/wizard/step2";
    }

    @GetMapping("/wizard/step2")
    public String wizardStep2(@ModelAttribute("draftTodo") Todo draftTodo, Model model) {
        boolean duplicate = draftTodo.getTitle() != null && repo.existsByTitleIgnoreCase(draftTodo.getTitle());
        model.addAttribute("todo", draftTodo);
        model.addAttribute("duplicate", duplicate);
        return "todos/wizard/step2";
    }

    // Go back to step1 to edit
    @PostMapping("/wizard/back")
    public String wizardBack() {
        return "redirect:/todos/wizard/new";
    }

    // Cancel the wizard and clear session attribute
    @PostMapping("/wizard/cancel")
    public String wizardCancel(SessionStatus status) {
        status.setComplete();
        return "redirect:/todos";
    }

    // Finish: save the draft and show result page (step3)
    @PostMapping("/wizard/finish")
    public String wizardFinish(@ModelAttribute("draftTodo") Todo draftTodo, SessionStatus status) {
        Todo saved = repo.save(new Todo(null, draftTodo.getTitle(), draftTodo.isCompleted()));
        status.setComplete();
        return "redirect:/todos/wizard/done?id=" + saved.getId();
    }

    @GetMapping("/wizard/done")
    public String wizardDone(@RequestParam("id") Long id, Model model) {
        Todo saved = repo.findById(id).orElse(null);
        model.addAttribute("saved", saved);
        return "todos/wizard/done";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        repo.findById(id).ifPresent(t -> {
            t.setCompleted(!t.isCompleted());
            repo.save(t);
        });
        return "redirect:/todos";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/todos";
    }
}

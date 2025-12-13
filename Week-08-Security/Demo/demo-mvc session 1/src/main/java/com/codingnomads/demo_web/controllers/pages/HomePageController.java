package com.codingnomads.demo_web.controllers.pages;

import com.codingnomads.demo_web.clients.WeatherClient;
import com.codingnomads.demo_web.clients.dtos.WeatherResponse;
import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.TodoList;
import com.codingnomads.demo_web.services.TodoListService;
import com.codingnomads.demo_web.services.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final TodoListService todoListService;
    private final TodoService todoService;

    private final WeatherClient weatherClient;

    @GetMapping("/")
    public String index(Model model) {
        WeatherResponse weather = weatherClient.getWeather(32, 81);
        model.addAttribute("periods", weather.getProperties().getPeriods());

        List<TodoList> todoLists = todoListService.getAllTodoLists();
        List<Todo> unlistedTodos = todoService.getTodosWithoutList();

        model.addAttribute("todoLists", todoLists);
        model.addAttribute("unlistedTodos", unlistedTodos);


        return "index";
    }
}

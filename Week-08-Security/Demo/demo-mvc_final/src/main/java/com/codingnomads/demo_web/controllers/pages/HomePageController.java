package com.codingnomads.demo_web.controllers.pages;

import com.codingnomads.demo_web.clients.WeatherClient;
import com.codingnomads.demo_web.clients.dtos.Period;
import com.codingnomads.demo_web.clients.dtos.WeatherResponse;
import com.codingnomads.demo_web.logging.Logged;
import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.TodoList;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.TodoListRepository;
import com.codingnomads.demo_web.repositories.TodoRepository;
import com.codingnomads.demo_web.repositories.UserRepository;
import com.codingnomads.demo_web.services.TodoListService;
import com.codingnomads.demo_web.services.TodoService;
import com.codingnomads.demo_web.services.UserService;
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
    private final UserService userService;

    private final WeatherClient weatherClient;

    // For statistics when user is anonymous
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final TodoListRepository todoListRepository;

    @GetMapping("/")
    @Logged
    public String index(Model model, @org.springframework.web.bind.annotation.RequestParam(name = "unit", required = false) String unit) {
        try {
            WeatherResponse weather = weatherClient.getWeather(32, 81);
            List<Period> periods = weather.getProperties().getPeriods();
            if (periods != null && !periods.isEmpty()) {
                Double tempF = periods.get(0).getTemperature();
                if (tempF != null) {
                    // Default unit: F, allow toggle to C via query param
                    boolean useC = "C".equalsIgnoreCase(unit);
                    String label = "Temperature";
                    String value;
                    if (useC) {
                        double c = (tempF - 32.0) * 5.0 / 9.0;
                        value = String.format("%.0f °C", c);
                        model.addAttribute("weatherUnit", "C");
                    } else {
                        value = String.format("%.0f °F", tempF);
                        model.addAttribute("weatherUnit", "F");
                    }
                    model.addAttribute("weatherPresent", true);
                    model.addAttribute("weatherLabel", label);
                    model.addAttribute("weatherValue", value);
                }
            }
        } catch (Exception ignored) {
            // Fault tolerant: no weather attributes set on error
        }

        User current = userService.getCurrentUser();
        if (current == null) {
            // Anonymous: show system statistics
            model.addAttribute("usersCount", userRepository.count());
            model.addAttribute("todosCount", todoRepository.count());
            model.addAttribute("listsCount", todoListRepository.count());
            model.addAttribute("anonymous", true);
        } else {
            // Authenticated: show personal data
            List<TodoList> todoLists = todoListService.getAllTodoLists();
            List<Todo> unlistedTodos = todoService.getTodosWithoutList();

            model.addAttribute("todoLists", todoLists);
            model.addAttribute("unlistedTodos", unlistedTodos);
            model.addAttribute("anonymous", false);
        }

        return "index";
    }
}

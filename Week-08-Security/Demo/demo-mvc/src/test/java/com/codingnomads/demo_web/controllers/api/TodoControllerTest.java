package com.codingnomads.demo_web.controllers.api;

import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.services.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @MockitoBean
    TodoService todoService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private TodoController todoController;


    List<Todo> todos = List.of(
            Todo.builder().id(1L).done(false).build(),
            Todo.builder().id(2L).done(true).build()
    );

    @Test
    void gatAll_success() throws Exception {
        // Given
        when(todoService.getAllTodos(null)).thenReturn(todos);

        // When
        mockMvc.perform(get("/api/todos")).
                // Then
                andExpect(status().isOk()).
                andExpect(jsonPath("$[0].id").value(1)).
                andExpect(jsonPath("$[0].done").value(false));

        verify(todoService, times(1)).getAllTodos(null);
    }

    @Test
    void count_success() throws Exception {
        // Given
        when(todoService.getAllTodos(null)).thenReturn(todos);

        // When
        mockMvc.perform(get("/api/todos").header("X-Param", "Count")).
                // Then
                        andExpect(status().isOk()).
                andExpect(jsonPath("$").isNumber()).
                andExpect(jsonPath("$").value(2));

        verify(todoService, times(1)).getAllTodos(null);
    }
}

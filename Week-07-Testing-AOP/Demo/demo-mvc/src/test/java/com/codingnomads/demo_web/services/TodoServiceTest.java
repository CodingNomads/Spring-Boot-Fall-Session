package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.repositories.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock // creates shell of the object
    //    @Spy // uses real object for all calls that were not mocked
    TodoRepository todoRepository;

    @InjectMocks
    TodoService todoService;

    List<Todo> todos = List.of(
            Todo.builder().id(1L).done(false).build(),
            Todo.builder().id(2L).done(true).build()
    );

    @Test
    void getAllTodos_null() {
        // Given
        when(todoRepository.findAll()).thenReturn(todos).thenReturn(null).thenThrow();

        // When
        List<Todo> result = todoService.getAllTodos(null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void getAllTodos_true() {
        when(todoRepository.findAll()).thenReturn(todos);

        List<Todo> result = todoService.getAllTodos(true);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void getAllTodos_false() {
       when(todoRepository.findAll()).thenReturn(todos);

        List<Todo> result = todoService.getAllTodos(false);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void getTodosWithoutList_exception() {
        when(todoRepository.findAllWithoutList()).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> todoService.getTodosWithoutList());

        verify(todoRepository, times(1)).findAllWithoutList();
    }
}
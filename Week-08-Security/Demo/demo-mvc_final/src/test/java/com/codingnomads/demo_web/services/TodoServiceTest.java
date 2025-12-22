package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.User;
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

    @Mock // creates a shell of the object
    TodoRepository todoRepository;

    @Mock
    UserService userService;

    @InjectMocks
    TodoService todoService;

    List<Todo> todos = List.of(
            Todo.builder().id(1L).done(false).build(),
            Todo.builder().id(2L).done(true).build()
    );

    @Test
    void getAllTodos_null() {
        // Given
        User user = User.builder().id(1L).username("u").build();
        when(userService.getCurrentUser()).thenReturn(user);
        when(todoRepository.findAllByUser(user)).thenReturn(todos);

        // When
        List<Todo> result = todoService.getAllTodos(null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(todoRepository, times(1)).findAllByUser(user);
    }

    @Test
    void getAllTodos_true() {
        User user = User.builder().id(1L).username("u").build();
        when(userService.getCurrentUser()).thenReturn(user);
        when(todoRepository.findAllByUser(user)).thenReturn(todos);

        List<Todo> result = todoService.getAllTodos(true);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(todoRepository, times(1)).findAllByUser(user);
    }

    @Test
    void getAllTodos_false() {
        User user = User.builder().id(1L).username("u").build();
        when(userService.getCurrentUser()).thenReturn(user);
        when(todoRepository.findAllByUser(user)).thenReturn(todos);

        List<Todo> result = todoService.getAllTodos(false);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(todoRepository, times(1)).findAllByUser(user);
    }

    @Test
    void getTodosWithoutList_exception() {
        User user = User.builder().id(42L).username("u").build();
        when(userService.getCurrentUser()).thenReturn(user);
        when(todoRepository.findAllByUserIdAndNoList(user.getId())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> todoService.getTodosWithoutList());

        verify(todoRepository, times(1)).findAllByUserIdAndNoList(user.getId());
    }
}
package com.codingnomads.demo_web.repositories;

import com.codingnomads.demo_web.models.Todo;
import com.codingnomads.demo_web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Todo entities.
 * By extending JpaRepository, we get standard CRUD operations (save, delete, findById, etc.) for free!
 * Spring Data JPA automatically implements this interface at runtime.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    /**
     * Derived query method: Spring parses the method name to generate the SQL.
     * It finds all todos where the 'user' matches the provided User object.
     */
    List<Todo> findAllByUser(User user);

    // Entity Todo doesn't have a `todoList` property; use native query scoped by user
    @Query(value = "SELECT * FROM todos WHERE user_id = :userId AND todo_list_id IS NULL", nativeQuery = true)
    List<Todo> findAllByUserIdAndNoList(Long userId);

    Optional<Todo> findByIdAndUser_Id(Long id, Long userId);

    // Assign or unassign a todo to a list (ownership enforced via user_id)
    @Modifying
    @Transactional
    @Query(value = "UPDATE todos SET todo_list_id = :listId WHERE id = :todoId AND user_id = :userId", nativeQuery = true)
    int assignToList(@Param("todoId") Long todoId, @Param("listId") Long listId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE todos SET todo_list_id = NULL WHERE id = :todoId AND user_id = :userId", nativeQuery = true)
    int unassignFromList(@Param("todoId") Long todoId, @Param("userId") Long userId);

    // Count todos by list for current user (used to block list deletion)
    @Query(value = "SELECT COUNT(*) FROM todos WHERE user_id = :userId AND todo_list_id = :listId", nativeQuery = true)
    long countByUserIdAndListId(@Param("userId") Long userId, @Param("listId") Long listId);
}

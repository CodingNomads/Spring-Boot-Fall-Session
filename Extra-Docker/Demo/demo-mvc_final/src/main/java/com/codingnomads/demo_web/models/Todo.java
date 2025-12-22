package com.codingnomads.demo_web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a Todo item in the database.
 * We use Lombok annotations (@Data, @AllArgsConstructor, etc.) to reduce boilerplate code.
 * The @Entity annotation tells JPA that this class should be mapped to a database table.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "todos")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private boolean done;

    /**
     * Many todos can belong to one user.
     * @JsonIgnore prevents the user data from being included in API responses to avoid infinite recursion.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}

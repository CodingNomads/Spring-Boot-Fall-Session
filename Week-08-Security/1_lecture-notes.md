# Week 8 — Spring Security: Authentication, Authorization & JWT/Session Login

Today we secure our Recipe API with **Spring Security**. We’ll learn about authentication vs. authorization, configure in-memory users, protect endpoints with roles, and introduce JWT-based authentication.

---

## 1) Core Concepts

### Authentication vs. Authorization
- **Authentication**: Verifying *who* the user is (login).  
- **Authorization**: Determining *what* the user is allowed to do (roles/permissions).

### Spring Security Filters
Spring Security adds a filter chain to intercept requests and enforce security rules.

### Common Security Annotations
- `@EnableWebSecurity` — enables Spring Security configuration.  
- `@Bean SecurityFilterChain` — defines security rules.  
- `@PreAuthorize` / `@Secured` — method-level security.  
- `PasswordEncoder` — securely hashes passwords (e.g., BCrypt).

### Authentication Strategies
1. **In-memory** users (for demos).  
2. **Database-backed** users with `UserDetailsService`.  
3. **JWT-based authentication** for stateless REST APIs.  
4. **OAUTH2 authentication** for strongest security.

---

## 2) Project Setup

`build.gradle`
```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
}
```

---

## 3) Working Code Samples

### 3.1 Security Configuration

`src/main/java/com/codingnomads/bootcamp/recipeapi/config/SecurityConfig.java`
```java
package com.codingnomads.bootcamp.recipeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Configures Spring Security for the application
@Configuration
public class SecurityConfig {

    // Defines the security filter chain and endpoint access rules
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disables CSRF protection for simplicity (enable in production)
            .csrf(csrf -> csrf.disable())
            // Configures authorization rules for endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll() // Public endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin-only endpoints
                .anyRequest().authenticated() // All other endpoints require authentication
            )
            // Enables HTTP Basic authentication
            .httpBasic();
        return http.build();
    }

    // Provides a password encoder bean (BCrypt for secure password hashing)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 3.2 In-Memory User Config

`src/main/java/com/codingnomads/bootcamp/recipeapi/config/UserConfig.java`
```java
package com.codingnomads.bootcamp.recipeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

// Configures in-memory users for authentication (demo purposes)
@Configuration
public class UserConfig {

    // Defines two users: "user" (USER role) and "admin" (ADMIN role)
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("{noop}password") // {noop} means no encoding (for demo only)
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();

        // Stores users in memory
        return new InMemoryUserDetailsManager(user, admin);
    }
}
```

### 3.3 Securing Endpoints

`src/main/java/com/codingnomads/bootcamp/recipeapi/controllers/SecureController.java`
```java
package com.codingnomads.bootcamp.recipeapi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// REST controller exposing endpoints with different access levels
@RestController
public class SecureController {

    // Public endpoint, accessible without authentication
    @GetMapping("/api/public/hello")
    public String publicHello() {
        return "Hello from a public endpoint!";
    }

    // Endpoint accessible to authenticated users with USER role
    @GetMapping("/api/user/hello")
    public String userHello() {
        return "Hello User!";
    }

    // Endpoint accessible to authenticated users with ADMIN role
    @GetMapping("/api/admin/hello")
    public String adminHello() {
        return "Hello Admin!";
    }
}
```

### 3.4 JWT Authentication Example (Simplified)

`src/main/java/com/codingnomads/bootcamp/recipeapi/security/JwtUtil.java`
```java
package com.codingnomads.bootcamp.recipeapi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

// Utility class for generating and validating JWT tokens
public class JwtUtil {
    // Secret key for signing JWTs (should be stored securely in production)
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Generates a JWT token for the given username, expires in 1 hour
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(key)
                .compact();
    }

    // Validates a JWT token and returns the username (subject)
    public static String validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
```

---

## 4) Testing Security

#### Public endpoint
```bash
curl http://localhost:8080/api/public/hello
```
✅ Works without authentication.

#### User endpoint
```bash
curl -u user:password http://localhost:8080/api/user/hello
```
✅ Requires user credentials.

#### Admin endpoint
```bash
curl -u admin:admin http://localhost:8080/api/admin/hello
```
✅ Requires admin role.

---

## 5) Best Practices
- Always store passwords hashed (BCrypt).  
- Use HTTPS in production.  
- JWTs are ideal for stateless REST APIs.  
- Don’t put sensitive data in JWT payload.  

---

## 6) Next Steps
Next week, we’ll deploy our secured app to **AWS**, configuring environment variables and scaling our Recipe API.

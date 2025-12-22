# Week 8 — Spring Security: Authentication, Authorization & JWT/Session Login

Today we secure our Recipe API with **Spring Security**. We’ll learn about authentication vs. authorization, configure
in-memory users, protect endpoints with roles, and introduce JWT-based authentication.

---

## 1) Core Concepts

### Authentication vs. Authorization

- **Authentication**: Verifying *who* the user is (login).
- **Authorization**: Determining *what* the user is allowed to do (roles/permissions).

### Spring Security Filters

An ordered servlet filter chain inspects every HTTP request before your controllers.

- Purpose: authenticate the request, authorize access, translate security errors (401/403).
- Order: many filters (often `OncePerRequestFilter`) run in a fixed order.
- Flow: restore `SecurityContext` → authenticate (form/JWT/etc.) → authorize (`FilterSecurityInterceptor`).
- Configure rules via a `SecurityFilterChain` bean (`HttpSecurity`).
- Custom filters: `http.addFilterBefore/After(...)` relative to built‑ins.
- Current user: `SecurityContextHolder.getContext().getAuthentication()`.

### Common Security Annotations

- `@EnableMethodSecurity`: turn on method security (`@PreAuthorize`, `@PostAuthorize`, `@Secured`, `@RolesAllowed`).
- `@Bean SecurityFilterChain`: define HTTP rules with `HttpSecurity`; return `http.build()`.
  ```java
  @Bean
  SecurityFilterChain security(HttpSecurity http) throws Exception {
      http
          .authorizeHttpRequests(a -> a
              .requestMatchers("/api/public/**").permitAll()
              .anyRequest().authenticated()
          )
          .httpBasic();
      return http.build();
  }
  ```
- `@PreAuthorize` / `@PostAuthorize`: SpEL checks on methods.
  ```java
  @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
  public Recipe findOne(Long id) { return null; }
  ```
- `@Secured` / `@RolesAllowed`: role-based checks (`ROLE_` convention).
- `@AuthenticationPrincipal`: inject current user/principal.
- `@Bean PasswordEncoder`: prefer `BCryptPasswordEncoder`; `{noop}` for demos only.

### Thymeleaf (server-side views) and Security

- What: server-side HTML templates; great with session login (`formLogin()` + CSRF on by default).
- Setup: add `spring-boot-starter-thymeleaf` and `thymeleaf-extras-springsecurity6`.
- Use the security dialect (`sec:`) to show/hide content and read the current user.

Example (roles, current user, CSRF-safe logout):

```html

<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="https://www.thymeleaf.org/extras/spring-security">
<body>
<div>Hello, <span sec:authentication="name">guest</span></div>
<a sec:authorize="isAuthenticated()" th:href="@{/profile}">My Profile</a>
<a sec:authorize="hasRole('ADMIN')" th:href="@{/admin}">Admin</a>
<form sec:authorize="isAuthenticated()" th:action="@{/logout}" method="post">
    <button type="submit">Logout</button>
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
</form>
</body>
</html>
```

Notes:

- CSRF is on by default for MVC forms; `th:action` includes the token.
- For APIs (no cookies), disable CSRF and avoid server-rendered forms.

### CORS and CSRF (very brief)

- CORS: browser cross‑origin policy. For SPAs calling your API, enable Security’s CORS support and configure origins in
  MVC.
    - In Security: `http.cors(withDefaults())`. Avoid disabling CORS for browsers.
- CSRF: protects cookie‑based sessions. Keep ON for forms; turn OFF for stateless token APIs.
  ```java
  @Bean
  SecurityFilterChain api(HttpSecurity http) throws Exception {
      http
          .cors(withDefaults()) // if your API is called from a browser SPA
          .csrf(csrf -> csrf.disable()) // token APIs (no cookies)
          .authorizeHttpRequests(a -> a.anyRequest().permitAll()); // demo
      return http.build();
  }
  ```

### Authentication Strategies

Pick the approach that matches your app’s shape (demo, monolith with sessions, or stateless REST). Below are the most
common options, when to use them, and tiny examples.

1) In‑memory users (fast demos/tests)

- What: Define a few hard‑coded users via `InMemoryUserDetailsManager`.
- When: Demos, spike solutions, unit/integration tests. Not for production.
- Pros: Zero setup, very fast.
- Cons: No persistence; passwords live in config.
- Minimal setup:

```java

@Bean
UserDetailsService userDetailsService() {
    UserDetails user = User.withUsername("user").password("{noop}password").roles("USER").build();
    UserDetails admin = User.withUsername("admin").password("{noop}admin").roles("ADMIN").build();
    return new InMemoryUserDetailsManager(user, admin);
}
```

2) Database‑backed users with UserDetailsService (most apps)

- What: Load users from DB, return a `UserDetails` object; compare hashed passwords with a `PasswordEncoder`.
- When: Real apps that own their user table (email/username + hashed password + roles/authorities).
- Flow: `AuthenticationProvider` → `UserDetailsService.loadUserByUsername()` → compare password → build
  `Authentication`.
- Minimal skeleton:

```java

@Service
class MyUserDetailsService implements UserDetailsService {
    private final UserRepository repo;
    private final PasswordEncoder encoder; // e.g., BCryptPasswordEncoder

    MyUserDetailsService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())      // already encoded in DB
                .authorities(u.getAuthorities())
                .accountLocked(!u.isAccountNonLocked())
                .disabled(!u.isEnabled())
                .build();
    }
}

@Configuration
class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

Tips:

- Never store plain text; store a strong hash (BCrypt/Argon2). Re‑hash on login if you upgrade strength.
- Model roles as `ROLE_X` or use fine‑grained authorities like `recipe:write`.

3) Session‑based login (form/basic) vs Stateless APIs

- Session (stateful): Server stores `SecurityContext` in the HTTP session. Good for MVC apps and browser logins.
    - Enable CSRF protection (on by default with form login). Works well with Spring MVC + Thymeleaf.
    - Example:

```java

@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(a -> a.anyRequest().authenticated())
            .formLogin(withDefaults()) // or .httpBasic()
            .logout(withDefaults());

    return http.build();
}
// Note: CSRF is enabled by default for form login; no need to enable explicitly.
```

- Stateless API: No server session. Each request carries credentials (e.g., bearer token). Preferred for REST.
    - Disable sessions: `sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))`.
    - Turn off CSRF for token APIs (no cookies): `csrf(csrf -> csrf.disable())`.

4) JWT‑based authentication (stateless REST)

- What: Client sends `Authorization: Bearer <jwt>`; server validates signature and sets `SecurityContext`.
- When: Mobile/SPAs/microservices where servers shouldn’t hold session state.
- Pros: Scales horizontally; no session store.
- Cons: Token revocation/rotation is your responsibility; keep expirations short and use refresh tokens if needed.
- Typical flow:
    1) Client authenticates once (login) to receive a signed JWT.
    2) For each request, a filter verifies signature/claims and builds `UsernamePasswordAuthenticationToken`.
    3) Authorization rules run as usual.
- Minimal filter wiring (custom JWT filter runs before `UsernamePasswordAuthenticationFilter`):

```java

@Bean
SecurityFilterChain api(HttpSecurity http, JwtAuthFilter jwt) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                    .requestMatchers("/auth/**", "/public/**").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

Notes:

- Your `JwtAuthFilter` typically extends `OncePerRequestFilter`, reads the `Authorization: Bearer <token>` header,
  verifies the signature/claims, and on success sets an `Authentication` (e.g., `UsernamePasswordAuthenticationToken`)
  into the `SecurityContext`.
- For pure APIs, return 401 instead of redirecting to a login page. If you see redirects, configure an authentication
  entry point (e.g., `exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> res.sendError(401)))`).
- If your tokens are issued by an external IdP (Auth0/Okta/Cognito), prefer Spring Security’s Resource Server (
  `oauth2ResourceServer().jwt()`) as shown below instead of a custom JWT filter.
- Online tools for Decoding and Verifying JWTs: https://jwt.io/

5) OAuth2 / OpenID Connect

- What: Delegate login to Google/GitHub/Okta, or validate JWTs issued by an external provider.
- Two common modes:
    - OAuth2 Login (client): for browser apps that want “Login with X”.
        - Minimal setup:

```properties
spring.security.oauth2.client.registration.google.client-id=...
spring.security.oauth2.client.registration.google.client-secret=...
```

```java

@Bean
SecurityFilterChain oauth2LoginChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(a -> a.anyRequest().authenticated())
            .oauth2Login(withDefaults());
    return http.build();
}
```

- Resource Server: protect your REST API with third‑party tokens (validate incoming JWTs).
    - Minimal setup:

```properties
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://issuer/.well-known/jwks.json
```

```java

@Bean
SecurityFilterChain resourceServerChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(a -> a.anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt());
    return http.build();
}
```

- When: Enterprise SSO, multi‑app ecosystems, or when you don’t want to manage passwords.

Choosing the right strategy

- Demos/tests → In‑memory.
- Your app owns user accounts → Database + `UserDetailsService` (+ session or JWT depending on client type).
- Public REST APIs → JWT (stateless) or Resource Server.
- Browser UX with server‑rendered pages → Session + `formLogin()`.
- Prefer OAuth2/OIDC when integrating with identity providers (SSO), avoid storing passwords yourself.

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

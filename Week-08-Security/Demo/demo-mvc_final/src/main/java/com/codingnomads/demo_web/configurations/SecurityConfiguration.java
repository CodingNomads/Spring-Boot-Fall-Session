package com.codingnomads.demo_web.configurations;

import com.codingnomads.demo_web.models.ApiToken;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.ApiTokenRepository;
import com.codingnomads.demo_web.services.JwtService;
import com.codingnomads.demo_web.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

/**
 * This class configures the security for our application.
 * It defines how different parts of the application (REST API vs. Web Pages) are protected.
 * 
 * We use two separate 'SecurityFilterChains' to handle two different types of authentication:
 * 1. JWT-based (Stateless) for the /api/** endpoints.
 * 2. Session-based (Stateful) for the MVC web pages.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final ObjectMapper objectMapper;

    /**
     * PasswordEncoder is used to securely hash passwords before storing them in the database.
     * BCrypt is a standard, strong hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration for the REST API (/api/**).
     * It uses JWT (JSON Web Tokens) and is 'stateless', meaning the server doesn't remember the user between requests.
     * Each request must include a token in the 'Authorization' header.
     */
    @Bean
    @Order(1) // Higher priority to catch /api/** requests first
    public SecurityFilterChain apiSecurity(HttpSecurity http, @Lazy OncePerRequestFilter jwtAuthenticationFilter) throws Exception {
        http
                .securityMatcher("/api/**") // Only apply this chain to URLs starting with /api/
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF as JWT is resistant to it and it's hard to use with APIs
                .cors(Customizer.withDefaults()) // Enable CORS (Cross-Origin Resource Sharing)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No JSESSIONID created
                .exceptionHandling(ex -> ex
                        // Custom handlers to return JSON instead of redirecting to a login page on error
                        .authenticationEntryPoint((request, response, authException) -> writeProblem(request, response, HttpStatus.UNAUTHORIZED, authException.getMessage() == null ? "Unauthorized" : authException.getMessage()))
                        .accessDeniedHandler((request, response, accessDeniedException) -> writeProblem(request, response, HttpStatus.FORBIDDEN, accessDeniedException.getMessage() == null ? "Forbidden" : accessDeniedException.getMessage()))
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // All API requests must be authenticated
                )
                // Add our custom JWT filter before the standard username/password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Explicitly disable form login and logout for API as they are for browsers
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Configuration for the Web Pages (MVC).
     * It uses standard Session-based authentication with a login form.
     */
    @Bean
    @Order(2) // Lower priority, catches everything else that wasn't /api/**
    public SecurityFilterChain mvcSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Permit access to static resources without logging in
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                        // Permit access to home, signup, and error pages
                        .requestMatchers("/", "/signup", "/errors").permitAll()
                        // Only users with ADMIN role can access /admin/**
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Everything else requires the user to be logged in
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login") // Custom login page
                        .failureUrl("/login?error")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID") // Clean up the session cookie
                        .permitAll());

        return http.build();
    }

    /**
     * This filter intercepts every request to /api/** to check for a valid JWT.
     * It's defined as a Bean so it can be injected with other services.
     */
    @Bean
    public OncePerRequestFilter jwtAuthenticationFilter(JwtService jwtService,
                                                        ApiTokenRepository apiTokenRepository,
                                                        UserService userService) {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                String path = request.getRequestURI();
                // If it's not an API request, just pass it through to the next filter
                if (path == null || !path.startsWith("/api/")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // Check for 'Authorization: Bearer <token>' header
                String auth = request.getHeader("Authorization");
                if (auth == null || !auth.startsWith("Bearer ")) {
                    writeProblem(request, response, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
                    return;
                }

                String token = auth.substring(7); // Remove "Bearer " prefix
                try {
                    // Parse and validate the JWT using our service
                    Claims claims = jwtService.parseAndValidate(token);
                    String username = claims.getSubject();

                    // Check if it's the correct type of token
                    Object typ = claims.get("typ");
                    if (!"api".equals(typ)) {
                        writeProblem(request, response, HttpStatus.FORBIDDEN, "Not an API token");
                        return;
                    }

                    // Verify the token exists in our database and isn't revoked
                    Optional<ApiToken> tokenOpt = apiTokenRepository.findByToken(token);
                    if (tokenOpt.isEmpty()) {
                        writeProblem(request, response, HttpStatus.UNAUTHORIZED, "Token not found");
                        return;
                    }

                    ApiToken apiToken = tokenOpt.get();
                    if (apiToken.isRevoked() || apiToken.getExpiresAt().isBefore(Instant.now())) {
                        writeProblem(request, response, HttpStatus.UNAUTHORIZED, "Token expired or revoked");
                        return;
                    }

                    // If everything is OK, tell Spring Security who this user is
                    User user = (User) userService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Proceed to the next filter (and eventually the Controller)
                    filterChain.doFilter(request, response);
                } catch (ExpiredJwtException eje) {
                    writeProblem(request, response, HttpStatus.UNAUTHORIZED, "Token expired");
                } catch (JwtException | IllegalArgumentException e) {
                    writeProblem(request, response, HttpStatus.UNAUTHORIZED, "Invalid token");
                }
            }
        };
    }

    private void writeProblem(HttpServletRequest request,
                              HttpServletResponse response,
                              HttpStatus status,
                              String detail) throws IOException {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(status.getReasonPhrase());
        if (detail != null && !detail.isBlank()) {
            pd.setDetail(detail);
        }
        // Enrich with standard helpful properties
        pd.setProperty("path", request.getRequestURI());
        pd.setProperty("timestamp", Instant.now());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Cache-Control", "no-store");
        objectMapper.writeValue(response.getOutputStream(), pd);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(java.util.List.of("http://localhost:*", "https://localhost:*"));
        cfg.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "X-Requested-With"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/api/**", cfg);
        return src;
    }
}

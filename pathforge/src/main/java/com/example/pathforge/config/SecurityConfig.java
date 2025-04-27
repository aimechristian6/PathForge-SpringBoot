package com.example.pathforge.config;

import com.example.pathforge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/signup", "/login", "/css/**", "/js/**", "/images/**", "/assets/**").permitAll() // Added /assets/**
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/hr/**").hasRole("HR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/logout", "/hr/employees/deleteEmployee/**")
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            com.example.pathforge.model.User user = userService.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            String email = authentication.getName();
            com.example.pathforge.model.User user = userService.findByEmail(email);
            if (user != null) {
                userService.addUserToSession(request.getSession(), user);
                request.getSession().setAttribute("username", user.getUsername());
                request.getSession().setAttribute("email", user.getEmail());
                request.getSession().setAttribute("role", user.getRole());
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    response.sendRedirect("/admin");
                } else if ("HR".equalsIgnoreCase(user.getRole())) {
                    response.sendRedirect("/hr");
                } else {
                    response.sendRedirect("/");
                }
            } else {
                response.sendRedirect("/login?error=userNotFound");
            }
        };
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
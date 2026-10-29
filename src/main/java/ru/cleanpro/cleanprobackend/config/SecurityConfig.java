package ru.cleanpro.cleanprobackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import ru.cleanpro.cleanprobackend.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth
                        // ===== Публичные страницы =====
                        .requestMatchers(
                                "/", "/services", "/services/**", "/contacts",
                                "/order", "/order-success",
                                "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                "/login", "/register",
                                "/api/**",
                                "/search"
                        ).permitAll()

                        // ===== Управление пользователями — только ADMIN =====
                        .requestMatchers("/admin/users/**").hasRole("ADMIN")

                        // ===== Настройки сайта — только ADMIN =====
                        .requestMatchers(
                                "/admin/services/**",
                                "/admin/categories/**",
                                "/admin/reviews/**",
                                "/admin/faq/**",
                                "/admin/calculator/**",
                                "/admin/export/**"
                        ).hasRole("ADMIN")

                        // ===== Заявки и дашборд — ADMIN и EMPLOYEE =====
                        .requestMatchers("/admin/orders/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/admin").hasAnyRole("ADMIN", "EMPLOYEE")

                        // ===== Личный кабинет — любой авторизованный =====
                        .requestMatchers("/profile/**").authenticated()

                        // ===== Всё остальное — после логина =====
                        .anyRequest().authenticated()
                )
                // ===== Отключаем CSRF для публичных POST-форм =====
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/order",
                                "/register",
                                "/login"
                        )
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                            boolean isEmployee = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

                            // ADMIN и EMPLOYEE → админка с заявками
                            // USER → личный кабинет
                            String target = (isAdmin || isEmployee) ? "/admin/orders" : "/profile";
                            response.sendRedirect(target);
                        })
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}

package lab2_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // REST API: stateless HTTP Basic authentication.
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .securityMatcher("/api/**")

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .csrf(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(auth -> auth

                // USER and ADMIN can read albums.
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/albums",
                    "/api/albums/**"
                ).hasAnyRole("USER", "ADMIN")

                // Only ADMIN can create albums.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/albums",
                    "/api/albums/**"
                ).hasRole("ADMIN")

                // Only ADMIN can update albums.
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/albums",
                    "/api/albums/**"
                ).hasRole("ADMIN")

                // Only ADMIN can delete albums.
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/albums",
                    "/api/albums/**"
                ).hasRole("ADMIN")

                // Deny other unsupported API operations.
                .anyRequest().denyAll()
            )

            .httpBasic(Customizer.withDefaults())

            .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // Browser pages: session authentication with CSRF protection.
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    HttpMethod.GET,
                    "/",
                    "/index.html",
                    "/register",
                    "/login",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/favicon.ico"
                ).permitAll()

                // Public registration.
                .requestMatchers(
                    HttpMethod.POST,
                    "/register"
                ).permitAll()

                // User administration: ADMIN only.
                .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                // All other pages require authentication.
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}

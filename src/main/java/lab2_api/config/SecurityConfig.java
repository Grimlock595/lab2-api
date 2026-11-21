
package lab2_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // Public pages and resources.
                .requestMatchers(HttpMethod.GET,
                    "/", "/index.html", "/register",
                    "/swagger-ui.html", "/swagger-ui/**",
                    "/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml",
                    "/css/**", "/js/**", "/images/**", "/webjars/**",
                    "/favicon.ico"
                ).permitAll()

                .requestMatchers(HttpMethod.POST, "/register").permitAll()

                // User administration: ADMIN only.
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // REST API: both USER and ADMIN can read albums.
                .requestMatchers(HttpMethod.GET, "/api/albums", "/api/albums/**")
                    .hasAnyRole("USER", "ADMIN")

                // REST API: only ADMIN can create albums.
                .requestMatchers(HttpMethod.POST, "/api/albums", "/api/albums/**")
                    .hasRole("ADMIN")

                // REST API: only ADMIN can update albums.
                .requestMatchers(HttpMethod.PUT, "/api/albums", "/api/albums/**")
                    .hasRole("ADMIN")

                // REST API: only ADMIN can delete albums.
                .requestMatchers(HttpMethod.DELETE, "/api/albums", "/api/albums/**")
                    .hasRole("ADMIN")

                // Other endpoints require authentication.
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .permitAll()
            )

            .httpBasic(Customizer.withDefaults())

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}

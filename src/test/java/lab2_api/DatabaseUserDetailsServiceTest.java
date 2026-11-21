package lab2_api;

import lab2_api.repository.UserRepository;
import lab2_api.service.DatabaseUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DatabaseUserDetailsServiceTest {

    @Autowired
    private DatabaseUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void mapsExistingAdminFromPostgres() {
        var storedAdmin = userRepository.findByUsername("admin").orElseThrow();
        var details = userDetailsService.loadUserByUsername("admin");

        assertEquals("admin", details.getUsername());
        assertEquals(storedAdmin.isEnabled(), details.isEnabled());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }
}
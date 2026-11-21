
package lab2_api;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import lab2_api.entity.User;
import lab2_api.repository.UserRepository;
import lab2_api.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private String testUsername;

    private static final String TEST_PASSWORD =
            "Lab5TestPassword123!";

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUsername = "lab5test_" +
                UUID.randomUUID().toString().replace("-", "");
    }

    @AfterEach
    void cleanUp() {

        userRepository.findByUsername(testUsername)
                .ifPresent(userRepository::delete);
    }

    private User createTestUser(boolean enabled) {

        User user = new User();

        user.setUsername(testUsername);
        user.setPassword(
                passwordEncoder.encode(TEST_PASSWORD)
        );
        user.setRole("USER");
        user.setEnabled(enabled);
        user.setPasswordChangeRequired(false);

        return userRepository.save(user);
    }

    // Public landing page must be accessible.
    @Test
    void landingPageIsPublic() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    // Registration page must be accessible.
    @Test
    void registrationPageIsPublic() throws Exception {

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    // Login page must be accessible.
    @Test
    void loginPageIsPublic() throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    // OpenAPI documentation must be public.
    @Test
    void openApiDocumentationIsPublic() throws Exception {

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    // A valid database user can authenticate.
    @Test
    void validDatabaseUserCanAuthenticate() throws Exception {

        createTestUser(true);

        mockMvc.perform(
                get("/api/albums")
                        .with(httpBasic(
                                testUsername,
                                TEST_PASSWORD
                        ))
        ).andExpect(status().isOk());
    }

    // An incorrect password must fail.
    @Test
    void incorrectPasswordIsRejected() throws Exception {

        createTestUser(true);

        mockMvc.perform(
                get("/api/albums")
                        .with(httpBasic(
                                testUsername,
                                "IncorrectPassword123!"
                        ))
        ).andExpect(status().isUnauthorized());
    }

    // An unknown username must fail.
    @Test
    void unknownUsernameIsRejected() throws Exception {

        mockMvc.perform(
                get("/api/albums")
                        .with(httpBasic(
                                testUsername,
                                TEST_PASSWORD
                        ))
        ).andExpect(status().isUnauthorized());
    }

    // Disabled accounts cannot authenticate.
    @Test
    void disabledAccountCannotAuthenticate() throws Exception {

        createTestUser(false);

        mockMvc.perform(
                get("/api/albums")
                        .with(httpBasic(
                                testUsername,
                                TEST_PASSWORD
                        ))
        ).andExpect(status().isUnauthorized());
    }

    // Registration must store a BCrypt hash.
    @Test
    void registrationEncodesPasswordAndAssignsUserRole() {

        userService.register(
                testUsername,
                TEST_PASSWORD,
                TEST_PASSWORD
        );

        User storedUser = userRepository
                .findByUsername(testUsername)
                .orElseThrow();

        assertNotEquals(
                TEST_PASSWORD,
                storedUser.getPassword()
        );

        assertTrue(
                passwordEncoder.matches(
                        TEST_PASSWORD,
                        storedUser.getPassword()
                )
        );

        assertEquals(
                "USER",
                storedUser.getRole()
        );

        assertTrue(storedUser.isEnabled());

        assertFalse(
                storedUser.isPasswordChangeRequired()
        );
    }

    // A newly registered user can authenticate.
    @Test
    void newlyRegisteredUserCanAuthenticate() throws Exception {

        userService.register(
                testUsername,
                TEST_PASSWORD,
                TEST_PASSWORD
        );

        mockMvc.perform(
                get("/api/albums")
                        .with(httpBasic(
                                testUsername,
                                TEST_PASSWORD
                        ))
        ).andExpect(status().isOk());
    }

    // BCrypt must accept the correct password
    // and reject an incorrect one.
    @Test
    void bcryptPasswordMatchingWorks() {

        User user = createTestUser(true);

        assertTrue(
                passwordEncoder.matches(
                        TEST_PASSWORD,
                        user.getPassword()
                )
        );

        assertFalse(
                passwordEncoder.matches(
                        "WrongPassword",
                        user.getPassword()
                )
        );
    }
}

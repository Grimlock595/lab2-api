
package lab2_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // Unauthenticated users cannot read protected albums.
    @Test
    void unauthenticatedUserCannotReadAlbums() throws Exception {

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isUnauthorized());
    }

    // Regular users can read albums.
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void regularUserCanReadAlbums() throws Exception {

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk());
    }

    // Administrators can read albums.
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanReadAlbums() throws Exception {

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk());
    }

    // Regular users cannot open the administration page.
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void regularUserCannotAccessAdminPage() throws Exception {

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    // Administrators can open the administration page.
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanAccessAdminPage() throws Exception {

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());
    }

    // Regular users cannot submit administrator role changes.
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void regularUserCannotChangeRoles() throws Exception {

        mockMvc.perform(post("/admin/users/1/role")
                        .with(csrf())
                        .param("role", "USER"))
                .andExpect(status().isForbidden());
    }

    // Administrators must provide a valid CSRF token for POST requests.
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminPostWithoutCsrfIsForbidden() throws Exception {

        mockMvc.perform(post("/admin/users/1/role")
                        .param("role", "USER"))
                .andExpect(status().isForbidden());
    }
}

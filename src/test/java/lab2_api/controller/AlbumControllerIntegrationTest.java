
package lab2_api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "testuser", roles = "USER")
class AlbumControllerIntegrationTest {

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

    // USER and ADMIN can read albums.

    @Test
    void shouldGetAllAlbums() throws Exception {

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetAlbumById() throws Exception {

        mockMvc.perform(get("/api/albums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albumId").value(1))
                .andExpect(jsonPath("$.title")
                        .value("For Those About To Rock We Salute You"));
    }

    @Test
    void shouldReturnNotFoundForMissingAlbum() throws Exception {

        mockMvc.perform(get("/api/albums/999999"))
                .andExpect(status().isNotFound());
    }

    // Only ADMIN can create albums.

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldCreateAlbum() throws Exception {

        mockMvc.perform(post("/api/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "albumId": null,
                                    "title": "Integration Test Album",
                                    "artistId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.albumId").isNumber())
                .andExpect(jsonPath("$.title")
                        .value("Integration Test Album"))
                .andExpect(jsonPath("$.artistId").value(1));
    }

    // Only ADMIN can update albums.

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldUpdateAlbum() throws Exception {

        mockMvc.perform(put("/api/albums/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "albumId": 1,
                                    "title": "Updated Integration Test Title",
                                    "artistId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albumId").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Updated Integration Test Title"))
                .andExpect(jsonPath("$.artistId").value(1));
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldReturnNotFoundWhenUpdatingMissingAlbum() throws Exception {

        mockMvc.perform(put("/api/albums/999999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "albumId": 999999,
                                    "title": "Missing Album",
                                    "artistId": 1
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    // Only ADMIN can delete albums.

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldDeleteAlbum() throws Exception {

        String response = mockMvc.perform(post("/api/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "albumId": null,
                                    "title": "Album To Delete",
                                    "artistId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int albumId = com.fasterxml.jackson.databind.json.JsonMapper
                .builder()
                .build()
                .readTree(response)
                .get("albumId")
                .asInt();

        mockMvc.perform(delete("/api/albums/" + albumId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldReturnNotFoundWhenDeletingMissingAlbum() throws Exception {

        mockMvc.perform(delete("/api/albums/999999")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // Security tests: USER cannot modify albums.

    @Test
    void userCannotCreateAlbum() throws Exception {

        mockMvc.perform(post("/api/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "albumId": null,
                                    "title": "Unauthorized Album",
                                    "artistId": 1
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void userCannotUpdateAlbum() throws Exception {

        mockMvc.perform(put("/api/albums/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "albumId": 1,
                                    "title": "Unauthorized Update",
                                    "artistId": 1
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void userCannotDeleteAlbum() throws Exception {

        mockMvc.perform(delete("/api/albums/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}

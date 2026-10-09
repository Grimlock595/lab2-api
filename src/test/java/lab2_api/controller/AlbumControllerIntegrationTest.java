package lab2_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AlbumControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllAlbums() throws Exception {
        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
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

    @Test
    void shouldCreateAlbum() throws Exception {
        mockMvc.perform(post("/api/albums")
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
                .andExpect(jsonPath("$.title").value("Integration Test Album"))
                .andExpect(jsonPath("$.artistId").value(1));
    }

    @Test
    void shouldUpdateAlbum() throws Exception {
        mockMvc.perform(put("/api/albums/1")
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

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingAlbum() throws Exception {
        mockMvc.perform(put("/api/albums/999999")
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

    @Test
    void shouldDeleteAlbum() throws Exception {
        // Create a temporary album first, then delete it.
        String response = mockMvc.perform(post("/api/albums")
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

        int albumId = com.fasterxml.jackson.databind.json.JsonMapper.builder()
                .build()
                .readTree(response)
                .get("albumId")
                .asInt();

        mockMvc.perform(delete("/api/albums/" + albumId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingAlbum() throws Exception {
        mockMvc.perform(delete("/api/albums/999999"))
                .andExpect(status().isNotFound());
    }
}


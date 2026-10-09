package lab2_api.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    void shouldRetrieveAlbumData() {
        Album album = albumRepository.findById(1).orElse(null);

        assertNotNull(album);
        assertEquals(1, album.getAlbumId());
        assertEquals("For Those About To Rock We Salute You", album.getTitle());
    }
}

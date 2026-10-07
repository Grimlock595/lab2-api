package lab2_api.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lab2_api.dto.AlbumDto;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void getAllAlbumsReturnsAlbums() {
        Album album = createAlbum(1, "Test Album", 1);

        when(albumRepository.findAll()).thenReturn(List.of(album));

        List<AlbumDto> result = albumService.getAllAlbums();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).albumId());
        assertEquals("Test Album", result.get(0).title());
        assertEquals(1, result.get(0).artistId());
    }

    @Test
    void getAlbumByIdReturnsAlbum() {
        Album album = createAlbum(1, "Test Album", 1);

        when(albumRepository.findById(1)).thenReturn(Optional.of(album));

        AlbumDto result = albumService.getAlbumById(1);

        assertEquals(1, result.albumId());
        assertEquals("Test Album", result.title());
        assertEquals(1, result.artistId());
    }

    @Test
    void createAlbumReturnsSavedAlbum() {
        AlbumDto request = new AlbumDto(null, "New Album", 2);
        Album savedAlbum = createAlbum(10, "New Album", 2);

        when(albumRepository.save(any(Album.class))).thenReturn(savedAlbum);

        AlbumDto result = albumService.createAlbum(request);

        assertEquals(10, result.albumId());
        assertEquals("New Album", result.title());
        assertEquals(2, result.artistId());

        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void updateAlbumReturnsUpdatedAlbum() {
        Album existingAlbum = createAlbum(1, "Old Album", 1);
        AlbumDto request = new AlbumDto(1, "Updated Album", 2);

        when(albumRepository.findById(1)).thenReturn(Optional.of(existingAlbum));
        when(albumRepository.save(any(Album.class))).thenReturn(existingAlbum);

        AlbumDto result = albumService.updateAlbum(1, request);

        assertEquals(1, result.albumId());
        assertEquals("Updated Album", result.title());
        assertEquals(2, result.artistId());

        verify(albumRepository).save(existingAlbum);
    }

    @Test
    void deleteAlbumReturnsTrueWhenAlbumExists() {
        when(albumRepository.existsById(1)).thenReturn(true);

        boolean result = albumService.deleteAlbum(1);

        assertTrue(result);
        verify(albumRepository).deleteById(1);
    }

    @Test
    void deleteAlbumReturnsFalseWhenAlbumDoesNotExist() {
        when(albumRepository.existsById(1)).thenReturn(false);

        boolean result = albumService.deleteAlbum(1);

        assertFalse(result);
    }

    private Album createAlbum(Integer id, String title, Integer artistId) {
        Album album = new Album();
        album.setAlbumId(id);
        album.setTitle(title);
        album.setArtistId(artistId);
        return album;
    }
}

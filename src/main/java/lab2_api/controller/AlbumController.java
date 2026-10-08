package lab2_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lab2_api.dto.AlbumDto;
import lab2_api.entity.AlbumService;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    /**
     * Gets all albums.
     *
     * @return a list of all albums
     */
    @GetMapping
    public List<AlbumDto> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    /**
     * Gets an album by its ID.
     *
     * @param id the album ID
     * @return the requested album, or 404 if it does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlbumDto> getAlbumById(@PathVariable Integer id) {
        AlbumDto album = albumService.getAlbumById(id);

        if (album == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(album);
    }

    /**
     * Creates a new album.
     *
     * @param albumDto the album data
     * @return the newly created album
     */
    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody AlbumDto albumDto) {
        AlbumDto createdAlbum = albumService.createAlbum(albumDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAlbum);
    }

    /**
     * Updates an existing album.
     *
     * @param id the album ID
     * @param albumDto the updated album data
     * @return the updated album, or 404 if it does not exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlbumDto> updateAlbum(
            @PathVariable Integer id,
            @RequestBody AlbumDto albumDto) {

        AlbumDto updatedAlbum = albumService.updateAlbum(id, albumDto);

        if (updatedAlbum == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedAlbum);
    }

    /**
     * Deletes an album.
     *
     * @param id the album ID
     * @return 204 if deleted, or 404 if the album does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Integer id) {
        boolean deleted = albumService.deleteAlbum(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}

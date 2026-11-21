
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
     * Requires an authenticated USER or ADMIN.
     *
     * @return a list of all albums
     */
    @Operation(
        summary = "Get all albums",
        description = "Returns a list of all albums. " +
                      "Requires authentication with the USER or ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Albums retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public List<AlbumDto> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    /**
     * Gets an album by its ID.
     *
     * Requires an authenticated USER or ADMIN.
     *
     * @param id the album ID
     * @return the requested album, or 404 if it does not exist
     */
    @Operation(
        summary = "Get album by ID",
        description = "Returns a specific album by its ID. " +
                      "Requires authentication with the USER or ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Album retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Album not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumDto> getAlbumById(
            @PathVariable Integer id) {

        AlbumDto album = albumService.getAlbumById(id);

        if (album == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(album);
    }

    /**
     * Creates a new album.
     *
     * Requires an authenticated ADMIN.
     *
     * @param albumDto the album data
     * @return the newly created album
     */
    @Operation(
        summary = "Create a new album",
        description = "Creates a new album in the Chinook database. " +
                      "Only authenticated users with the ADMIN role " +
                      "can perform this operation."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Album created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid album data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(
            @RequestBody AlbumDto albumDto) {

        AlbumDto createdAlbum = albumService.createAlbum(albumDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAlbum);
    }

    /**
     * Updates an existing album.
     *
     * Requires an authenticated ADMIN.
     *
     * @param id the album ID
     * @param albumDto the updated album data
     * @return the updated album, or 404 if it does not exist
     */
    @Operation(
        summary = "Update an existing album",
        description = "Updates the information for an existing album. " +
                      "Only authenticated users with the ADMIN role " +
                      "can perform this operation."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Album updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid album data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Album not found")
    })
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
     * Deletes an album by its ID.
     *
     * Requires an authenticated ADMIN.
     *
     * @param id the album ID
     * @return 204 if deleted, or 404 if the album does not exist
     */
    @Operation(
        summary = "Delete an album",
        description = "Deletes an album from the Chinook database. " +
                      "Only authenticated users with the ADMIN role " +
                      "can perform this operation."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Album deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Album not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(
            @PathVariable Integer id) {

        boolean deleted = albumService.deleteAlbum(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}

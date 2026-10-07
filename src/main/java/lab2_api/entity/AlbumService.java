package lab2_api.entity;

import java.util.List;

import org.springframework.stereotype.Service;

import lab2_api.dto.AlbumDto;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public List<AlbumDto> getAllAlbums() {
        return albumRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public AlbumDto getAlbumById(Integer id) {
        return albumRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public AlbumDto createAlbum(AlbumDto albumDto) {
        Album album = new Album();
        album.setTitle(albumDto.title());
        album.setArtistId(albumDto.artistId());

        Album savedAlbum = albumRepository.save(album);

        return toDto(savedAlbum);
    }

    public AlbumDto updateAlbum(Integer id, AlbumDto albumDto) {
        return albumRepository.findById(id)
                .map(album -> {
                    album.setTitle(albumDto.title());
                    album.setArtistId(albumDto.artistId());

                    Album updatedAlbum = albumRepository.save(album);

                    return toDto(updatedAlbum);
                })
                .orElse(null);
    }

    public boolean deleteAlbum(Integer id) {
        if (!albumRepository.existsById(id)) {
            return false;
        }

        albumRepository.deleteById(id);
        return true;
    }

    private AlbumDto toDto(Album album) {
        return new AlbumDto(
                album.getAlbumId(),
                album.getTitle(),
                album.getArtistId()
        );
    }
}

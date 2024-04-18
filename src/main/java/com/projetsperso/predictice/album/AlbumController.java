package com.projetsperso.predictice.album;

import com.projetsperso.predictice.dto.MusicLibrary;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/albums")
@Validated
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/search")
    public MusicLibrary searchAlbums(@RequestParam(required = false, defaultValue = "") String searchTerm,
                                     @RequestParam(required = false, defaultValue = "0") @Min(0) Integer releaseYear,
                                     @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                     @RequestParam(required = false, defaultValue = "10") @Min(0) @Max(100) int size) {
        return albumService.searchAlbums(searchTerm, releaseYear, page, size);
    }

    @GetMapping
    public MusicLibrary fetchAlbums() {
        return albumService.searchAlbums("searchTerm", -1, 0, 10);
    }

    @GetMapping("/{albumId}")
    public Album findAlbum(@PathVariable String albumId) {
        return albumService.findById(albumId);
    }
}

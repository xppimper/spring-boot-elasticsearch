package com.projetsperso.predictice.album;

import com.projetsperso.predictice.TestPredicticeApplication;
import com.projetsperso.predictice.dto.MusicLibrary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Import(TestPredicticeApplication.class)
class AlbumServiceTest {

    @Autowired
    ElasticsearchContainer elasticsearchContainer;

    @Autowired
    AlbumService albumService;

    @Test
    void connectionEstablished() {
        assertTrue(elasticsearchContainer.isCreated());
    }

    @Test
    void fetchAlbumsWithDefaultParams() {
        MusicLibrary musicLibrary = albumService.searchAlbums("", 0, 0, 10);
        assertEquals(10, musicLibrary.albums().size()); // as per pagination size
        assertEquals(42, musicLibrary.releaseYears().size());
        assertEquals("1ca3e093-c2c1-4fa9-b144-4f9d139f23b9", musicLibrary.albums().getFirst().getId());
        assertEquals("1ca3e093-c2c1-4fa9-b144-4f9d139f23b9", musicLibrary.albums().getFirst().getId());
        assertEquals(89, musicLibrary.releaseYears().get("2000").longValue());
    }

    @Test
    void searchAlbumsWithGivenTerm() {
        MusicLibrary musicLibrary = albumService.searchAlbums("green", 0, 0, 10);
        assertEquals(6, musicLibrary.albums().size());
        assertEquals(4, musicLibrary.releaseYears().size());
        assertTrue(musicLibrary.albums().getFirst().getTitle().toLowerCase().contains("green"));
        assertTrue(musicLibrary.albums().getLast().getArtist().toLowerCase().contains("green"));
    }

    @Test
    void searchAlbumsWithGivenTermFilteredByGivenYear() {
        MusicLibrary musicLibrary = albumService.searchAlbums("green", 1998, 0, 10);
        assertEquals(2, musicLibrary.albums().size());
        for (String year : musicLibrary.releaseYears().keySet()) {
            assertEquals("1998", year);
        }
        assertTrue(musicLibrary.albums().getFirst().getArtist().equalsIgnoreCase("Green Day"));
        assertTrue(musicLibrary.albums().getLast().getArtist().equalsIgnoreCase("Green Day"));
    }

    @Test
    void searchAlbumsWithEmptyResults() {
        MusicLibrary musicLibrary = albumService.searchAlbums("green", 2000, 0, 10);
        assertEquals(0, musicLibrary.albums().size());
        assertEquals(0, musicLibrary.releaseYears().size());
    }

    @Test
    void searchAlbumsFilteredByYearWithNoSearchTerm() {
        MusicLibrary musicLibrary = albumService.searchAlbums("", 2000, 0, 10);
        assertEquals(89, musicLibrary.releaseYears().get("2000").longValue());
    }

    @Test
    void searchAlbumsFilteredByYearEndOfPagination() {
        // Give me the next 30 from 90 onwards
        MusicLibrary musicLibrary = albumService.searchAlbums("", 2000, 3, 30);
        assertEquals(89, musicLibrary.releaseYears().get("2000").longValue());
        assertEquals(0, musicLibrary.albums().size());
    }
}
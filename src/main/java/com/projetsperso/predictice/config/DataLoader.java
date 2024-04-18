package com.projetsperso.predictice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetsperso.predictice.album.Album;
import com.projetsperso.predictice.album.AlbumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    @Value("${predictice.sample-data}")
    public static final String DATA_ALBUM_SAMPLE_JSON = "/data/album_sample.json";

    private final ObjectMapper objectMapper;
    private final AlbumRepository albumRepository;

    public DataLoader(ObjectMapper objectMapper, AlbumRepository albumRepository) {
        this.objectMapper = objectMapper;
        this.albumRepository = albumRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        albumRepository.deleteAll();
        if (Streamable.of(albumRepository.findAll()).isEmpty()) {
            try (InputStream inputStream = TypeReference.class.getResourceAsStream(DATA_ALBUM_SAMPLE_JSON)) {
                var albums = objectMapper.readValue(inputStream, Album[].class);
                log.info("Reading {} albums from album_sample.json", albums.length);
                albumRepository.saveAll(Arrays.stream(albums).toList());
            } catch (IOException e) {
                log.error("Failed to read JSON data");
                throw new RuntimeException("Unable to load data from JSON file", e);
            }
        }
    }
}

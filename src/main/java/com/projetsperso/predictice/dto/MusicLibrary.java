package com.projetsperso.predictice.dto;

import com.projetsperso.predictice.album.Album;

import java.util.List;
import java.util.Map;

public record MusicLibrary(List<Album> albums, Map<String, Long> releaseYears) {
}

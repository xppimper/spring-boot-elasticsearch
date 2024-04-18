package com.projetsperso.predictice.album;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends ElasticsearchRepository<Album, String> {
}

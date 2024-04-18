package com.projetsperso.predictice.album;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.projetsperso.predictice.dto.MusicLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

@Service
public class AlbumService {
    private final static Logger log = LoggerFactory.getLogger(AlbumService.class);

    private final AlbumRepository albumRepository;
    private final ElasticsearchClient elasticsearchClient;


    public AlbumService(AlbumRepository albumRepository, ElasticsearchClient elasticsearchClient) {
        this.albumRepository = albumRepository;
        this.elasticsearchClient = elasticsearchClient;
    }

    public MusicLibrary searchAlbums(String searchTerm, Integer year, int page, int size) {
        SearchResponse<Album> searchResponse;
        try {
            searchResponse = elasticsearchClient.search(s -> s.index("album")
                    .aggregations("releaseYears", Aggregation.of(a -> a.terms(ta -> ta.field("releaseYear").size(100))))
                    .from(page * size)
                    .size(size)
                    .query(prepareQuery(searchTerm, year).get()), Album.class);
        } catch (IOException e) {

            log.error("Unable to reach Elasticsearch Server for searchTerm({})", searchTerm, e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to process the search", e);
        }

        ArrayList<Album> albums = new ArrayList<>();
        List<Hit<Album>> hits = searchResponse.hits().hits();
        for (Hit<Album> hit : hits) {
            albums.add(hit.source());
        }

        HashMap<String, Long> releaseYears = new HashMap<>();
        if (!searchResponse.aggregations().isEmpty()) {
            List<StringTermsBucket> buckets = searchResponse.aggregations().get("releaseYears").sterms().buckets().array();
            for (StringTermsBucket bucket : buckets) {
                releaseYears.put(bucket.key().stringValue(), bucket.docCount());
            }
        }

        return new MusicLibrary(albums, releaseYears);
    }

    public Album findById(String id) {
        return albumRepository.findById(id).orElseThrow(AlbumNotFoundException::new);
    }

    private Supplier<Query> prepareQuery(String searchTerm, Integer year) {
        if (searchTerm.isBlank()) {
            if (year <= 0) {
                return () -> Query.of(q -> q.matchAll(ma -> ma));
            }

            return () -> Query.of(q -> q.bool(BoolQuery.of(bq ->
                bq.filter(f -> f.term(t -> t.field("releaseYear").value(year)))
            )));
        }

        MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(m -> m.fields(List.of("title", "artist"))
                .fuzziness("AUTO")
                .operator(Operator.Or)
                .query(searchTerm));

        if (year <= 0) {
            return () -> Query.of(q -> q.multiMatch(multiMatchQuery));
        }

        return () -> Query.of(q -> q.bool(BoolQuery.of(bq -> {
            bq.filter(f -> f.term(t -> t.field("releaseYear").value(year)));
            bq.must(Query.of(mq -> mq.multiMatch(multiMatchQuery)));
            return bq;
        })));
    }
}

package ucsal.tcc.app.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ucsal.tcc.app.common.SearchParams;
import ucsal.tcc.app.model.ReviewDocument;
import ucsal.tcc.app.model.ReviewRank;
import ucsal.tcc.app.model.ReviewRepository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticHelperService {

    private static final Logger log = LoggerFactory.getLogger(ElasticHelperService.class);
    private final ElasticsearchClient esClient;
    private final ReviewRepository reviewRepository;
    private static final String INDEX = "reviews";
    private static final String INDEXED_TEXT_FIELD = "indexedText";
    private static final String SUMMARY_FIELD = "summary";
    private static final String SCORE_FILED = "_score";

    @PreDestroy
    public void close() throws IOException {
        esClient._transport().close();
    }

    public List<ReviewDocument> matchQuery(SearchParams params) {
        try {
            long start = System.currentTimeMillis();
            SearchResponse<ReviewDocument> response = esClient.search(s -> s
                            .index(INDEX)
                            .from(params.getPage())
                            .size(params.getPageSize())
                            .query(q -> q
                                    .match(match -> match
                                            .field(INDEXED_TEXT_FIELD)
                                            .query(params.getText())
                                    )
                            )
                            .sort(sort -> sort
                                    .field(f -> f
                                            .field(SCORE_FILED)
                                            .order(SortOrder.Desc)
                                    )
                            ),
                    ReviewDocument.class
            );
            long end = System.currentTimeMillis();
            log.info("tempo {}", end - start);
            return response.hits().hits().stream().map(Hit::source).toList();
        } catch (IOException e) {
            log.error("", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro busca ES!");
        }
    }

    public List<ReviewDocument> fuzzyMatch(SearchParams params) {
        try {
            SearchResponse<ReviewDocument> response = esClient.search(s -> s
                            .index(INDEX)
                            .from(params.getPage())
                            .size(params.getPageSize())
                            .query(q -> q
                                    .fuzzy(fuzzy -> fuzzy
                                            .field(SUMMARY_FIELD)
                                            .fuzziness("AUTO")
                                            .value(params.getText())
                                    )
                            )
                            .sort(sort -> sort
                                    .field(f -> f
                                            .field(SCORE_FILED)
                                            .order(SortOrder.Desc)
                                    )
                            ),
                    ReviewDocument.class
            );
            return response.hits().hits().stream().map(Hit::source).toList();
        } catch (IOException e) {
            log.error("", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro busca fuzzy ES!");
        }
    }

    @Transactional(readOnly = true)
    public void indexReviews() {
        final AtomicInteger count = new AtomicInteger(0);

        try (BulkIngester<Void> ingester = BulkIngester.of(b -> b
                .client(esClient)
                .maxOperations(100)
                .flushInterval(1, TimeUnit.SECONDS)
        )) {

            try (Stream<ReviewRank> reviewStream = reviewRepository.streamAll()) {
                reviewStream.forEach(r -> {
                    IndexOperation<ReviewDocument> operation = buildOperation(r);

                    if (operation == null) {
                        count.addAndGet(1);
                        return;
                    }

                    ingester.add(op -> op.index(operation));
                    count.addAndGet(1);
                    log.info(String.format("[INDEXED] ID: [%s] | Total: [%s]", r.getId(), count.get()));
                });
            }
        }
    }

    private IndexOperation<ReviewDocument> buildOperation(ReviewRank data) {
        if (data == null) {
            return null;
        }
        ReviewDocument document = ReviewDocument.from(data);
        return IndexOperation.of(op -> op
                .document(document)
                .index(INDEX)
                .id(data.getId().toString())
        );
    }

}

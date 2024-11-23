package ucsal.tcc.app.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ucsal.tcc.app.elasticsearch.ElasticHelperService;
import ucsal.tcc.app.model.ReviewDocument;
import ucsal.tcc.app.model.ReviewRank;
import ucsal.tcc.app.model.PostgresHelperService;
import ucsal.tcc.app.model.Review;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class SearchController {

    private final PostgresHelperService pgService;
    private final ElasticHelperService esService;

    @GetMapping("/postgres/fts/indexed-text")
    public Page<Review> postgresFullTextSearch(SearchParams params) {
        return pgService.matchReviews(params);
    }

    @GetMapping("/postgres/fts/indexed-text/frequency-rank")
    public Page<ReviewRank> postgresFullTextSearchIndexedWithFrequencyRank(SearchParams params) {
        return pgService.matchReviewsWithFrequencyRank(params);
    }

    @GetMapping("/postgres/fts/indexed-text/context-dependent-rank")
    public Page<ReviewRank> postgresFullTextSearchIndexedWithRank(SearchParams params) {
        return pgService.matchReviewsWithContextDependentRank(params);
    }

    @GetMapping("/es/fts/indexed-text")
    public List<ReviewDocument> esFullTextSearch(SearchParams params) {
        return esService.matchQuery(params);
    }

    @GetMapping("/postgres/fuzzy/summary")
    public Page<ReviewRank> postgresFuzzySearch(SearchParams params) {
        return pgService.fuzzyMatch(params);
    }

    @GetMapping("/es/fuzzy/summary")
    public List<ReviewDocument> esFuzzySearch(SearchParams params) {
        return esService.fuzzyMatch(params);
    }

    @GetMapping("/postgres/contains/text")
    public Page<Review> postgresContains(SearchParams params) {
        return pgService.matchReviewsContains(params);
    }

    @GetMapping("/postgres/fts/text")
    public Page<Review> postgresFullTextSearchNotIndexed(SearchParams params) {
        return pgService.matchReviewsNotIndexed(params);
    }

    @PostMapping("/es/index")
    public void index() {
        esService.indexReviews();
    }

}

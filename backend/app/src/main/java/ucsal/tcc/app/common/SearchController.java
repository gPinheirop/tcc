package ucsal.tcc.app.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ucsal.tcc.app.postgres.ReviewRank;
import ucsal.tcc.app.postgres.PostgresHelperService;
import ucsal.tcc.app.postgres.Review;

@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class SearchController {

    private final PostgresHelperService pgService;

    @GetMapping("/postgres/fts/indexed-text")
    public Page<Review> postgresFullTextSearchIndexed(SearchParams params) {
        return pgService.matchReviewsIndexed(params);
    }

    @GetMapping("/postgres/fts/indexed-text/frequency-rank")
    public Page<ReviewRank> postgresFullTextSearchIndexedWithFrequencyRank(SearchParams params) {
        return pgService.matchReviewsIndexedWithFrequencyRank(params);
    }

    @GetMapping("/postgres/fts/indexed-text/context-dependent-rank")
    public Page<ReviewRank> postgresFullTextSearchIndexedWithRank(SearchParams params) {
        return pgService.matchReviewsIndexedWithContextDependentRank(params);
    }

    @GetMapping("/postgres/fts/text")
    public Page<Review> postgresFullTextSearchNotIndexed(SearchParams params) {
        return pgService.matchReviewsNotIndexed(params);
    }

}

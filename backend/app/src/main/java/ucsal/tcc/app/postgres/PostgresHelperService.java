package ucsal.tcc.app.postgres;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ucsal.tcc.app.common.SearchParams;

@Service
@RequiredArgsConstructor
public class PostgresHelperService {

    private final ReviewRepository repository;

    public Page<Review> matchReviewsIndexed(SearchParams params) {
        checkNotNull(params);
        params.setSortField("indexed_text");
        Pageable pageable = PageRequest.of(params.getPage(), params.getPageSize(), params.getSort());
        return repository.fullTextSearch(params.getText(), pageable);
    }

    public Page<ReviewRank> matchReviewsIndexedWithFrequencyRank(SearchParams params) {
        checkNotNull(params);
        params.setSortField("rank");
        Pageable pageable = PageRequest.of(params.getPage(), params.getPageSize(), params.getSort());
        return repository.fullTextSearchIndexedWithFrequencyRank(params.getText(), pageable);
    }

    public Page<ReviewRank> matchReviewsIndexedWithContextDependentRank(SearchParams params) {
        checkNotNull(params);
        params.setSortField("rank");
        Pageable pageable = PageRequest.of(params.getPage(), params.getPageSize(), params.getSort());
        return repository.fullTextSearchIndexedWithContextDependentRank(params.getText(), pageable);
    }

    public Page<Review> matchReviewsNotIndexed(SearchParams params) {
        checkNotNull(params);
        params.setSortField("text");
        Pageable pageable = PageRequest.of(params.getPage(), params.getPageSize(), params.getSort());
        return repository.fullTextSearchNotIndexed(params.getText(), pageable);
    }

    private void checkNotNull(SearchParams params) {
        if (params != null) {
            return;
        }

        throw new RuntimeException("Parâmetros de busca inválidos");
    }
}

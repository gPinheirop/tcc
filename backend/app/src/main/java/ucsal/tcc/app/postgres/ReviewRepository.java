package ucsal.tcc.app.postgres;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = """
            SELECT *
            FROM review
            WHERE to_tsvector('english', indexed_text) @@ plainto_tsquery(:text)
            """, nativeQuery = true)
    Page<Review> fullTextSearch(@Param("text") String text, Pageable pageable);

    @Query(value = """
            SELECT *
            FROM review
            WHERE to_tsvector('english', text) @@ plainto_tsquery(:text)
            """, nativeQuery = true)
    Page<Review> fullTextSearchNotIndexed(@Param("text") String text, Pageable pageable);

    @Query(value = """
            SELECT
                *,
                ts_rank(to_tsvector('english', indexed_text), plainto_tsquery('english', :text)) rank
            FROM
                review
            WHERE
                to_tsvector('english', indexed_text) @@ plainto_tsquery('english', :text)
            """, nativeQuery = true)
    Page<ReviewRank> fullTextSearchIndexedWithFrequencyRank(@Param("text") String text, Pageable pageable);

    @Query(value = """
            SELECT
                *,
                ts_rank_cd(to_tsvector('english', indexed_text), plainto_tsquery('english', :text)) rank
            FROM
                review
            WHERE
                to_tsvector('english', indexed_text) @@ plainto_tsquery('english', :text)
            """, nativeQuery = true)
    Page<ReviewRank> fullTextSearchIndexedWithContextDependentRank(@Param("text") String text, Pageable pageable);

}

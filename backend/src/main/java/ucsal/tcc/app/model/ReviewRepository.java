package ucsal.tcc.app.model;

import jakarta.persistence.QueryHint;
import org.hibernate.jpa.AvailableHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

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
            SELECT *
            FROM review
            WHERE text ILIKE (CONCAT('%', :text, '%'))
            """, nativeQuery = true)
    Page<Review> contains(@Param("text") String text, Pageable pageable);

    @Query(value = """
            SELECT
                *,
                ts_rank(to_tsvector('english', indexed_text), plainto_tsquery('english', :text)) rank
            FROM
                review
            WHERE
                to_tsvector('english', indexed_text) @@ plainto_tsquery('english', :text)
            ORDER BY rank DESC
            """, nativeQuery = true)
    Page<ReviewRank> fullTextSearchWithFrequencyRank(@Param("text") String text, Pageable pageable);

    @Query(value = """
            SELECT
                *,
                ts_rank_cd(to_tsvector('english', indexed_text), plainto_tsquery('english', :text)) rank
            FROM
                review
            WHERE
                to_tsvector('english', indexed_text) @@ plainto_tsquery('english', :text)
            ORDER BY rank DESC
            """, nativeQuery = true)
    Page<ReviewRank> fullTextSearchWithContextDependentRank(@Param("text") String text, Pageable pageable);

    @Query(value = """
            SELECT
                *,
                levenshtein(summary, :text) AS distance
            FROM
                review
            WHERE
                to_tsvector('english', summary) @@ plainto_tsquery('english', :text)
            ORDER BY
                distance
            """, nativeQuery = true)
    Page<ReviewRank> fuzzySearch(@Param("text") String text, Pageable pageable);

    @Query(value = """
            SELECT
                id AS id,
                summary AS summary,
                text AS text,
                indexed_text AS indexedText,
                time AS time
            FROM
                review
            """, nativeQuery = true)
    @QueryHints(
            @QueryHint(name = AvailableHints.HINT_FETCH_SIZE, value = "100")
    )
    Stream<ReviewRank> streamAll();
}

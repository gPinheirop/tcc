package ucsal.tcc.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDocument {
    private Long id;
    private String summary;
    private String text;
    private String indexedText;
    private Long time;

    public static ReviewDocument from(ReviewRank from) {
        if (from == null) {
            return null;
        }
        return ReviewDocument.builder()
                .id(from.getId())
                .summary(from.getSummary())
                .text(from.getText())
                .indexedText(from.getIndexedText())
                .time(from.getTime())
                .build();
    }
}

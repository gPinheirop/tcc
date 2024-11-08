package ucsal.tcc.app.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    private Long id;
    private String summary;
    private String text;
    private String indexedText;
    private Long time;

}

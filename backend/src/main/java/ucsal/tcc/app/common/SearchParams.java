package ucsal.tcc.app.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchParams {

    public enum SortOrder {
        ASC, DESC
    }

    private String text;
    private String summary;

    private SortOrder sortOrder = SortOrder.DESC;
    private String sortField = "indexed_text";
    private int page = 0;
    private int pageSize = 100;

    public Sort getSort() {
        return sortOrder.equals(SortOrder.DESC) ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
    }

}

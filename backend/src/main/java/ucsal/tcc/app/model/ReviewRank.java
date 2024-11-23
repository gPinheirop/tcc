package ucsal.tcc.app.model;

import jakarta.persistence.Id;

public interface ReviewRank {
    Long getId();
    String getSummary();
    String getText();
    String getIndexedText();
    Long getTime();
    Double getRank();
    Integer getDistance();
}

package ucsal.tcc.app.postgres;

public interface ReviewRank {
    Long getId();
    String getSummary();
    String getText();
    String getIndexedText();
    Long getTime();
    Double getRank();
}

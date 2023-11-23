package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public final class UrlCheck {
    private Long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private Long urlId;
    private Timestamp createdAt;

    public UrlCheck(int statusCode, String title, String h1, String description, Long urlId, Timestamp createdAt) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.urlId = urlId;
        this.createdAt = createdAt;
    }

    public String getCreatedAtToString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return formatter.format(createdAt.toLocalDateTime());
    }
}

package mate.academy.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({
        "id",
        "title",
        "author",
        "isbn",
        "price",
        "description",
        "coverImage",
})
@Getter
@Setter
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
}

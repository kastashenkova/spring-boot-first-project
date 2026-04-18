package mate.academy.util;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.CategoryDto;
import mate.academy.dto.book.CreateBookRequestDto;

public class TestUtil {
    public static BookDto bookDtoISeeYouAreInterestedInDarkness() {
        return new BookDto()
                .setId(1L)
                .setTitle("I See You Are Interested in Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-847-6")
                .setPrice(BigDecimal.valueOf(15.99))
                .setCategoryIds(List.of());
    }

    public static BookDto bookDtoTheFoolsDance() {
        return new BookDto()
                .setId(2L)
                .setTitle("The Fool's Dance")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-921-3")
                .setPrice(BigDecimal.valueOf(12.99))
                .setCategoryIds(List.of());
    }

    public static CreateBookRequestDto createBookDtoTheBookOfEmil() {
        return new CreateBookRequestDto()
                .setTitle("The Book of Emil")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-966-448-494-4")
                .setPrice(BigDecimal.valueOf(70))
                .setCategoryIds(List.of(1L));
    }

    public static CreateBookRequestDto createBookDtoTangoOfDeath() {
        return new CreateBookRequestDto()
                .setTitle("Tango of Death")
                .setAuthor("Yurii Vynnychuk")
                .setIsbn("978-617-585-236-1")
                .setPrice(BigDecimal.valueOf(35))
                .setCategoryIds(List.of(1L));
    }

    public static CategoryDto categoryDtoFantasy() {
        return new CategoryDto()
                .setName("Fantasy")
                .setDescription("Fantasy books");
    }
}

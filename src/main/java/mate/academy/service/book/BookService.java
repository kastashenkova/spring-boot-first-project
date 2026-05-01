package mate.academy.service.book;

import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.repository.book.BookSearchParameters;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto) throws BadRequestException;

    Page<BookDto> getAll(Pageable pageable);

    BookDto getBookById(Long id);

    BookDto updateBookById(Long id, CreateBookRequestDto requestDto);

    void deleteBookById(Long id);

    Page<BookDto> search(BookSearchParameters searchParameters, Pageable pageable);

    Page<BookDtoWithoutCategoryIds> getAllByCategoryId(Long id, Pageable pageable);
}

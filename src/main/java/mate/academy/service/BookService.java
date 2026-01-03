package mate.academy.service;

import java.util.List;
import mate.academy.dto.BookDto;
import mate.academy.dto.CreateBookRequestDto;
import mate.academy.repository.book.BookSearchParameters;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto getBookById(Long id);

    BookDto updateBookById(Long id, CreateBookRequestDto requestDto);

    void deleteBookById(Long id);

    List<BookDto> search(BookSearchParameters searchParameters, Pageable pageable);
}

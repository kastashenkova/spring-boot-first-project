package mate.academy.service.book;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.book.BookSearchParameters;
import mate.academy.repository.book.BookSpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toBookEntity(requestDto);
        bookRepository.save(book);
        return bookMapper.toBookDto(book);
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toBookDto);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find book by id: " + id));
        return bookMapper.toBookDto(book);
    }

    @Override
    public BookDto updateBookById(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot update book by id: " + id));
        bookMapper.updateBookFromDto(requestDto, book);
        Book updatedBook = bookRepository.save(book);
        return bookMapper.toBookDto(updatedBook);
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot delete book by id: " + id));
        bookRepository.deleteById(id);
    }

    @Override
    public Page<BookDto> search(BookSearchParameters searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder
                .buildSpecification(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable)
                .map(bookMapper::toBookDto);
    }
}

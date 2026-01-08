package mate.academy.service.book;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.book.BookMapper;
import mate.academy.model.book.Book;
import mate.academy.model.book.Category;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.book.BookSearchParameters;
import mate.academy.repository.book.BookSpecificationBuilder;
import mate.academy.repository.book.category.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toEntity(requestDto);
        if (requestDto.getCategoryIds() != null && !requestDto.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>(
                    categoryRepository.findAllById(requestDto.getCategoryIds()));
            if (categories.size() != requestDto.getCategoryIds().size()) {
                throw new EntityNotFoundException("Some categories not found");
            }
            book.setCategories(categories);
        }
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find book by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateBookById(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot update book by id: " + id));
        bookMapper.updateBookFromDto(requestDto, book);
        if (requestDto.getCategoryIds() != null) {
            if (requestDto.getCategoryIds().isEmpty()) {
                book.getCategories().clear();
            } else {
                Set<Category> categories = new HashSet<>(
                        categoryRepository.findAllById(requestDto.getCategoryIds()));
                if (categories.size() != requestDto.getCategoryIds().size()) {
                    throw new EntityNotFoundException("Some categories not found");
                }
                book.getCategories().clear();
                book.setCategories(categories);
            }

        }
        bookMapper.updateBookFromDto(requestDto, book);
        Book updatedBook = bookRepository.save(book);
        return bookMapper.toDto(updatedBook);
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
                .map(bookMapper::toDto);
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id, Pageable pageable) {
        return bookRepository.findAllByCategories_Id(id, pageable)
                .map(bookMapper::toDtoWithoutCategories);
    }
}

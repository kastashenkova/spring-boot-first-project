package mate.academy.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
import mate.academy.util.TestUtil;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
class BookServiceTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Test
    @DisplayName("Create a new Book")
    void save_newBook_ReturnsNewBook() throws BadRequestException {
        // given
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("I See You Are Interested in Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-847-6")
                .setPrice(BigDecimal.valueOf(15.99));

        Book bookWithoutId = new Book();
        bookWithoutId.setTitle(requestDto.getTitle());
        bookWithoutId.setAuthor(requestDto.getAuthor());
        bookWithoutId.setIsbn(requestDto.getIsbn());
        bookWithoutId.setPrice(requestDto.getPrice());

        Book saved = new Book()
                .setId(1L)
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice());

        BookDto expected = TestUtil.bookDtoISeeYouAreInterestedInDarkness();

        when(bookMapper.toEntity(requestDto)).thenReturn(bookWithoutId);
        when(bookRepository.save(bookWithoutId)).thenReturn(saved);
        when(bookMapper.toDto(any(Book.class))).thenReturn(expected);

        // when
        BookDto actual = bookService.save(requestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id", "categoryIds")
        );
        assertTrue(
                actual.getCategoryIds().containsAll(expected.getCategoryIds())
                        && expected.getCategoryIds().containsAll(actual.getCategoryIds())
        );

        verify(bookRepository, times(1)).save(bookWithoutId);
    }

    @Test
    @DisplayName("Create Book without title")
    void save_bookWithoutTitle_BadRequest() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setAuthor("New Author")
                .setIsbn("New-isbn")
                .setPrice(BigDecimal.TEN)
                .setCategoryIds(null);

       assertThrows(BadRequestException.class,
                () -> bookService.save(requestDto));

       verify(bookRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Find all books in database")
    void findAll_twoBooks_ReturnsAllBooks() {
        BookDto bookDtoISeeYouAreInterestedInDarkness
                = TestUtil.bookDtoISeeYouAreInterestedInDarkness();
        Book ISeeYouAreInterestedInDarkness
                = new Book()
                .setTitle(bookDtoISeeYouAreInterestedInDarkness.getTitle())
                .setAuthor(bookDtoISeeYouAreInterestedInDarkness.getAuthor())
                .setIsbn(bookDtoISeeYouAreInterestedInDarkness.getIsbn())
                .setPrice(bookDtoISeeYouAreInterestedInDarkness.getPrice());

        BookDto bookDtoTheFoolsDance
                = TestUtil.bookDtoTheFoolsDance();
        Book theFoolsDance
                = new Book()
                .setTitle(bookDtoTheFoolsDance.getTitle())
                .setAuthor(bookDtoTheFoolsDance.getAuthor())
                .setIsbn(bookDtoTheFoolsDance.getIsbn())
                .setPrice(bookDtoTheFoolsDance.getPrice());

        List<Book> books = List.of(ISeeYouAreInterestedInDarkness, theFoolsDance);
        Page<Book> page = new PageImpl<>(books);
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findAll(pageable)).thenReturn(page);
        when(bookMapper.toDto(theFoolsDance))
                .thenReturn(bookDtoTheFoolsDance);
        when(bookMapper.toDto(ISeeYouAreInterestedInDarkness))
                .thenReturn(bookDtoISeeYouAreInterestedInDarkness);

        Page<BookDto> actual = bookService.getAll(pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());
        verify(bookRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Get existing book by its id")
    void getBookById_existingBook_ReturnsTheBook() {
        BookDto bookDtoISeeYouAreInterestedInDarkness
                = TestUtil.bookDtoISeeYouAreInterestedInDarkness();
        Book ISeeYouAreInterestedInDarkness
                = new Book()
                .setTitle(bookDtoISeeYouAreInterestedInDarkness.getTitle())
                .setAuthor(bookDtoISeeYouAreInterestedInDarkness.getAuthor())
                .setIsbn(bookDtoISeeYouAreInterestedInDarkness.getIsbn())
                .setPrice(bookDtoISeeYouAreInterestedInDarkness.getPrice());

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(ISeeYouAreInterestedInDarkness));
        when(bookMapper.toDto(ISeeYouAreInterestedInDarkness))
                .thenReturn(bookDtoISeeYouAreInterestedInDarkness);

        BookDto actual = bookService.getBookById(1L);

        assertNotNull(actual);
        verify(bookRepository, times(1))
                .findById(anyLong());
    }

    @Test
    @DisplayName("Returns error in case of non-existing book")
    void getBookById_nonExistingBook_ThrowsException() {
        Long nonExistingBookId = 9999L;

        assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(nonExistingBookId));

        verify(bookRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Update an existing book - Unit Test")
    void updateBookById_existingBook_ReturnsUpdatedBook() {
        Long bookId = 1L;

        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Old Title");

        CreateBookRequestDto updateDto = new CreateBookRequestDto()
                .setTitle("New Title")
                .setAuthor("New Author")
                .setIsbn("978-000-000-000-0")
                .setPrice(BigDecimal.valueOf(20.99));

        Book savedBook = new Book();
        savedBook.setId(bookId);
        savedBook.setTitle(updateDto.getTitle());

        BookDto expectedDto = new BookDto()
                .setId(bookId)
                .setTitle(updateDto.getTitle());

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        when(bookMapper.toDto(any(Book.class))).thenReturn(expectedDto);

        BookDto actual = bookService.updateBookById(bookId, updateDto);

        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expectedDto, actual)
        );

        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Returns error in case of trying to update non-existing book")
    void updateBookById_nonExistingBook_ThrowsException() {
        Long nonExistingBookId = 9999L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("New Book")
                .setAuthor("New Author")
                .setIsbn("New-isbn")
                .setPrice(BigDecimal.TEN);

        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBookById(nonExistingBookId, requestDto));

        verify(bookRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Returns error in case of trying to update book with non-existing category")
    void updateBookById_nonExistingCategory_ThrowsException() throws BadRequestException {
        Long nonExistingBookId = 9999L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("New Book")
                .setAuthor("New Author")
                .setIsbn("New-isbn")
                .setPrice(BigDecimal.TEN)
                .setCategoryIds(List.of(nonExistingBookId));

        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBookById(nonExistingBookId, requestDto));

        verify(bookRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Delete existing book by its id")
    void deleteBookById_existingBook_Success() {
        BookDto bookDtoISeeYouAreInterestedInDarkness
                = TestUtil.bookDtoISeeYouAreInterestedInDarkness();
        Book ISeeYouAreInterestedInDarkness
                = new Book()
                .setTitle(bookDtoISeeYouAreInterestedInDarkness.getTitle())
                .setAuthor(bookDtoISeeYouAreInterestedInDarkness.getAuthor())
                .setIsbn(bookDtoISeeYouAreInterestedInDarkness.getIsbn())
                .setPrice(bookDtoISeeYouAreInterestedInDarkness.getPrice());

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(ISeeYouAreInterestedInDarkness));

        bookService.deleteBookById(1L);

        verify(bookRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("Returns error in case of trying to delete non-existing book")
    void deleteBookById_nonExistingBook_ThrowsException() {
        Long nonExistingBookId = 9999L;

        assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteBookById(nonExistingBookId));

        verify(bookRepository, times(0))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("Search books by the same authors")
    void search_byAuthors_ReturnsBooks() {
        BookDto bookDtoISeeYouAreInterestedInDarkness
                = TestUtil.bookDtoISeeYouAreInterestedInDarkness();
        Book iSeeYouAreInterestedInDarkness
                = new Book()
                .setTitle(bookDtoISeeYouAreInterestedInDarkness.getTitle())
                .setAuthor(bookDtoISeeYouAreInterestedInDarkness.getAuthor())
                .setIsbn(bookDtoISeeYouAreInterestedInDarkness.getIsbn())
                .setPrice(bookDtoISeeYouAreInterestedInDarkness.getPrice());

        CreateBookRequestDto createBookRequestDtoTangoOfDeath
                = TestUtil.createBookDtoTangoOfDeath();
        Book tangoOfDeath
                = new Book()
                .setTitle(createBookRequestDtoTangoOfDeath.getTitle())
                .setAuthor(createBookRequestDtoTangoOfDeath.getAuthor())
                .setIsbn(createBookRequestDtoTangoOfDeath.getIsbn())
                .setPrice(createBookRequestDtoTangoOfDeath.getPrice());
        BookDto bookDtoTangoOfDeath
                = new BookDto()
                .setTitle(createBookRequestDtoTangoOfDeath.getTitle())
                .setAuthor(createBookRequestDtoTangoOfDeath.getAuthor())
                .setIsbn(createBookRequestDtoTangoOfDeath.getIsbn())
                .setPrice(createBookRequestDtoTangoOfDeath.getPrice());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(
                List.of(iSeeYouAreInterestedInDarkness, tangoOfDeath), pageable, 2
        );

        String[] requiredAuthors = {"Illarion Pavliuk", "Yurii Vynnychuk"};
        BookSearchParameters searchParameters
                = new BookSearchParameters(null, requiredAuthors);
        Specification<Book> bookSpecification = mock(Specification.class); // stub

        when(bookSpecificationBuilder.buildSpecification(searchParameters))
                .thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification, pageable))
                .thenReturn(bookPage);
        when(bookMapper.toDto(iSeeYouAreInterestedInDarkness))
                .thenReturn(bookDtoISeeYouAreInterestedInDarkness);
        when(bookMapper.toDto(tangoOfDeath))
                .thenReturn(bookDtoTangoOfDeath);

        Page<BookDto> actual = bookService.search(searchParameters, pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());

        verify(bookSpecificationBuilder, times(1))
                .buildSpecification(searchParameters);
        verify(bookRepository, times(1))
                .findAll(bookSpecification, pageable);
    }

    @Test
    @DisplayName("Search books by the same titles")
    void search_byTitles_ReturnsBooks() {
        BookDto bookDtoISeeYouAreInterestedInDarkness
                = TestUtil.bookDtoISeeYouAreInterestedInDarkness();
        Book iSeeYouAreInterestedInDarkness
                = new Book()
                .setTitle(bookDtoISeeYouAreInterestedInDarkness.getTitle())
                .setAuthor(bookDtoISeeYouAreInterestedInDarkness.getAuthor())
                .setIsbn(bookDtoISeeYouAreInterestedInDarkness.getIsbn())
                .setPrice(bookDtoISeeYouAreInterestedInDarkness.getPrice());

        CreateBookRequestDto createBookRequestDtoTangoOfDeath
                = TestUtil.createBookDtoTangoOfDeath();
        Book tangoOfDeath
                = new Book()
                .setTitle(createBookRequestDtoTangoOfDeath.getTitle())
                .setAuthor(createBookRequestDtoTangoOfDeath.getAuthor())
                .setIsbn(createBookRequestDtoTangoOfDeath.getIsbn())
                .setPrice(createBookRequestDtoTangoOfDeath.getPrice());
        BookDto bookDtoTangoOfDeath
                = new BookDto()
                .setTitle(createBookRequestDtoTangoOfDeath.getTitle())
                .setAuthor(createBookRequestDtoTangoOfDeath.getAuthor())
                .setIsbn(createBookRequestDtoTangoOfDeath.getIsbn())
                .setPrice(createBookRequestDtoTangoOfDeath.getPrice());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(
                List.of(iSeeYouAreInterestedInDarkness, tangoOfDeath), pageable, 2
        );

        String[] requiredTitles = {"I See You Are Interested in Darkness",
                "Tango of Death"};
        BookSearchParameters searchParameters
                = new BookSearchParameters(requiredTitles, null);
        Specification<Book> bookSpecification = mock(Specification.class); // stub

        when(bookSpecificationBuilder.buildSpecification(searchParameters))
                .thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification, pageable))
                .thenReturn(bookPage);
        when(bookMapper.toDto(iSeeYouAreInterestedInDarkness))
                .thenReturn(bookDtoISeeYouAreInterestedInDarkness);
        when(bookMapper.toDto(tangoOfDeath))
                .thenReturn(bookDtoTangoOfDeath);

        Page<BookDto> actual = bookService.search(searchParameters, pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());

        verify(bookSpecificationBuilder, times(1))
                .buildSpecification(searchParameters);
        verify(bookRepository, times(1))
                .findAll(bookSpecification, pageable);
    }

    @Test
    @DisplayName("Find all books in one category")
    void findAllByCategoryId_fantasyBooks_ReturnsBooks() {
        Category fantasy = new Category()
                .setId(1L)
                .setName("Fantasy");

        CreateBookRequestDto createBookDtoTheBookOfEmil = TestUtil.createBookDtoTheBookOfEmil();
        Book theBookOfEmil = new Book()
                .setTitle(createBookDtoTheBookOfEmil.getTitle())
                .setAuthor(createBookDtoTheBookOfEmil.getAuthor())
                .setIsbn(createBookDtoTheBookOfEmil.getIsbn())
                .setPrice(createBookDtoTheBookOfEmil.getPrice());

        CreateBookRequestDto createBookRequestDtoTangoOfDeath = TestUtil.createBookDtoTangoOfDeath();
        Book tangoOfDeath = new Book()
                .setTitle(createBookRequestDtoTangoOfDeath.getTitle())
                .setAuthor(createBookRequestDtoTangoOfDeath.getAuthor())
                .setIsbn(createBookRequestDtoTangoOfDeath.getIsbn())
                .setPrice(createBookRequestDtoTangoOfDeath.getPrice());

        BookDtoWithoutCategoryIds dtoTheBookOfEmil = new BookDtoWithoutCategoryIds()
                .setTitle(createBookDtoTheBookOfEmil.getTitle())
                .setAuthor(createBookDtoTheBookOfEmil.getAuthor())
                .setIsbn(createBookDtoTheBookOfEmil.getIsbn())
                .setPrice(createBookDtoTheBookOfEmil.getPrice());

        BookDtoWithoutCategoryIds dtoTangoOfDeath = new BookDtoWithoutCategoryIds()
                .setTitle(createBookRequestDtoTangoOfDeath.getTitle())
                .setAuthor(createBookRequestDtoTangoOfDeath.getAuthor())
                .setIsbn(createBookRequestDtoTangoOfDeath.getIsbn())
                .setPrice(createBookRequestDtoTangoOfDeath.getPrice());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(theBookOfEmil, tangoOfDeath), pageable, 2);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(fantasy));
        when(bookRepository.findAllByCategories_Id(1L, pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(theBookOfEmil)).thenReturn(dtoTheBookOfEmil);
        when(bookMapper.toDtoWithoutCategories(tangoOfDeath)).thenReturn(dtoTangoOfDeath);

        Page<BookDtoWithoutCategoryIds> actual = bookService.getAllByCategoryId(1L, pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());

        verify(categoryRepository, times(1)).findById(1L);
        verify(bookRepository, times(1))
                .findAllByCategories_Id(1L, pageable);
    }
}

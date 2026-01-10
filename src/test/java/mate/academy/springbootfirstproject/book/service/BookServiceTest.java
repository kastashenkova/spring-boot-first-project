package mate.academy.springbootfirstproject.book.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.book.Category;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.book.BookSearchParameters;
import mate.academy.repository.book.category.CategoryRepository;
import mate.academy.service.book.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Create a new Book")
    void save_newBook_ReturnsNewBook() {
        Category history = categoryRepository.save(new Category().setName("history"));
        Category poetry = categoryRepository.save(new Category().setName("poetry"));

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("New Book")
                .setAuthor("New Author")
                .setIsbn("New-isbn")
                .setCategoryIds(List.of(history.getId(), poetry.getId()));

        BookDto actual = bookService.save(requestDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals("New Book", actual.getTitle());
        Assertions.assertEquals("New Author", actual.getAuthor());
        Assertions.assertEquals("New-isbn", actual.getIsbn());
        Assertions.assertEquals(2, actual.getCategoryIds().size());
    }

    @Test
    @DisplayName("Find all books in database")
    void findAll_threeBooks_ReturnsAllBooks() {
        categoryRepository.save(new Category().setName("fiction"));

        CreateBookRequestDto book1 = new CreateBookRequestDto()
                .setTitle("I See You Are Interested In Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("isbn-1");
        CreateBookRequestDto book2 = new CreateBookRequestDto()
                .setTitle("The Fool's Dance")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("isbn-2");
        CreateBookRequestDto book3 = new CreateBookRequestDto()
                .setTitle("Marusia Churai")
                .setAuthor("Lina Kostenko")
                .setIsbn("isbn-3");

        bookService.save(book1);
        bookService.save(book2);
        bookService.save(book3);

        Page<BookDto> actual = bookService.findAll(Pageable.unpaged());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(3, actual.getTotalElements());
    }

    @Test
    @DisplayName("Get existing book by its id")
    void getBookById_existingBook_ReturnsTheBook() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("I See You Are Interested In Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("isbn-test");

        BookDto savedBook = bookService.save(requestDto);

        BookDto actualBookDto = bookService.getBookById(savedBook.getId());

        Assertions.assertNotNull(actualBookDto);
        Assertions.assertEquals("I See You Are Interested In Darkness", actualBookDto.getTitle());
        Assertions.assertEquals("Illarion Pavliuk", actualBookDto.getAuthor());
    }

    @Test
    @DisplayName("Returns error in case of non-existing book")
    void getBookById_nonExistingBook_ThrowsException() {
        Long nonExistingBookId = 9999L;

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(nonExistingBookId));

        String expectedMessage = "Cannot find book by id: " + nonExistingBookId;
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Update an existing book")
    void updateBookById_existingBook_ReturnsUpdatedBook() {
        CreateBookRequestDto createDto = new CreateBookRequestDto()
                .setTitle("Original Book")
                .setAuthor("Original Author")
                .setIsbn("original-isbn");

        BookDto savedBook = bookService.save(createDto);

        CreateBookRequestDto updateDto = new CreateBookRequestDto()
                .setTitle("Updated Book")
                .setAuthor("Updated Author")
                .setIsbn("updated-isbn");

        BookDto updatedBook = bookService.updateBookById(savedBook.getId(), updateDto);

        Assertions.assertNotNull(updatedBook);
        Assertions.assertEquals("Updated Book", updatedBook.getTitle());
        Assertions.assertEquals("Updated Author", updatedBook.getAuthor());
        Assertions.assertEquals("updated-isbn", updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Returns error in case of trying to update non-existing book")
    void updateBookById_nonExistingBook_ThrowsException() {
        Long nonExistingBookId = 9999L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("New Book")
                .setAuthor("New Author")
                .setIsbn("New-isbn");

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBookById(nonExistingBookId, requestDto));

        String expectedMessage = "Cannot update book by id: " + nonExistingBookId;
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Returns error in case of trying to update book with non-existing category")
    void updateBookById_nonExistingCategory_ThrowsException() {
        CreateBookRequestDto createDto = new CreateBookRequestDto()
                .setTitle("Book")
                .setAuthor("Author")
                .setIsbn("isbn");

        BookDto savedBook = bookService.save(createDto);

        Long nonExistingCategoryId = 9999L;
        CreateBookRequestDto updateDto = new CreateBookRequestDto()
                .setTitle("Updated Book")
                .setAuthor("Updated Author")
                .setIsbn("updated-isbn")
                .setCategoryIds(List.of(nonExistingCategoryId));

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBookById(savedBook.getId(), updateDto));

        String expectedMessage = "Some categories not found";
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Delete existing book by its id")
    void deleteBookById_existingBook_Success() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("I See You Are Interested In Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("isbn-test");

        BookDto savedBook = bookService.save(requestDto);
        Long bookId = savedBook.getId();

        bookService.deleteBookById(bookId);

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(bookId));

        Assertions.assertTrue(exception.getMessage().contains("Cannot find book by id"));
    }

    @Test
    @DisplayName("Returns error in case of trying to delete non-existing book")
    void deleteBookById_nonExistingBook_ThrowsException() {
        Long nonExistingBookId = 9999L;

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteBookById(nonExistingBookId));

        String expectedMessage = "Cannot delete book by id: " + nonExistingBookId;
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Search books by the same authors")
    void search_byAuthors_ReturnsBooks() {
        CreateBookRequestDto book1 = new CreateBookRequestDto()
                .setTitle("I See You Are Interested in Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-847-6");

        CreateBookRequestDto book2 = new CreateBookRequestDto()
                .setTitle("The Fool's Dance")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-921-3");

        CreateBookRequestDto book3 = new CreateBookRequestDto()
                .setTitle("Marusia Churai")
                .setAuthor("Lina Kostenko")
                .setIsbn("978-000-000000-5");

        CreateBookRequestDto book4 = new CreateBookRequestDto()
                .setTitle("The City")
                .setAuthor("Myhail Semenko")
                .setIsbn("978-617-679-840-6");

        bookService.save(book1);
        bookService.save(book2);
        bookService.save(book3);
        bookService.save(book4);

        String[] requiredAuthors = {"Illarion Pavliuk", "Lina Kostenko"};
        BookSearchParameters searchParameters = new BookSearchParameters(null, requiredAuthors);

        Page<BookDto> actualPage = bookService.search(searchParameters, Pageable.unpaged());

        Assertions.assertNotNull(actualPage);
        Assertions.assertEquals(3, actualPage.getTotalElements());
    }

    @Test
    @DisplayName("Search books by the same titles")
    void search_byTitles_ReturnsBooks() {
        CreateBookRequestDto book1 = new CreateBookRequestDto()
                .setTitle("I See You Are Interested in Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-847-6");

        CreateBookRequestDto book2 = new CreateBookRequestDto()
                .setTitle("Marusia Churai")
                .setAuthor("Lina Kostenko")
                .setIsbn("978-000-000000-5");

        CreateBookRequestDto book3 = new CreateBookRequestDto()
                .setTitle("The City")
                .setAuthor("Myhail Semenko")
                .setIsbn("978-617-679-840-6");

        CreateBookRequestDto book4 = new CreateBookRequestDto()
                .setTitle("The City")
                .setAuthor("Valerian Pidmohylnyi")
                .setIsbn("978-0000000001");

        bookService.save(book1);
        bookService.save(book2);
        bookService.save(book3);
        bookService.save(book4);

        String[] requiredTitles = {"The City", "Marusia Churai"};
        BookSearchParameters searchParameters = new BookSearchParameters(requiredTitles, null);

        Page<BookDto> actualPage = bookService.search(searchParameters, Pageable.unpaged());

        Assertions.assertNotNull(actualPage);
        Assertions.assertEquals(3, actualPage.getTotalElements());
    }

    @Test
    @DisplayName("Find all books in one category")
    void findAllByCategoryId_fantasyBooks_ReturnsBooks() {
        Category fantasy = categoryRepository.save(new Category().setName("fantasy"));
        Category history = categoryRepository.save(new Category().setName("history"));

        CreateBookRequestDto book1 = new CreateBookRequestDto()
                .setTitle("I See You Are Interested in Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-847-6")
                .setCategoryIds(List.of(fantasy.getId()));

        CreateBookRequestDto book2 = new CreateBookRequestDto()
                .setTitle("The Fool's Dance")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-921-3")
                .setCategoryIds(List.of(fantasy.getId()));

        CreateBookRequestDto book3 = new CreateBookRequestDto()
                .setTitle("Marusia Churai")
                .setAuthor("Lina Kostenko")
                .setIsbn("978-000-000000-5")
                .setCategoryIds(List.of(history.getId()));

        bookService.save(book1);
        bookService.save(book2);
        bookService.save(book3);

        Page<BookDtoWithoutCategoryIds> actualPage =
                bookService.findAllByCategoryId(fantasy.getId(), Pageable.unpaged());

        Assertions.assertNotNull(actualPage);
        Assertions.assertEquals(2, actualPage.getTotalElements());
    }
}

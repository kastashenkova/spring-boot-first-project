package mate.academy.springbootfirstproject.book.repository;

import java.util.Optional;
import mate.academy.model.book.Book;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.book.BookSearchParameters;
import mate.academy.repository.book.BookSpecificationBuilder;
import mate.academy.repository.book.BookSpecificationProviderManager;
import mate.academy.repository.book.specification.AuthorSpecificationProvider;
import mate.academy.repository.book.specification.TitleSpecificationProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        BookSpecificationProviderManager.class,
        AuthorSpecificationProvider.class,
        TitleSpecificationProvider.class
})
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookSpecificationProviderManager bookSpecificationProviderManager;

    @Test
    @DisplayName("""
            Should return all books with pagination
            """)
    @Sql(scripts = "classpath:database/books/add-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_fiveBooks_ReturnsAllBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> actual = bookRepository.findAll(pageable);
        Assertions.assertEquals(5, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return all books of the specific author with pagination
            """)
    @Sql(scripts = "classpath:database/books/add-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllWithSpec_booksOfOneAuthor_ReturnsAllBooksOfTheSameAuthor() {
        String[] requiredAuthor = {"Illarion Pavliuk"};
        BookSearchParameters searchParameter
                = new BookSearchParameters(null, requiredAuthor);
        BookSpecificationBuilder bookSpecificationBuilder
                = new BookSpecificationBuilder(bookSpecificationProviderManager);
        Specification<Book> bookSpecification = bookSpecificationBuilder
                .buildSpecification(searchParameter);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> actual = bookRepository.findAll(bookSpecification, pageable);
        Assertions.assertEquals(2, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return all books with the specific title with pagination
            """)
    @Sql(scripts = "classpath:database/books/add-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllWithSpec_booksOfOneTitle_ReturnsAllBooksWithTheSameTitle() {
        String[] requiredTitle = {"The City"};
        BookSearchParameters searchParameter
                = new BookSearchParameters(requiredTitle, null);
        BookSpecificationBuilder bookSpecificationBuilder
                = new BookSpecificationBuilder(bookSpecificationProviderManager);
        Specification<Book> bookSpecification = bookSpecificationBuilder
                .buildSpecification(searchParameter);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> actual = bookRepository.findAll(bookSpecification, pageable);
        Assertions.assertEquals(2, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return all books within specific category with pagination
            """)
    @Sql(scripts = "classpath:database/books/add-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/add-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/add-books-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategories_Id_fantasyCategory_ReturnsAllBooksWithinSpecificCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> actual = bookRepository.findAllByCategories_Id(1L, pageable);
        Assertions.assertEquals(2, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return information about the specific book by its id
            """)
    @Sql(scripts = "classpath:database/books/add-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById_book_ReturnsBookInfoByItsId() {
        Book historyOfUkraine = new Book()
                .setTitle("Ukraine: A History")
                .setAuthor("Orest Subtelny")
                .setIsbn("978-080-205-809-6");
        Book saved = bookRepository.save(historyOfUkraine);
        Optional<Book> actual = bookRepository.findById(saved.getId());
        Assertions.assertTrue(actual.isPresent());
        Optional<Book> expected = Optional.of(historyOfUkraine);
        Assertions.assertEquals(expected.get().getTitle(), actual.get().getTitle());
    }
}

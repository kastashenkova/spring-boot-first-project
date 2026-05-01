package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.util.TestUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @Sql(scripts = "classpath:database/books/add-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return all available books
           """)
    void getAll_fiveBooks_ReturnsAllFiveBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<BookDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, BookDto.class)
        );

        assertNotNull(actualList);
        assertEquals(5, actualList.size());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @Sql(scripts = "classpath:database/books/add-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return specific book by its id
           """)
    void getBookById_secondBook_ReturnsTheSecondBookInDto() throws Exception {
        BookDto expected = TestUtil.bookDtoTheFoolsDance();

        MvcResult result = mockMvc.perform(get("/books/{id}",
                        2L))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        BookDto actual = objectMapper.readValue(
                json,
                BookDto.class
        );

        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id", "categoryIds")
        );
        assertTrue(
                actual.getCategoryIds().containsAll(expected.getCategoryIds())
                        && expected.getCategoryIds().containsAll(actual.getCategoryIds())
        );
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/add-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/delete-books-categories-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/delete-categories-from-categories-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Create a new Book
           """)
    void createBook_validRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("The White Ashes")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-921-6")
                .setPrice(BigDecimal.TEN)
                .setCategoryIds(List.of(1L, 2L, 3L));
        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setCategoryIds(requestDto.getCategoryIds());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id", "categoryIds")
        );
        assertTrue(
                actual.getCategoryIds().containsAll(expected.getCategoryIds())
                        && expected.getCategoryIds().containsAll(actual.getCategoryIds())
        );
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/delete-books-categories-table.sql",
            "classpath:database/books/delete-categories-from-categories-table.sql",
            "classpath:database/books/add-categories-to-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/delete-books-categories-table.sql",
            "classpath:database/books/delete-categories-from-categories-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Create Book without author
           """)
    void createBook_bookRequestDtoWithoutAuthor_BadRequest() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("The White Ashes")
                .setIsbn("978-617-679-921-6")
                .setPrice(BigDecimal.TEN)
                .setCategoryIds(List.of(1L, 2L, 3L));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/add-books-categories-table.sql",
            "classpath:database/books/add-categories-to-categories-table.sql",
            "classpath:database/books/add-books-to-books-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/delete-books-categories-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/delete-categories-from-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Update existing book by its id
           """)
    void updateBookById_validRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("I See You Are Interested in Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-600-000-0")
                .setPrice(BigDecimal.TEN)
                .setCategoryIds(List.of(1L));
        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setCategoryIds(requestDto.getCategoryIds());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/books/{id}", 1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Update Book which does not exist in the database (has wrong id)
           """)
    void updateBookById_invalidRequestDto_NotFound() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("I See You Are Interested in Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-600-000-0")
                .setPrice(BigDecimal.TEN)
                .setCategoryIds(List.of(1L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/{id}", 10L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/add-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Delete existing book by its id
           """)
    void deleteBookById_validRequestDto_Success() throws Exception {
        mockMvc.perform(delete("/books/{id}",
                        2L))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Delete Book which does not exist in the database (has wrong id)
           """)
    void deleteBookById_invalidRequestDto_NotFound() throws Exception {
        mockMvc.perform(delete("/books/{id}",
                        10L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = "classpath:database/books/add-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Search books by authors")
    void search_sameAuthor_ReturnsAllBookWithTheSameAuthor() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("authors", "Illarion Pavliuk"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<BookDto> actualBooks = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, BookDto.class)
        );

        assertEquals(2, actualBooks.size());
        assertEquals("Illarion Pavliuk", actualBooks.get(0).getAuthor());
        assertEquals("Illarion Pavliuk", actualBooks.get(1).getAuthor());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = "classpath:database/books/add-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Search books by authors")
    void search_sameTitle_ReturnsAllBookWithTheSameTitle() throws Exception {

        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("titles", "The City"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<BookDto> actualBooks = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, BookDto.class)
        );

        assertEquals(2, actualBooks.size());
        assertEquals("The City", actualBooks.get(0).getTitle());
        assertEquals("The City", actualBooks.get(1).getTitle());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Search books by authors which do not exist in the database")
    void search_notExistingAuthor_ReturnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("authors", "Serhii Zhadan"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<BookDto> actualBooks = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, BookDto.class)
        );

        assertEquals(0, actualBooks.size());
    }
}

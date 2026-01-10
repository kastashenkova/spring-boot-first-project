package mate.academy.springbootfirstproject.book.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.repository.book.BookSearchParameters;
import mate.academy.service.book.BookService;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("""
           Should return all available books
           """)
    void getAll_fiveBooks_ReturnsAllFiveBooks() throws Exception {
        BookDto interestedInDarkness = new BookDto()
                .setTitle("I See You Are Interested in Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-847-6");

        BookDto foolDance = new BookDto()
                .setTitle("The Fool's Dance")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-921-3");

        BookDto marusiaChurai = new BookDto()
                .setTitle("Marusia Churai")
                .setAuthor("Lina Kostenko")
                .setIsbn("978-000-000000-5");

        BookDto cityPidmohylnyi = new BookDto()
                .setTitle("The City")
                .setAuthor("Valerian Pidmohylnyi")
                .setIsbn("978-0000000001");

        BookDto citySemenko = new BookDto()
                .setTitle("The City")
                .setAuthor("Myhail Semenko")
                .setIsbn("978-617-679-840-6");

        List<BookDto> books = List.of(interestedInDarkness, foolDance, marusiaChurai,
                cityPidmohylnyi, citySemenko);
        Page<BookDto> expectedPage = new PageImpl<>(books);
        when(bookService.findAll(any(Pageable.class))).thenReturn(expectedPage);

        MvcResult result = mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(expectedPage);
        assertEquals(expectedJson, actualJson);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("""
           Should return specific book by its id
           """)
    void getBookById_secondBook_ReturnsTheSecondBookInDto() throws Exception {
        BookDto expected = new BookDto()
                .setTitle("The Fool's Dance")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-921-3");
        when(bookService.getBookById(2L)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/api/books/{id}",
                        2L))
                .andExpect(status().isOk())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(expected);
        assertEquals(expectedJson, actualJson);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Create a new Book
           """)
    void createBook_validRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("The White Ashes")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-921-6");
        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        when(bookService.save(any(CreateBookRequestDto.class)))
                .thenReturn(
                        new BookDto()
                                .setId(1L)
                                .setTitle(requestDto.getTitle())
                                .setAuthor(requestDto.getAuthor())
                                .setIsbn(requestDto.getIsbn())
                );

        MvcResult result = mockMvc.perform(post("/api/books")
                        .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Update existing book by its id
           """)
    void updateBookById_validRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("I See You Are Interested in Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-600-000-0");
        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        when(bookService.updateBookById(eq(1L), any(CreateBookRequestDto.class)))
                .thenReturn(
                        new BookDto()
                                .setId(1L)
                                .setTitle(requestDto.getTitle())
                                .setAuthor(requestDto.getAuthor())
                                .setIsbn(requestDto.getIsbn())
                );


        MvcResult result = mockMvc.perform(put("/api/books/{id}", 1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Delete existing book by its id
           """)
    void deleteBookById_validRequestDto_Success() throws Exception {
        BookDto expected = new BookDto()
                .setTitle("The Fool's Dance")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-921-3");
        when(bookService.getBookById(2L)).thenReturn(expected);

        mockMvc.perform(delete("/api/books/{id}",
                        2L))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("""
           Search books by authors
           """)
    void search_sameAuthor_ReturnsAllBookWithTheSameAuthor() throws Exception {
        BookDto interestedInDarkness = new BookDto()
                .setTitle("I See You Are Interested in Darkness")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-847-6");

        BookDto foolDance = new BookDto()
                .setTitle("The Fool's Dance")
                .setAuthor("Illarion Pavliuk")
                .setIsbn("978-617-679-921-3");

        List<BookDto> books = List.of(interestedInDarkness, foolDance);
        Page<BookDto> expectedPage = new PageImpl<>(books);
        when(bookService.search(any(BookSearchParameters.class),
                any(Pageable.class))).thenReturn(expectedPage);

        MvcResult result = mockMvc.perform(get("/api/books/search")
                        .param("authors", "Illarion Pavliuk"))
                .andExpect(status().isOk())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(expectedPage);
        assertEquals(expectedJson, actualJson);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("""
           Search books by titles
           """)
    void search_sameTitle_ReturnsAllBookWithTheSameTitle() throws Exception {
        BookDto cityPidmohylnyi = new BookDto()
                .setTitle("The City")
                .setAuthor("Valerian Pidmohylnyi")
                .setIsbn("978-0000000001");

        BookDto citySemenko = new BookDto()
                .setTitle("The City")
                .setAuthor("Myhail Semenko")
                .setIsbn("978-617-679-840-6");

        List<BookDto> books = List.of(cityPidmohylnyi, citySemenko);
        Page<BookDto> expectedPage = new PageImpl<>(books);
        when(bookService.search(any(BookSearchParameters.class),
                any(Pageable.class))).thenReturn(expectedPage);

        MvcResult result = mockMvc.perform(get("/api/books/search")
                        .param("titles", "The City"))
                .andExpect(status().isOk())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(expectedPage);
        assertEquals(expectedJson, actualJson);
    }
}

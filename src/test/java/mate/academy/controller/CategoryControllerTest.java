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

import java.util.List;
import mate.academy.dto.book.CategoryDto;
import mate.academy.dto.book.CategoryRequestDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.service.book.BookService;
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
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookService bookService;

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @Sql(scripts = "classpath:database/books/add-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return all available categories
           """)
    void getAll_fourCategories_ReturnsAllFourCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<CategoryDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, CategoryDto.class)
        );

        assertNotNull(actualList);
        assertEquals(3, actualList.size());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @Sql(scripts = "classpath:database/books/add-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return specific category by its id
           """)
    void getCategoryById_firstCategory_ReturnsTheThirdCategoryInDto() throws Exception {
        CategoryDto expected = TestUtil.categoryDtoFantasy();

        MvcResult result = mockMvc.perform(get("/categories/{id}",
                        1L))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        CategoryDto actual = objectMapper.readValue(
                json,
                CategoryDto.class
        );

        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Create a new Category
           """)
    void createCategory_validRequestDto_Success() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setName("story");
        CategoryDto expected = new CategoryDto()
                .setName(requestDto.getName());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Create empty Category
           """)
    void createCategory_emptyCategoryRequestDto_BadRequest() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/add-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Update existing category by its id
           """)
    void updateCategory_validRequestDto_Success() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setName("science fiction");
        CategoryDto expected = new CategoryDto()
                .setName(requestDto.getName());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/categories/{id}",
                        1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Update Category which does not exist in the database (has wrong id)
           """)
    void updateCategoryById_invalidRequestDto_NotFound() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Fantasy");
        requestDto.setDescription("Fantasy books");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/categories/{id}", 10L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/add-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Delete existing Category by its id
           """)
    void deleteCategory_validRequestDto_Success() throws Exception {
        mockMvc.perform(delete("/categories/{id}",
                        2L))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Delete Category which does not exist in the database (has wrong id)
           """)
    void deleteBookById_invalidRequestDto_NotFound() throws Exception {
        mockMvc.perform(delete("/categories/{id}",
                        10L))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @Sql(scripts = "classpath:database/books/add-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/delete-books-categories-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/delete-categories-from-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return all available books within
           specific category by its id
           """)
    void getBooksByCategoryId_twoFantasyBooks_ReturnsTwoBooksInDto()
            throws Exception {
        CreateBookRequestDto bookOfEmil = TestUtil.createBookDtoTheBookOfEmil();
        bookService.save(bookOfEmil);

        CreateBookRequestDto bookTangoOfDeath = TestUtil.createBookDtoTangoOfDeath();
        bookService.save(bookTangoOfDeath);

        MvcResult result = mockMvc.perform(get("/categories/{id}/books",
                        1L))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<CategoryDto> actualBooks = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, CategoryDto.class)
        );

        assertEquals(2, actualBooks.size());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Search books in the Category which does not exist in the database (has wrong id)
           """)
    void getBooksByCategoryId_invalidRequestDto_NotFound() throws Exception {
        mockMvc.perform(get("/categories/{id}/books",
                        10L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }
}

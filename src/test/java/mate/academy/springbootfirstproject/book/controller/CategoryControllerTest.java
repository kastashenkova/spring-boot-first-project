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
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CategoryDto;
import mate.academy.dto.book.CategoryRequestDto;
import mate.academy.service.book.BookService;
import mate.academy.service.category.CategoryService;
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

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private BookService bookService;

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("""
           Should return all available categories
           """)
    void getAll_fourCategories_ReturnsAllFourCategories() throws Exception {
        CategoryDto fantasy = new CategoryDto()
                .setName("fantasy");
        CategoryDto history = new CategoryDto()
                .setName("history");
        CategoryDto poetry = new CategoryDto()
                .setName("poetry");
        CategoryDto novel = new CategoryDto()
                .setName("novel");

        List<CategoryDto> categories = List.of(fantasy, history,
                poetry, novel);
        Page<CategoryDto> expectedPage = new PageImpl<>(categories);
        when(categoryService.findAll(any(Pageable.class))).thenReturn(expectedPage);

        MvcResult result = mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(expectedPage);
        assertEquals(expectedJson, actualJson);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("""
           Should return specific category by its id
           """)
    void getCategoryById_thirdCategory_ReturnsTheThirdCategoryInDto() throws Exception {
        CategoryDto expected = new CategoryDto()
                .setName("poetry");
        when(categoryService.getById(3L)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/api/categories/{id}",
                        3L))
                .andExpect(status().isOk())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(expected);
        assertEquals(expectedJson, actualJson);
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

        when(categoryService.save(any(CategoryRequestDto.class)))
                .thenReturn(
                        new CategoryDto()
                                .setName(requestDto.getName())
                );

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Update existing category by its id
           """)
    void updateCategory_validRequestDto_Success() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setName("science fiction");
        CategoryDto expected = new CategoryDto()
                .setName(requestDto.getName());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        when(categoryService.update(eq(1L),
                any(CategoryRequestDto.class)))
                .thenReturn(
                        new CategoryDto()
                                .setName(requestDto.getName())
                );


        MvcResult result = mockMvc.perform(put("/api/categories/{id}",
                        1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto  actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Delete existing category by its id
           """)
    void deleteCategory_validRequestDto_Success() throws Exception {
        CategoryDto expected = new CategoryDto()
                .setName("history");
        when(categoryService.getById(2L)).thenReturn(expected);

        mockMvc.perform(delete("/api/categories/{id}",
                        2L))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("""
           Should return all available books within
           specific category by its id
           """)
    void getBooksByCategoryId_twoHistoryBooks_ReturnsTwoBooksInDto()
            throws Exception {
        BookDtoWithoutCategoryIds marusiaChurai
                = new BookDtoWithoutCategoryIds()
                .setTitle("Marusia Churai")
                .setAuthor("Lina Kostenko")
                .setIsbn("978-000-000000-5");
        BookDtoWithoutCategoryIds historyOfUkraine
                = new BookDtoWithoutCategoryIds()
                .setTitle("Ukraine. A History")
                .setAuthor("Orest Subtelnuy")
                .setIsbn("978-000-3747-5");

        List<BookDtoWithoutCategoryIds> books
                = List.of(marusiaChurai, historyOfUkraine);
        Page<BookDtoWithoutCategoryIds> expectedPage = new PageImpl<>(books);
        when(bookService.findAllByCategoryId(eq(2L), any(Pageable.class)))
                .thenReturn(expectedPage);

        MvcResult result = mockMvc.perform(get("/api/categories/{id}/books",
                        2L))
                .andExpect(status().isOk())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(expectedPage);
        assertEquals(expectedJson, actualJson);
    }
}

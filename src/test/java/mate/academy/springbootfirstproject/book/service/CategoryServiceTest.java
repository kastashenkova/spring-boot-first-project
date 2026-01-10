package mate.academy.springbootfirstproject.book.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import mate.academy.dto.book.*;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.book.Category;
import mate.academy.repository.book.BookSearchParameters;
import mate.academy.repository.book.category.CategoryRepository;
import mate.academy.service.category.CategoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootTest
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Create a new Category")
    void save_newCategory_ReturnsNewCategory() {
        CategoryRequestDto history
                = new CategoryRequestDto().setName("history");
        CategoryDto actual = categoryService.save(history);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("history", actual.getName());
    }

    @Test
    @DisplayName("Find all categories in database")
    void findAll_threeCategories_ReturnsAllCategories() {
        CategoryRequestDto history
                = new CategoryRequestDto().setName("history");
        CategoryRequestDto fantasy
                = new CategoryRequestDto().setName("fantasy");
        CategoryRequestDto poetry
                = new CategoryRequestDto().setName("poetry");
        categoryService.save(history);
        categoryService.save(fantasy);
        categoryService.save(poetry);
        Page<CategoryDto> actual = categoryService.findAll(Pageable.unpaged());
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(3, actual.getTotalElements());
    }

    @Test
    @DisplayName("Get existing category by its id")
    void getById_existingCategory_ReturnsTheCategory() {
        CategoryRequestDto history
                = new CategoryRequestDto()
                .setName("history");
        categoryService.save(history);
        Category savedInDb = categoryRepository.findAll().get(0);
        CategoryDto actual = categoryService.getById(savedInDb.getId());
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("history", actual.getName());
    }

    @Test
    @DisplayName("Returns error in case of non-existing category")
    void getById_nonExistingBook_ThrowsException() {
        Long nonExistingCategoryId = 9999L;
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(nonExistingCategoryId));
        String expectedMessage = "Cannot find category by id: " + nonExistingCategoryId;
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Update an existing category")
    void update_existingCategory_ReturnsUpdatedCategory() {
        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setName("story");
        categoryService.save(requestDto);
        CategoryRequestDto updateDto = new CategoryRequestDto()
                .setName("updated");
        Category savedInDb = categoryRepository.findAll().get(0);
        CategoryDto updated = categoryService.update(savedInDb.getId(), updateDto);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals("updated", updated.getName());
    }

    @Test
    @DisplayName("Returns error in case of trying to update non-existing category")
    void update_nonExistingCategory_ThrowsException() {
        Long nonExistingCategoryId = 9999L;
        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setName("New Category");
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(nonExistingCategoryId, requestDto));
        String expectedMessage = "Cannot update category by id: " + nonExistingCategoryId;
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Delete existing category by its id")
    void deleteById_existingBook_Success() {
        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setName("history");
        categoryService.save(requestDto);
        Category savedInDb = categoryRepository.findAll().get(0);
        Long categoryId = savedInDb.getId();
        categoryService.deleteById(categoryId);
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));
        Assertions.assertTrue(exception.getMessage().contains("Cannot find category by id"));
    }

    @Test
    @DisplayName("Returns error in case of trying to delete non-existing category")
    void deleteById_nonExistingCategory_ThrowsException() {
        Long nonExistingCategoryId = 9999L;
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(nonExistingCategoryId));
        String expectedMessage = "Cannot delete category by id: " + nonExistingCategoryId;
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}

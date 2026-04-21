package mate.academy.service.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.dto.book.CategoryDto;
import mate.academy.dto.book.CategoryRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.book.CategoryMapper;
import mate.academy.model.book.Category;
import mate.academy.repository.book.category.CategoryRepository;
import mate.academy.util.TestUtil;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
@Transactional
@ActiveProfiles("test")
public class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("Create a new Category")
    void save_newCategory_ReturnsNewCategory() throws BadRequestException {
        // given
        CategoryRequestDto requestDto
                = new CategoryRequestDto()
                .setName("Fantasy")
                .setDescription("Fantasy books");

        Category categoryWithoutId = new Category()
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        Category saved = new Category()
                .setId(1L)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        CategoryDto expected = TestUtil.categoryDtoFantasy();

        when(categoryMapper.toEntity(requestDto)).thenReturn(categoryWithoutId);
        when(categoryRepository.save(categoryWithoutId)).thenReturn(saved);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(expected);

        // when
        CategoryDto actual = categoryService.save(requestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );

        verify(categoryRepository, times(1)).save(categoryWithoutId);
    }

    @Test
    @DisplayName("Create Category without name")
    void save_categoryWithoutName_BadRequest() {
        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setDescription("Fantasy books");

        assertThrows(BadRequestException.class,
                () -> categoryService.save(requestDto));

        verify(categoryRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Find all categories in database")
    void findAll_threeCategories_ReturnsAllCategories() {
        CategoryDto categoryDtoFantasy = TestUtil.categoryDtoFantasy();
        Category fantasy
                = new Category()
                .setName(categoryDtoFantasy.getName())
                .setDescription(categoryDtoFantasy.getDescription());

        CategoryDto categoryDtoHistory = TestUtil.categoryDtoHistory();
        Category history
                = new Category()
                .setName(categoryDtoHistory.getName())
                .setDescription(categoryDtoHistory.getDescription());

        CategoryDto categoryDtoFiction = TestUtil.categoryDtoFiction();
        Category fiction
                = new Category()
                .setName(categoryDtoFiction.getName())
                .setDescription(categoryDtoFiction.getDescription());

        List<Category> categories = List.of(fantasy, history, fiction);
        Page<Category> page = new PageImpl<>(categories);
        Pageable pageable = PageRequest.of(0, 10);

        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(categoryMapper.toDto(fantasy)).thenReturn(categoryDtoFantasy);
        when(categoryMapper.toDto(history)).thenReturn(categoryDtoHistory);
        when(categoryMapper.toDto(fiction)).thenReturn(categoryDtoFiction);

        Page<CategoryDto> actual = categoryService.getAll(pageable);

        assertNotNull(actual);
        assertEquals(3, actual.getTotalElements());
        verify(categoryRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Get existing category by its id")
    void getById_existingCategory_ReturnsTheCategory() {
        CategoryDto categoryDtoFantasy = TestUtil.categoryDtoFantasy();
        Category fantasy
                = new Category()
                .setName(categoryDtoFantasy.getName())
                .setDescription(categoryDtoFantasy.getDescription());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(fantasy));
        when(categoryMapper.toDto(fantasy)).thenReturn(categoryDtoFantasy);

        CategoryDto actual = categoryService.getById(1L);

        assertNotNull(actual);
        verify(categoryRepository, times(1))
                .findById(anyLong());
    }

    @Test
    @DisplayName("Returns error in case of non-existing category")
    void getById_nonExistingBook_ThrowsException() {
        Long nonExistingCategoryId = 9999L;

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(nonExistingCategoryId));

        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Update an existing category")
    void update_existingCategory_ReturnsUpdatedCategory() {
        Long categoryId = 1L;

        Category existingCategory = new Category()
                .setId(categoryId)
                .setName("Old Name");

        CategoryRequestDto updateDto = new CategoryRequestDto()
                .setName("New Name");

        Category savedCategory = new Category()
                .setId(categoryId)
                .setName(updateDto.getName());

        CategoryDto expectedDto = new CategoryDto()
                .setName(updateDto.getName());

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);
        when(categoryMapper.toDto(any(Category.class)))
                .thenReturn(expectedDto);

        CategoryDto actual = categoryService.update(categoryId, updateDto);

        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expectedDto, actual)
        );

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Returns error in case of trying to update non-existing category")
    void update_nonExistingCategory_ThrowsException() {
        Long nonExistingCategoryId = 9999L;
        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setName("New Category");
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(nonExistingCategoryId, requestDto));

        verify(categoryRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Delete existing category by its id")
    void deleteById_existingBook_Success() {
        CategoryDto categoryDto
                = TestUtil.categoryDtoFiction();
        Category category
                = new Category()
                .setName(categoryDto.getName());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteById(1L);

        verify(categoryRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("Returns error in case of trying to delete non-existing category")
    void deleteById_nonExistingCategory_ThrowsException() {
        Long nonExistingCategoryId = 9999L;

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(nonExistingCategoryId));

        verify(categoryRepository, times(0))
                .deleteById(anyLong());
    }
}

package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CategoryDto;
import mate.academy.mapper.CategoryMapper;
import mate.academy.service.book.BookService;
import mate.academy.service.category.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoints for managing categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all categories",
            description = "Get a list of all available categories")
    @PreAuthorize("hasRole('USER')")
    public Page<CategoryDto> getAll(Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category",
            description = "Get an existing category by its id")
    @PreAuthorize("hasRole('USER')")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new category",
            description = "Create a new category")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category",
            description = "Update an existing category by its id")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto updateCategory(@PathVariable Long id,
                                  @RequestBody @Valid CategoryDto requestDto) {
        return categoryService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category",
            description = "Delete an existing category by its id")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get books by category id",
            description = "Retrieve books in specific category by category id")
    @PreAuthorize("hasRole('USER')")
    public Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            @PathVariable Long id, Pageable pageable) {
        return bookService.findAllByCategoryId(id, pageable);
    }
}

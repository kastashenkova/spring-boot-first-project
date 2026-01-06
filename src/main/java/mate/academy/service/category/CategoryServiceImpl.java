package mate.academy.service.category;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.book.CategoryDto;
import mate.academy.dto.book.CategoryRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.book.Category;
import mate.academy.repository.category.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto);
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot find category by id: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto save(CategoryRequestDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto update(Long id, CategoryRequestDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot update category by id: " + id));
        categoryMapper.updateFromDto(categoryDto, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot delete category by id: " + id));
        categoryRepository.deleteById(id);
    }
}

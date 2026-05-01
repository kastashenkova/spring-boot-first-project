package mate.academy.service.category;

import mate.academy.dto.book.CategoryDto;
import mate.academy.dto.book.CategoryRequestDto;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryDto> getAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CategoryRequestDto categoryDto) throws BadRequestException;

    CategoryDto update(Long id, CategoryRequestDto categoryDto);

    void deleteById(Long id);
}

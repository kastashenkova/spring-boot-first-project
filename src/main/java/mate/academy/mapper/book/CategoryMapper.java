package mate.academy.mapper.book;

import mate.academy.config.MapperConfig;
import mate.academy.dto.book.CategoryDto;
import mate.academy.dto.book.CategoryRequestDto;
import mate.academy.model.book.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryRequestDto categoryDto);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(CategoryRequestDto requestDto,
                           @MappingTarget Category category);
}

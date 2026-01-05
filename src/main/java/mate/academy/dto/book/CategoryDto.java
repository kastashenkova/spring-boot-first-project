package mate.academy.dto.book;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    @NotBlank
    private String name;
    private String description;
}

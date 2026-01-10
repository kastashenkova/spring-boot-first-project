package mate.academy.dto.book;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CategoryRequestDto {
    @NotBlank
    private String name;
    private String description;
}

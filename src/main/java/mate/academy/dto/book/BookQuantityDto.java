package mate.academy.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookQuantityDto {
    @NotNull
    @Min(1)
    private int quantity;
}

package mate.academy.dto.cart;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookToTheShoppingCartDto {
    @NotBlank
    private Long bookId;
    @NotBlank
    private int quantity;
}

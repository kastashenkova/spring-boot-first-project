package mate.academy.dto.cart;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookToTheShoppingCartDto {
    @Positive
    private Long bookId;
    @Positive
    private int quantity;
}

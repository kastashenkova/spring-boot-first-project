package mate.academy.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDto {
    @Positive
    private Long bookId;
    @Positive
    @NotNull
    private int quantity;
}

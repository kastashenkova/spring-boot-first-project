package mate.academy.dto.cart;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemDto {
    @Positive
    private int quantity;
}

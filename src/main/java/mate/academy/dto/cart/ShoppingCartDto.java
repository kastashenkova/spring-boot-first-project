package mate.academy.dto.cart;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingCartDto {
    @NotBlank
    private Long id;
    @NotBlank
    private Long userId;
    private Set<CartItemDto> cartItems;
}

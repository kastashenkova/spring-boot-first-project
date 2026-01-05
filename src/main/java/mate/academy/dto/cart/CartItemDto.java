package mate.academy.dto.cart;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
    @NotBlank
    private Long id;
    @NotBlank
    private Long bookId;
    @NotBlank
    private String bookTitle;
    @NotBlank
    private int quantity;
}

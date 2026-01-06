package mate.academy.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import mate.academy.model.book.Book;
import mate.academy.model.order.Order;

@Getter
@Setter
public class OrderItemRequestDto {
    @Positive
    private Long id;
    @NotBlank
    private Order order;
    @NotBlank
    private Book book;
    @Positive
    private int quantity;
    @Positive
    private BigDecimal price;
}

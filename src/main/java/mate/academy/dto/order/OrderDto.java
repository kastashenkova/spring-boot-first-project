package mate.academy.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import mate.academy.model.order.Order;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long userId;
    private Order.Status status;
    private BigDecimal total;
    private LocalDateTime orderDate;
    private Set<OrderItemDto> orderItems;
}

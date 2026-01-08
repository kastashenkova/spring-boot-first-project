package mate.academy.dto.order;

import lombok.Getter;
import lombok.Setter;
import mate.academy.model.order.Order;

@Getter
@Setter
public class UpdateOrderDto {
    private Order.Status status;
}

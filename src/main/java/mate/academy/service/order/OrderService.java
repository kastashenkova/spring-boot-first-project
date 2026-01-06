package mate.academy.service.order;

import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.dto.order.OrderRequestDto;
import mate.academy.dto.order.UpdateOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto addOrder(OrderRequestDto request);

    Page<OrderDto> getOrders(Pageable pageable);

    OrderDto updateStatus(Long orderId, UpdateOrderDto request);

    Page<OrderItemDto> getOrderItems(Long orderId, Pageable pageable);

    OrderItemDto getItem(Long orderId, Long itemId);
}

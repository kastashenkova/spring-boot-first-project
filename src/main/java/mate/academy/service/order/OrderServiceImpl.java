package mate.academy.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.dto.order.OrderRequestDto;
import mate.academy.dto.order.UpdateOrderDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.order.OrderItemMapper;
import mate.academy.mapper.order.OrderMapper;
import mate.academy.model.cart.CartItem;
import mate.academy.model.cart.ShoppingCart;
import mate.academy.model.order.Order;
import mate.academy.model.order.OrderItem;
import mate.academy.model.user.User;
import mate.academy.repository.cart.CartItemRepository;
import mate.academy.repository.cart.ShoppingCartRepository;
import mate.academy.repository.order.OrderItemRepository;
import mate.academy.repository.order.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    @Override
    public OrderDto addOrder(OrderRequestDto request) {
        User user = getCurrentUser();
        ShoppingCart cart = shoppingCartRepository.findByShoppingCartUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user: " + user.getId()));
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new EntityNotFoundException(
                    "Shopping cart is empty for user: " + user.getId());
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            BigDecimal itemTotal = cartItem.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(itemTotal);
        }
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(request.getShippingAddress());
        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);
        orderRepository.save(savedOrder);
        cartItemRepository.deleteAll(cart.getCartItems());
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public Page<OrderDto> getOrders(Pageable pageable) {
        Long userId = getCurrentUser().getId();
        return orderRepository.findAllByUserIdWithOrderItems(userId, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public OrderDto updateStatus(Long orderId, UpdateOrderDto request) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Cannot update order by id: " + orderId));
        orderMapper.updateFromDto(request, order);
        Order updatedOder = orderRepository.save(order);
        return orderMapper.toDto(updatedOder);
    }

    @Override
    public Page<OrderItemDto> getOrderItems(Long orderId, Pageable pageable) {
        if (!orderRepository.existsById(orderId)) {
            throw new EntityNotFoundException(
                    "Cannot find order item by id: " + orderId);
        }
        return orderItemRepository.findAllByOrder_Id(orderId, pageable)
                .map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemDto getItem(Long orderId, Long itemId) {
        OrderItem item = orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot get order by id: " + orderId));
        return orderItemMapper.toDto(item);
    }

    private User getCurrentUser() throws SecurityException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Unauthorized");
        }
        return (User) authentication.getPrincipal();
    }
}

package mate.academy.mapper.order;

import mate.academy.config.MapperConfig;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderRequestDto;
import mate.academy.dto.order.UpdateOrderDto;
import mate.academy.model.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderDto toDto(Order order);

    Order toEntity(OrderRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateFromDto(UpdateOrderDto requestDto,
                       @MappingTarget Order order);
}

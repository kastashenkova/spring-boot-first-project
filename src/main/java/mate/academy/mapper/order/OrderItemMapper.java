package mate.academy.mapper.order;

import mate.academy.config.MapperConfig;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.model.order.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);
}

package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.cart.CartItemDto;
import mate.academy.model.cart.CartItem;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    CartItemDto toDto(CartItem cartItem);

    CartItem toEntity(CartItemDto cartItemDto);
}

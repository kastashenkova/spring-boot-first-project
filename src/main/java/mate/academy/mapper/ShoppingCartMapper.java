package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.model.book.Category;
import mate.academy.model.cart.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItems", source = "cartItems")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    Category toEntity(ShoppingCartDto shoppingCartDto);
}

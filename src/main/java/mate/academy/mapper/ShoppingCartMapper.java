package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.model.book.Category;
import mate.academy.model.cart.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    Category toEntity(ShoppingCartDto shoppingCartDto);
}

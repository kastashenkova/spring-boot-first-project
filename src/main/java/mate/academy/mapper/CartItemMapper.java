package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.cart.CartItemDto;
import mate.academy.model.cart.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(target = "book", source = "bookId",
            qualifiedByName = "bookFromId")
    CartItem toEntity(CartItemDto cartItemDto);
}

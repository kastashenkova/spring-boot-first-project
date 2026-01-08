package mate.academy.service.cart;

import mate.academy.dto.cart.CartItemRequestDto;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.dto.cart.UpdateCartItemDto;
import mate.academy.model.user.User;

public interface ShoppingCartService {
    ShoppingCartDto getCart();

    ShoppingCartDto addBook(CartItemRequestDto request);

    ShoppingCartDto updateQuantity(Long cartItemId, UpdateCartItemDto request);

    void deleteBook(Long cartItemId);

    void addUser(User user);
}

package mate.academy.service.cart;

import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookQuantityDto;
import mate.academy.dto.cart.BookToTheShoppingCartDto;
import mate.academy.dto.cart.CartItemDto;
import mate.academy.dto.cart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getCart();

    BookDto addBook(BookToTheShoppingCartDto request);

    CartItemDto updateQuantity(Long cartItemId, BookQuantityDto request);

    void deleteBook(Long cartItemId);
}

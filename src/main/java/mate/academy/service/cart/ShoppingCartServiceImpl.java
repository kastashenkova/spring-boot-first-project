package mate.academy.service.cart;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookQuantityDto;
import mate.academy.dto.cart.BookToTheShoppingCartDto;
import mate.academy.dto.cart.CartItemDto;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.mapper.CartItemMapper;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.book.Book;
import mate.academy.model.cart.CartItem;
import mate.academy.model.cart.ShoppingCart;
import mate.academy.model.user.User;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.cart.CartItemRepository;
import mate.academy.repository.cart.ShoppingCartRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto getCart() {
        ShoppingCart cart = shoppingCartRepository
                .findByUserId(getCurrentUser().getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Cart not found for user " + getCurrentUser().getId()));
        return shoppingCartMapper.toDto(cart);
    }

    private User getCurrentUser() throws SecurityException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Unauthorized");
        }
        return (User) authentication.getPrincipal();
    }

    @Override
    public BookDto addBook(BookToTheShoppingCartDto request) {
        User user = getCurrentUser();
        ShoppingCart cart = shoppingCartRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart not found for user " + user.getId()));
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book not found: " + request.getBookId()));
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getBook()
                        .getId().equals(request.getBookId()))
                .findFirst()
                .orElse(null);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setBook(book);
            newItem.setQuantity(request.getQuantity());
            newItem.setShoppingCart(cart);
            cart.getCartItems().add(newItem);
        }
        shoppingCartRepository.save(cart);
        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public CartItemDto updateQuantity(Long cartItemId, BookQuantityDto request) {
        User user = getCurrentUser();
        CartItem item = cartItemRepository.findByIdWithCartAndBook(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid cart item id: " + cartItemId));
        if (!item.getShoppingCart().getUser().getId().equals(user.getId())) {
            throw new SecurityException(
                    "Cart item with id " + cartItemId
                            + "does not belong to user: " + user.getId());
        }
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return cartItemMapper.toDto(item);
    }

    @Override
    public void deleteBook(Long cartItemId) {
        User user = getCurrentUser();
        CartItem item = cartItemRepository.findByIdWithCartAndBook(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found by id: " + cartItemId));
        if (!item.getShoppingCart().getUser().getId().equals(user.getId())) {
            throw new SecurityException(
                    "Cart item with id " + cartItemId
                            + "does not belong to user: " + user.getId());
        }
        cartItemRepository.delete(item);
    }
}

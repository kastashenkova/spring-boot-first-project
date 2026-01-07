package mate.academy.service.cart;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.cart.CartItemRequestDto;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.dto.cart.UpdateCartItemDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.cart.ShoppingCartMapper;
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
@Transactional
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto getCart() {
        Long currentUserId = getCurrentUserId();
        ShoppingCart cart = shoppingCartRepository
                .findByUserId(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart not found for user " + currentUserId));
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto addBook(CartItemRequestDto request) {
        Long currentUserId = getCurrentUserId();
        ShoppingCart cart = shoppingCartRepository
                .findByUserId(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart not found for user " + currentUserId));
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
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto updateQuantity(Long cartItemId, UpdateCartItemDto request) {
        Long currentUserId = getCurrentUserId();
        final ShoppingCart cart = shoppingCartRepository
                .findByUserId(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart not found for user " + currentUserId));
        CartItem item = cartItemRepository
                .findByIdAndShoppingCartId(cartItemId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid cart item id: " + cartItemId));
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public void deleteBook(Long cartItemId) {
        Long currentUserId = getCurrentUserId();
        CartItem item = cartItemRepository
                .findByIdAndShoppingCartId(cartItemId, currentUserId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found by id: " + cartItemId));
        cartItemRepository.delete(item);
    }

    @Override
    public void addUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    private Long getCurrentUserId() throws SecurityException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Unauthorized");
        }
        return ((User) authentication.getPrincipal()).getId();
    }
}

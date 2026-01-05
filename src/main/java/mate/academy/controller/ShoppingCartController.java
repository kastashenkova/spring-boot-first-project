package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.cart.CartItemRequestDto;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.dto.cart.UpdateCartItemDto;
import mate.academy.service.cart.ShoppingCartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management",
        description = "Endpoints for managing user's shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get shopping cart",
            description = "Retrieve user's shopping cart")
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartService.getCart();
    }

    @PostMapping
    @Operation(summary = "Add book to cart",
            description = "Add book to the shopping cart")
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto addBookToTheCart(@RequestBody @Valid CartItemRequestDto request) {
        return shoppingCartService.addBook(request);
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update quantity of a book in the shopping cart",
            description = "Update quantity of a book in the shopping cart  by its id")
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto updateBookQuantityInCart(@PathVariable Long cartItemId,
                                                @RequestBody @Valid UpdateCartItemDto request) {
        return shoppingCartService.updateQuantity(cartItemId, request);
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Remove a book from the shopping cart",
            description = "Remove a book from the shopping cart by its id")
    @PreAuthorize("hasRole('USER')")
    public void deleteBookFromTheCart(@PathVariable Long cartItemId) {
        shoppingCartService.deleteBook(cartItemId);
    }
}

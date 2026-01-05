package mate.academy.repository.cart;

import java.util.Optional;
import mate.academy.model.cart.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository
        extends JpaRepository<ShoppingCart, Long> {

    @Query("SELECT sc FROM ShoppingCart sc"
            + " LEFT JOIN FETCH sc.cartItems ci"
            + " LEFT JOIN FETCH ci.book"
            + " WHERE sc.user.id = :id AND sc.isDeleted = false")
    Optional<ShoppingCart> findByUserId(Long id);
}

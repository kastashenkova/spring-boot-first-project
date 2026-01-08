package mate.academy.repository.cart;

import java.util.Optional;
import mate.academy.model.cart.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository
        extends JpaRepository<ShoppingCart, Long> {

    @EntityGraph(attributePaths = {"cartItems", "cartItems.book"})
    Optional<ShoppingCart> findByUserId(Long id);
}

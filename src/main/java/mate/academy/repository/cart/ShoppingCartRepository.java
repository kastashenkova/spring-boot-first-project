package mate.academy.repository.cart;

import java.util.Optional;
import mate.academy.model.cart.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository
        extends JpaRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findByUserId(Long id);
}

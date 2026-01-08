package mate.academy.repository.cart;

import java.util.Optional;
import mate.academy.model.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci"
            + " JOIN FETCH ci.shoppingCart sc"
            + " WHERE ci.id = :cartItemId AND sc.id = :shoppingCartId")
    Optional<CartItem> findByIdAndShoppingCartId(Long cartItemId, Long shoppingCartId);
}

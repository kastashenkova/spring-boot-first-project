package mate.academy.repository.order;

import java.util.Optional;
import mate.academy.model.order.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT items FROM OrderItem items"
            + " JOIN FETCH items.order o"
            + " WHERE items.id = :itemId AND o.id = :orderId")
    Optional<OrderItem> findByIdAndOrderId(Long itemId, Long orderId);

    @EntityGraph(attributePaths = "order")
    Page<OrderItem> findAllByOrder_Id(Long orderId, Pageable pageable);
}

package mate.academy.repository.order;

import mate.academy.model.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o "
            + "LEFT JOIN FETCH o.orderItems oi "
            + "LEFT JOIN FETCH oi.book "
            + "JOIN FETCH o.user "
            + "WHERE o.user.id = :userId AND o.isDeleted = false")
    Page<Order> findAllByUserIdWithOrderItems(Long userId, Pageable pageable);
}

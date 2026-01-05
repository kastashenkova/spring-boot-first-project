package mate.academy.model.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mate.academy.model.user.User;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE orders SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(nullable = false)
    private BigDecimal total;
    @Column(nullable = false)
    private LocalDateTime orderDate;
    @Column(nullable = false)
    private String shippingAddress;
    @OneToMany
    @JoinColumn(name = "order_id")
    Set<OrderItem> orderItems = new HashSet<>();
    @Column(nullable = false)
    private boolean isDeleted = false;

    public enum Status {
        ACTIVE, PENDING, COMPLETED, CANCELLED
    }
}

package mate.academy.model.cart;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import mate.academy.model.user.User;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE shopping_cart SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "shopping_cart")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToMany(mappedBy = "shoppingCart",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<CartItem> cartItems = new HashSet<>();
    @Column(nullable = false)
    private boolean isDeleted = false;
}

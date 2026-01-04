package mate.academy.dto.user.registration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String shippingAddress;
}

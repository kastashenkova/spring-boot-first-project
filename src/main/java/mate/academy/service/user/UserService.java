package mate.academy.service.user;

import mate.academy.dto.user.registration.UserRegistrationRequestDto;
import mate.academy.dto.user.registration.UserResponseDto;
import mate.academy.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}

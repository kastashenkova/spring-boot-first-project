package mate.academy.service.user;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.registration.UserRegistrationRequestDto;
import mate.academy.dto.user.registration.UserResponseDto;
import mate.academy.exception.RegistrationException;
import mate.academy.mapper.UserMapper;
import mate.academy.model.user.Role;
import mate.academy.model.user.User;
import mate.academy.repository.user.RoleRepository;
import mate.academy.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new RegistrationException(
                    "User with such email already exists: "
                            + request.getEmail());
        }
        User user = userMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role defaultRole = roleRepository.findRoleByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
        user.setRoles(Set.of(defaultRole));
        userRepository.save(user);
        return userMapper.toUserResponseDto(user);
    }
}

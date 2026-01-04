package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.user.registration.UserRegistrationRequestDto;
import mate.academy.dto.user.registration.UserResponseDto;
import mate.academy.model.user.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User book);

    User toUserEntity(UserRegistrationRequestDto requestDto);
}

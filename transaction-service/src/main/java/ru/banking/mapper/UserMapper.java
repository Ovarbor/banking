package ru.banking.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.banking.dto.UserDtoResponse;
import ru.banking.model.User;
import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface UserMapper {

    @Mapping(source = "emailsList", target = "emailsList")
    @Mapping(source = "phonesList", target = "phonesList")
    User toUser(UserDtoResponse userDtoResponse);

    @Mapping(source = "emailsList", target = "emailsList")
    @Mapping(source = "phonesList", target = "phonesList")
    UserDtoResponse toUserDtoResponse(User user);

    @Mapping(source = "emailsList", target = "emailsList")
    @Mapping(source = "phonesList", target = "phonesList")
    List<UserDtoResponse> toUserDtoResponseList(List<User> userList);
}

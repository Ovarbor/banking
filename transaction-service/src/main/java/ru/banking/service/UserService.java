package ru.banking.service;

import ru.banking.dto.*;
import ru.banking.model.User;

import java.time.LocalDate;
import java.util.List;


public interface UserService {

    UserDtoResponse addUser(CreateUserDtoRequest createUserDtoRequest);

    User findUserByName(String name);

    UserDtoResponse addUserPhoneEmail(Long userId, UpdateUserDtoRequest updateUserDtoRequest);

    UserDtoResponse updateUserPhone(Long userId, String phone, UpdateUserPhoneDtoRequest updateUserPhoneDtoRequest);

    UserDtoResponse updateUserEmail(Long userId, String email, UpdateUserEmailDtoRequest updateUserEmailDtoRequest);

    void deleteUserPhone(Long userId, String phone);

    void deleteUserEmail(Long userId, String email);

    List<SearchUserDtoResponse> searchUser(Long userId, String text, LocalDate birthday, String phone,
                                     String email, Integer from, Integer size);
}

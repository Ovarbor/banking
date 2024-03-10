package ru.banking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.banking.dto.*;
import ru.banking.exception.ConflictException;
import ru.banking.exception.NotFoundValidationException;
import ru.banking.mapper.UserMapper;
import ru.banking.model.Account;
import ru.banking.model.QUser;
import ru.banking.model.User;
import ru.banking.repo.AccountRepo;
import ru.banking.repo.UserRepo;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDtoResponse addUser(CreateUserDtoRequest createUserDtoRequest) {
        usernameValidation(createUserDtoRequest.getUsername());
        emailValidation(createUserDtoRequest.getEmail());
        phoneValidation(createUserDtoRequest.getPhone());
        Account account = new Account();
        account.setBalance(createUserDtoRequest.getBalance());
        User user = new User();
        user.setBirthday(createUserDtoRequest.getBirthday());
        user.setUsername(createUserDtoRequest.getUsername());
        user.setPassword(passwordEncoder.encode(createUserDtoRequest.getPassword()));
        user.setPhonesList(new ArrayList<>());
        user.getPhonesList().add(createUserDtoRequest.getPhone());
        user.setEmailsList(new ArrayList<>());
        user.getEmailsList().add(createUserDtoRequest.getEmail());
        account.setUser(user);
        userRepo.save(user);
        accountRepo.save(account);
        return userMapper.toUserDtoResponse(user);
    }

    @Override
    public User findUserByName(String username) {
        return userRepo.findUserByUsername(username).orElseThrow(() ->
                new NotFoundValidationException("User with name " + username + " not found"));
    }

    @Override
    public UserDtoResponse addUserPhoneEmail(Long userId, UpdateUserDtoRequest updateUserDtoRequest) {
        Long start = System.nanoTime();
        emailValidation(updateUserDtoRequest.getEmail());
        phoneValidation(updateUserDtoRequest.getPhone());
        User oldUser = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        User newUser = userParametersUpdate(oldUser, updateUserDtoRequest);
        Long end = System.nanoTime();
        System.out.println(end - start);
        return userMapper.toUserDtoResponse(userRepo.save(newUser));
    }

    @Override
    public UserDtoResponse updateUserPhone(Long userId, String phone, UpdateUserPhoneDtoRequest updateUserPhoneDtoRequest) {
        Long startTime = System.nanoTime();
        phoneValidation(updateUserPhoneDtoRequest.getPhone());
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        List<String> phonesList = user.getPhonesList();
        if (phonesList.contains(phone)) {
            phonesList.set(phonesList.indexOf(phone), updateUserPhoneDtoRequest.getPhone());
            userRepo.save(user);
        } else {
            throw new NotFoundValidationException("Phone: " + phone + " not found");
        }

        Long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        return userMapper.toUserDtoResponse(user);
    }

    @Override
    public UserDtoResponse updateUserEmail(Long userId, String email, UpdateUserEmailDtoRequest updateUserEmailDtoRequest) {
        emailValidation(updateUserEmailDtoRequest.getEmail());
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        List<String> emailList = user.getEmailsList();
        if (emailList.contains(email)) {
            emailList.set(emailList.indexOf(email), updateUserEmailDtoRequest.getEmail());
            userRepo.save(user);
        } else {
            throw new NotFoundValidationException("Email: " + email + " not found");
        }
        return userMapper.toUserDtoResponse(user);
    }

    @Override
    public void deleteUserPhone(Long userId, String phone) {
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        List<String> phoneList = user.getPhonesList();
        if (phoneList.size() <= 1) {
            throw new ConflictException("User must have at least one active phone");
        }
        if (phoneList.contains(phone)) {
            phoneList.remove(phone);
            userRepo.save(user);
        } else {
            throw new NotFoundValidationException("Phone: " + phone + " not found");
        }
    }

    @Override
    public void deleteUserEmail(Long userId, String email) {
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        List<String> emailList = user.getEmailsList();
        if (emailList.size() <= 1) {
            throw  new ConflictException("User must have at least one active email");
        }
        if (emailList.contains(email)) {
            emailList.remove(email);
            userRepo.save(user);
        } else {
            throw new NotFoundValidationException("Email: " + email + " not found");
        }

    }

    @Override
    public List<UserDtoResponse> searchUser(Long userId, String text, LocalDate birthday,
                                            String phone, String email, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size, Sort.by("username").ascending());
        BooleanExpression expression = buildExpression(text, birthday, phone, email);
        List<User> foundUserList = userRepo.findAll(expression, page).getContent();
        return userMapper.toUserDtoResponseList(foundUserList);
    }

    private BooleanExpression buildExpression(String text, LocalDate birthday, String phone, String email) {
        QUser qUser = QUser.user;
        BooleanExpression expression = qUser.eq(qUser);
        if (text != null) {
            expression = expression.and(qUser.username.containsIgnoreCase(text));
        }
        if (birthday != null) {
            expression = expression.and(qUser.birthday.after(birthday));
        }
        if (phone != null) {
            expression = expression.and(qUser.phonesList.contains(phone));
        }
        if (email != null) {
            expression = expression.and(qUser.emailsList.contains(email));
        }
        return expression;
    }

    private User userParametersUpdate(User oldUser, UpdateUserDtoRequest updateUserDtoRequest) {
        if (updateUserDtoRequest.getEmail() != null) {
            if (!updateUserDtoRequest.getEmail().isBlank()) {
                oldUser.getEmailsList().add(updateUserDtoRequest.getEmail());
            }
        }
        if (updateUserDtoRequest.getPhone() != null) {
            if (!updateUserDtoRequest.getPhone().isBlank()) {
                oldUser.getPhonesList().add(updateUserDtoRequest.getPhone());
            }
        }
        return oldUser;
    }


    private void usernameValidation(String username) {
        Set<String> userNamesSet = new HashSet<>(userRepo.findAllNames());
        if (userNamesSet.contains(username)) {
            throw new ConflictException("Username: " + username + ", already used");
        }
    }

    private void emailValidation(String email) {
        Set<String> emailsSet = new HashSet<>(userRepo.findAllEmails());
        if (emailsSet.contains(email)) {
            throw new ConflictException("Email: " + email + ", already used");
        }
    }

    private void phoneValidation(String phone) {
        Set<String> phonesSet = new HashSet<>(userRepo.findAllPhones());
        if (phonesSet.contains(phone)) {
            throw new ConflictException("Phone: " + phone + ", already used");
        }
    }
}

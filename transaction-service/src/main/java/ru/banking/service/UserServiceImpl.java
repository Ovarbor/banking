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
import ru.banking.model.*;
import ru.banking.repo.AccountRepo;
import ru.banking.repo.UserRepo;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        Email email = new Email(createUserDtoRequest.getEmail());
        Phone phone = new Phone(createUserDtoRequest.getPhone());
        usernameValidation(createUserDtoRequest.getUsername());
        emailValidation(email);
        phoneValidation(phone);
        Account account = new Account();
        account.setBalance(createUserDtoRequest.getBalance());
        User user = new User();
        user.setBirthday(createUserDtoRequest.getBirthday());
        user.setUsername(createUserDtoRequest.getUsername());
        user.setPassword(passwordEncoder.encode(createUserDtoRequest.getPassword()));
        user.setPhonesList(new ArrayList<>());
        user.getPhonesList().add(phone);
        user.setEmailsList(new ArrayList<>());
        user.getEmailsList().add(email);
        account.setUser(user);
        userRepo.save(user);
        accountRepo.save(account);
        stackAccountBalance(60000, 60000, account.getBalance(), account);
        return userMapper.toUserDtoResponse(user);
    }

    private void stackAccountBalance(int delay, int period, BigDecimal startBalance, Account account) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Account account1 = accountRepo.findById(account.getId()).orElseThrow();
                BigDecimal actualBalance = account1.getBalance();
                if (actualBalance.compareTo(startBalance.multiply(BigDecimal.valueOf(2.07))) > 0) {
                    timer.cancel();
                } else {
                    account.setBalance(actualBalance.add(actualBalance.multiply(BigDecimal.valueOf(0.05))).setScale(7, RoundingMode.HALF_DOWN));
                    accountRepo.save(account);
                }
            }
        }, delay, period);
    }

    @Override
    public User findUserByName(String username) {
        return userRepo.findUserByUsername(username).orElseThrow(() ->
                new NotFoundValidationException("User with name " + username + " not found"));
    }

    @Override
    public UserDtoResponse addUserPhoneEmail(Long userId, UpdateUserDtoRequest updateUserDtoRequest) {
        Long start = System.nanoTime();
        emailValidation(new Email(updateUserDtoRequest.getEmail()));
        phoneValidation(new Phone(updateUserDtoRequest.getPhone()));
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
        Phone requestPhone = new Phone(updateUserPhoneDtoRequest.getPhone());
        Phone replaceablePhone = new Phone(phone);
        phoneValidation(requestPhone);
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        List<Phone> phonesList = user.getPhonesList();
        if (phonesList.contains(replaceablePhone)) {
            phonesList.set(phonesList.indexOf(replaceablePhone), requestPhone);
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
        Email requestEmail = new Email(updateUserEmailDtoRequest.getEmail());
        Email replaceableEmail = new Email(email);
        emailValidation(requestEmail);
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        List<Email> emailList = user.getEmailsList();
        if (emailList.contains(replaceableEmail)) {
            emailList.set(emailList.indexOf(replaceableEmail), requestEmail);
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
        Phone requestPhone = new Phone(phone);
        List<Phone> phoneList = user.getPhonesList();
        if (phoneList.size() <= 1) {
            throw new ConflictException("User must have at least one active phone");
        }
        if (phoneList.contains(requestPhone)) {
            phoneList.remove(requestPhone);
            userRepo.save(user);
        } else {
            throw new NotFoundValidationException("Phone: " + phone + " not found");
        }
    }

    @Override
    public void deleteUserEmail(Long userId, String email) {
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        Email requestEmail = new Email(email);
        List<Email> emailList = user.getEmailsList();
        if (emailList.size() <= 1) {
            throw  new ConflictException("User must have at least one active email");
        }
        if (emailList.contains(requestEmail)) {
            emailList.remove(requestEmail);
            userRepo.save(user);
        } else {
            throw new NotFoundValidationException("Email: " + email + " not found");
        }

    }

    @Override
    public List<SearchUserDtoResponse> searchUser(Long userId, String text, LocalDate birthday,
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
            expression = expression.and(qUser.phonesList.contains(new Phone(phone)));
        }
        if (email != null) {
            expression = expression.and(qUser.emailsList.contains(new Email(email)));
        }
        return expression;
    }

    private User userParametersUpdate(User oldUser, UpdateUserDtoRequest updateUserDtoRequest) {
        if (updateUserDtoRequest.getEmail() != null) {
            if (!updateUserDtoRequest.getEmail().isBlank()) {
                Email email = new Email(updateUserDtoRequest.getEmail());
                oldUser.getEmailsList().add(email);
            }
        }
        if (updateUserDtoRequest.getPhone() != null) {
            if (!updateUserDtoRequest.getPhone().isBlank()) {
                Phone phone = new Phone(updateUserDtoRequest.getPhone());
                oldUser.getPhonesList().add(phone);
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

    private void emailValidation(Email email) {
        Set<Email> emailsSet = new HashSet<>(userRepo.findAllEmails());
        if (emailsSet.contains(email)) {
            throw new ConflictException("Email: " + email + ", already used");
        }
    }

    private void phoneValidation(Phone phone) {
        Set<Phone> phonesSet = new HashSet<>(userRepo.findAllPhones());
        if (phonesSet.contains(phone)) {
            throw new ConflictException("Phone: " + phone + ", already used");
        }
    }
}

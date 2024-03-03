package ru.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.banking.dto.*;
import ru.banking.exception.ConflictException;
import ru.banking.exception.NotFoundValidationException;
import ru.banking.mapper.UserMapper;
import ru.banking.model.Account;
import ru.banking.model.Email;
import ru.banking.model.Phone;
import ru.banking.model.User;
import ru.banking.repo.AccountRepo;
import ru.banking.repo.EmailRepo;
import ru.banking.repo.PhoneRepo;
import ru.banking.repo.UserRepo;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final AccountRepo accountRepo;
    private final EmailRepo emailRepo;
    private final PhoneRepo phoneRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDtoResponse addUser(CreateUserDtoRequest createUserDtoRequest) {
        usernameValidation(createUserDtoRequest.getUsername());
        emailValidation(createUserDtoRequest.getEmail());
        phoneValidation(createUserDtoRequest.getPhone());
        Account account = new Account();
        account.setBalance(createUserDtoRequest.getBalance());
        Phone phone = new Phone();
        phone.setPhone(createUserDtoRequest.getPhone());
        Email email = new Email();
        email.setEmail(createUserDtoRequest.getEmail());
        User user = new User();
        user.setBirthday(createUserDtoRequest.getBirthday());
        user.setUsername(createUserDtoRequest.getUsername());
        user.setPassword(passwordEncoder.encode(createUserDtoRequest.getPassword()));
        user.setPhoneList(new ArrayList<>());
        user.getPhoneList().add(phone);
        user.setEmailList(new ArrayList<>());
        user.getEmailList().add(email);
        phone.setUser(user);
        email.setUser(user);
        account.setUser(user);
        userRepo.save(user);
        accountRepo.save(account);
        emailRepo.save(email);
        phoneRepo.save(phone);
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
    public UserDtoResponse updateUserPhone(Long userId, Long phoneId, UpdateUserPhoneDtoRequest updateUserPhoneDtoRequest) {
        Long startTime = System.nanoTime();
        phoneValidation(updateUserPhoneDtoRequest.getPhone());
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        List<Phone> phonesList = user.getPhoneList();
        Optional<Phone> phoneOptional = phonesList.stream()
                .filter(phone -> phone.getId().equals(phoneId))
                .findAny();
        if (phoneOptional.isPresent()) {
            Phone phone = phoneOptional.get();
            phone.setPhone(updateUserPhoneDtoRequest.getPhone());
            phoneRepo.save(phone);
        } else {
            throw new NotFoundValidationException("Phone with id: " + phoneId + " not found");
        }

        Long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        return userMapper.toUserDtoResponse(user);
    }

    @Override
    public UserDtoResponse updateUserEmail(Long userId, Long emailId, UpdateUserEmailDtoRequest updateUserEmailDtoRequest) {
        emailValidation(updateUserEmailDtoRequest.getEmail());
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        List<Email> emailList = user.getEmailList();
        Optional<Email> emailOptional = emailList.stream()
                .filter(email -> email.getId().equals(emailId))
                .findAny();
        if (emailOptional.isPresent()) {
            Email email = emailOptional.get();
            email.setEmail(updateUserEmailDtoRequest.getEmail());
            emailRepo.save(email);
        } else {
            throw new NotFoundValidationException("Email with id: " + emailId + " not found");
        }
        return userMapper.toUserDtoResponse(user);
    }

    @Override
    public void deleteUserPhone(Long userId, Long phoneId) {
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        List<Phone> phoneList = user.getPhoneList();
        Optional<Phone> optionalPhone = phoneList.stream()
                .filter(phone -> phone.getId().equals(phoneId))
                .findAny();
        Phone phone;
        if (optionalPhone.isPresent()) {
            phone = optionalPhone.get();
        } else {
            throw new NotFoundValidationException("Phone with id: " + phoneId + " not found");
        }
        if (phoneList.size() > 1) {
            phoneRepo.delete(phone);
        } else  {
            throw new ConflictException("User must have at least one active phone");
        }
    }

    @Override
    public void deleteUserEmail(Long userId, Long emailId) {
        User user = userRepo.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userId + " not found"));
        List<Email> emailList = user.getEmailList();
        Optional<Email> emailOptional = emailList.stream()
                .filter(email -> email.getId().equals(emailId))
                .findAny();
        Email email;
        if (emailOptional.isPresent()) {
            email = emailOptional.get();
        } else {
            throw new NotFoundValidationException("Email with id: " + emailId + " not found");
        }
        if (emailList.size() > 1) {
            emailRepo.delete(email);
        } else {
            throw  new ConflictException("User must have at least one active email");
        }
    }

    @Override
    public List<UserDtoResponse> searchUser(Long userId, String text, LocalDate birthday, String phone, String email, Integer from, Integer size) {
        if (birthday != null) {
            List<User> userList = userRepo.findUsersByBirthdayAfter(PageRequest.of(from, size, Sort.by("birthday").ascending()), birthday);
            return userMapper.toUserDtoResponseList(userList);
        }
        if (phone != null) {
            List<User> userList = userRepo.findUsersByPhone(PageRequest.of(from, size, Sort.by("username").ascending()), phone);
            return userMapper.toUserDtoResponseList(userList);
        }
        if (email != null) {
            List<User> userList = userRepo.findUsersByEmail(PageRequest.of(from, size, Sort.by("username").ascending()), email);
            return userMapper.toUserDtoResponseList(userList);
        }
        if (text != null) {
            List<User> userList = userRepo.findUsersByText(PageRequest.of(from, size, Sort.by("username").ascending()), text);
            return userMapper.toUserDtoResponseList(userList);
        }
        return new ArrayList<>();
    }

    private User userParametersUpdate(User oldUser, UpdateUserDtoRequest updateUserDtoRequest) {
        if (updateUserDtoRequest.getEmail() != null) {
            if (!updateUserDtoRequest.getEmail().isBlank()) {
                Email email = new Email();
                email.setEmail(updateUserDtoRequest.getEmail());
                email.setUser(oldUser);
                emailRepo.save(email);
                oldUser.getEmailList().add(email);
            }
        }
        if (updateUserDtoRequest.getPhone() != null) {
            if (!updateUserDtoRequest.getPhone().isBlank()) {
                Phone phone = new Phone();
                phone.setPhone(updateUserDtoRequest.getPhone());
                phone.setUser(oldUser);
                phoneRepo.save(phone);
                oldUser.getPhoneList().add(phone);
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
        Set<String> emailsSet = new HashSet<>(emailRepo.findAllEmails());
        if (emailsSet.contains(email)) {
            throw new ConflictException("Email: " + email + ", already used");
        }
    }

    private void phoneValidation(String phone) {
        Set<String> phonesSet = new HashSet<>(phoneRepo.getAllPhones());
        if (phonesSet.contains(phone)) {
            throw new ConflictException("Phone: " + phone + ", already used");
        }
    }
}

package ru.banking.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.banking.dto.*;
import ru.banking.model.User;
import ru.banking.service.UserService;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/add-data")
    public ResponseEntity<UserDtoResponse> addUserPhoneEmail(Principal principal,
                                                             @RequestBody UpdateUserDtoRequest updateUserDtoRequest) {
        User user = userService.findUserByName(principal.getName());
        log.info("PATCH: /users/{}", user.getId());
        return ResponseEntity.ok().body(userService.addUserPhoneEmail(user.getId(), updateUserDtoRequest));
    }

    @PatchMapping("/phone/{phone}")
    public ResponseEntity<UserDtoResponse> updateUserPhone(Principal principal,
                                                           @PathVariable String phone,
                                                           @RequestBody UpdateUserPhoneDtoRequest updateUserPhoneDtoRequest) {
        User user = userService.findUserByName(principal.getName());
        log.info("PATCH: /user/phone/{}", phone);
        return ResponseEntity.ok().body(userService.updateUserPhone(user.getId(), phone, updateUserPhoneDtoRequest));
    }

    @PatchMapping("/email/{email}")
    public ResponseEntity<UserDtoResponse> updateUserEmail(Principal principal,
                                                           @PathVariable String email,
                                                           @RequestBody UpdateUserEmailDtoRequest updateUserEmailDtoRequest) {
        User user = userService.findUserByName(principal.getName());
        log.info("PATCH: /user/email/{}", email);
        return ResponseEntity.ok().body(userService.updateUserEmail(user.getId(), email, updateUserEmailDtoRequest));
    }

    @DeleteMapping("/phone/{phone}")
    public ResponseEntity<Void> deleteUserPhone(Principal principal, @PathVariable String phone) {
        User user = userService.findUserByName(principal.getName());
        log.info("DELETE: /user/phone/{}", phone);
        userService.deleteUserPhone(user.getId(), phone);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteUserEmail(Principal principal, @PathVariable String email) {
        User user = userService.findUserByName(principal.getName());
        log.info("DELETE: /user/email/{}", email);
        userService.deleteUserEmail(user.getId(), email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDtoResponse>> searchUser(Principal principal,
                                                 @RequestParam(value = "text", required = false) String text,
                                                 @RequestParam(required = false) LocalDate birthday,
                                                 @RequestParam(required = false) String phone,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(value = "from", defaultValue = "0")
                                                 @PositiveOrZero Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10")
                                                     @Positive Integer size) {
        User user = userService.findUserByName(principal.getName());
        log.info("GET: /events?text={}&birthday={}&phone={}&email={}&from={}&size={}",
                text, birthday, phone, email, from, size);
        return ResponseEntity.ok().body(userService.searchUser(user.getId(), text, birthday, phone, email, from, size));

    }
}

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

    @PatchMapping("/phone/{phoneId}")
    public ResponseEntity<UserDtoResponse> updateUserPhone(Principal principal,
                                                           @PathVariable Long phoneId,
                                                           @RequestBody UpdateUserPhoneDtoRequest updateUserPhoneDtoRequest) {
        User user = userService.findUserByName(principal.getName());
        log.info("PATCH: /user/phone/{}", phoneId);
        return ResponseEntity.ok().body(userService.updateUserPhone(user.getId(), phoneId, updateUserPhoneDtoRequest));
    }

    @PatchMapping("/email/{emailId}")
    public ResponseEntity<UserDtoResponse> updateUserEmail(Principal principal,
                                                           @PathVariable Long emailId,
                                                           @RequestBody UpdateUserEmailDtoRequest updateUserEmailDtoRequest) {
        User user = userService.findUserByName(principal.getName());
        log.info("PATCH: /user/email/{}", emailId);
        return ResponseEntity.ok().body(userService.updateUserEmail(user.getId(), emailId, updateUserEmailDtoRequest));
    }

    @DeleteMapping("/phone/{phoneId}")
    public ResponseEntity<Void> deleteUserPhone(Principal principal, @PathVariable Long phoneId) {
        User user = userService.findUserByName(principal.getName());
        log.info("DELETE: /user/phone/{}", phoneId);
        userService.deleteUserPhone(user.getId(), phoneId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/email/{emailId}")
    public ResponseEntity<Void> deleteUserEmail(Principal principal, @PathVariable Long emailId) {
        User user = userService.findUserByName(principal.getName());
        log.info("DELETE: /user/email/{}", emailId);
        userService.deleteUserEmail(user.getId(), emailId);
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

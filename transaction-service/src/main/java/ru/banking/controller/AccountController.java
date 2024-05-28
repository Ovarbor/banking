package ru.banking.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.banking.dto.AccountDtoResponse;
import ru.banking.dto.UpdateAccountDtoRequest;
import ru.banking.model.User;
import ru.banking.service.AccountService;
import ru.banking.service.UserService;
import java.security.Principal;

@RestController
@RequestMapping("/user/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final UserService userService;
    private final AccountService accountService;

    @PatchMapping("/send/{recipientId}")
    public ResponseEntity<AccountDtoResponse> sendMoney(Principal principal, @PathVariable Long recipientId,
                                                        @Valid @RequestBody UpdateAccountDtoRequest updateAccountDtoRequest) {
        User user = userService.findUserByName(principal.getName());
        log.info("PATCH: /user/send/{}", recipientId);
        return ResponseEntity.ok().body(accountService.sendMoney(user.getId(), recipientId, updateAccountDtoRequest));
    }

    @GetMapping()
    public ResponseEntity<AccountDtoResponse> getAccount(Principal principal) {
        User user = userService.findUserByName(principal.getName());
        log.info("GET: /user/account :{}", principal.getName());
        return ResponseEntity.ok().body(accountService.getAccount(user.getId()));
    }
}

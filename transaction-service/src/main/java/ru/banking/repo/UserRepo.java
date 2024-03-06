package ru.banking.repo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.banking.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    @Query(value = "SELECT u.username FROM User u")
    List<String> findAllNames();

    @Query(value = "SELECT e.emailsList FROM User e")
    List<String> findAllEmails();

    @Query(value = "SELECT p.phonesList FROM User p")
    List<String> findAllPhones();

    List<User> findUsersByBirthdayAfter(PageRequest pageRequest, LocalDate birthday);

    @Query(value = "SELECT u FROM User u JOIN u.phonesList p WHERE p = :phone")
    List<User> findUsersByPhone(PageRequest pageRequest, @Param("phone") String phone);

    @Query(value = "SELECT u FROM User u JOIN u.emailsList e WHERE e = :email")
    List<User> findUsersByEmail(PageRequest pageRequest, @Param("email") String email);

    @Query(value = "SELECT u FROM User u WHERE u.username LIKE %:text%")
    List<User> findUsersByText(PageRequest pageRequest, @Param("text") String text);
}

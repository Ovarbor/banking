package ru.banking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.banking.model.Email;
import java.util.List;

@Repository
public interface EmailRepo extends JpaRepository<Email, Long> {

    @Query(value = "SELECT e.email FROM Email e")
    List<String> findAllEmails();
}

package ru.banking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.banking.model.Phone;
import java.util.List;

@Repository
public interface PhoneRepo extends JpaRepository<Phone, Long> {

    @Query(value = "SELECT p.phone FROM Phone p")
    List<String> getAllPhones();
}

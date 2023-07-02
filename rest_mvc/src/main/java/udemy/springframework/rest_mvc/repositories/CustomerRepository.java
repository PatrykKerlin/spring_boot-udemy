package udemy.springframework.rest_mvc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import udemy.springframework.rest_mvc.entities.Customer;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}

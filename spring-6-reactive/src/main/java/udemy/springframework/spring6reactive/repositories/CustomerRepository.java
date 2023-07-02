package udemy.springframework.spring6reactive.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import udemy.springframework.spring6reactive.domain.Customer;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}

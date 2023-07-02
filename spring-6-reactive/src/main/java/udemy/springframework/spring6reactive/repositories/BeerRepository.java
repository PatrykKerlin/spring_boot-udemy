package udemy.springframework.spring6reactive.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import udemy.springframework.spring6reactive.domain.Beer;

public interface BeerRepository extends ReactiveCrudRepository<Beer, Integer> {

}

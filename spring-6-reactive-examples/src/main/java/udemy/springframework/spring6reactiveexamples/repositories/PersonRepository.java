package udemy.springframework.spring6reactiveexamples.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactiveexamples.domain.Person;

public interface PersonRepository {
	
	Flux<Person> findAll();
	
	Mono<Person> getById(Integer id);
}

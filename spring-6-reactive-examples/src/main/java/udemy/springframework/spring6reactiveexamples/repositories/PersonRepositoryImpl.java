package udemy.springframework.spring6reactiveexamples.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactiveexamples.domain.Person;

public class PersonRepositoryImpl implements PersonRepository {
	
	Person jan = Person.builder().id(1).firstName("Jan").lastName("Dupa").build();
	Person antoni = Person.builder().id(2).firstName("Antoni").lastName("Kutasinski").build();
	Person aneta = Person.builder().id(3).firstName("Aneta").lastName("Wedzajna").build();
	Person krzysztof = Person.builder().id(4).firstName("Krzysztof").lastName("Krawczyk").build();
	
	@Override
	public Flux<Person> findAll() {
		
		return Flux.just(jan, antoni, aneta, krzysztof);
	}
	
	@Override
	public Mono<Person> getById(final Integer id) {
		
		return findAll().filter(person -> person.id().equals(id)).next();
	}
}

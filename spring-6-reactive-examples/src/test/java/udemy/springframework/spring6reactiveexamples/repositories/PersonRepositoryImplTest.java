package udemy.springframework.spring6reactiveexamples.repositories;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import udemy.springframework.spring6reactiveexamples.domain.Person;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonRepositoryImplTest {
	
	private final PersonRepository personRepository = new PersonRepositoryImpl();
	
	@Test
	void monoByIdBlock() {
		
		Mono<Person> personMono = personRepository.getById(1);
		
		Person person = personMono.block();
		
		System.out.println(person.toString());
	}
	
	@Test
	void testGetByIdSubscriber() {
		
		Mono<Person> personMono = personRepository.getById(1);
		
		personMono.subscribe(System.out::println);
	}
	
	@Test
	void testMap() {
		
		Mono<Person> personMono = personRepository.getById(1);
		
		personMono.map(Person::firstName).subscribe(System.out::println);
	}
	
	@Test
	void testFluxBlockFirst() {
		
		Flux<Person> personFlux = personRepository.findAll();
		
		Person person = personFlux.blockFirst();
		
		System.out.println(person.toString());
	}
	
	@Test
	void testFluxSubscriber() {
		
		Flux<Person> personFlux = personRepository.findAll();
		
		personFlux.subscribe(System.out::println);
	}
	
	@Test
	void testFluxMap() {
		
		Flux<Person> personFlux = personRepository.findAll();
		
		personFlux.map(Person::firstName).subscribe(System.out::println);
	}
	
	@Test
	void testFluxToList() {
		
		Flux<Person> personFlux = personRepository.findAll();
		
		Mono<List<Person>> listMono = personFlux.collectList();
		
		listMono.subscribe(list -> list.forEach(System.out::println));
	}
	
	@Test
	void testFilterOnName() {
		
		personRepository.findAll().filter(person -> person.firstName().equals("Jan")).subscribe(System.out::println);
	}
	
	@Test
	void testGetById() {
		
		Mono<Person> personMono = personRepository.findAll().filter(person -> person.firstName().equals("Jan")).next();
		
		personMono.subscribe(person -> System.out.println(person.firstName()));
	}
	
	@Test
	void testFindPersonByIdNotFound() {
		
		final Integer id = 8;
		
		Flux<Person> personFlux = personRepository.findAll();
		
		Mono<Person> personMono = personFlux.filter(person -> Objects.equals(person.id(), id)).single().doOnError(
				throwable -> {
					System.out.println("Error in flux!");
					System.out.println(throwable.toString());
				});
		
		personMono.subscribe(System.out::println, throwable -> {
			System.out.println("Error in mono!");
			System.out.println(throwable.toString());
		});
	}
	
	@Test
	void testGetByIdFound() {
		
		Mono<Person> personMono = personRepository.getById(1);
		
		assertTrue(personMono.hasElement().block());
	}
	
	@Test
	void testGetByIdNotFound() {
		
		Mono<Person> personMono = personRepository.getById(6);
		
		assertFalse(personMono.hasElement().block());
	}
	
	@Test
	void testGetByIdFoundStepVerifier() {
		
		Mono<Person> personMono = personRepository.getById(1);
		
		StepVerifier.create(personMono).expectNextCount(1).verifyComplete();
		
		personMono.subscribe(System.out::println);
	}
	
	@Test
	void testGetByIdNotFoundStepVerifier() {
		
		Mono<Person> personMono = personRepository.getById(6);
		
		StepVerifier.create(personMono).expectNextCount(0).verifyComplete();
		
		personMono.subscribe(System.out::println);
	}
}
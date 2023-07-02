package udemy.springframework.spring6reactive.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactive.domain.Beer;
import udemy.springframework.spring6reactive.domain.Customer;
import udemy.springframework.spring6reactive.repositories.BeerRepository;
import udemy.springframework.spring6reactive.repositories.CustomerRepository;

@RequiredArgsConstructor
@Component
public class BootStrapData implements CommandLineRunner {
	
	private final BeerRepository beerRepository;
	private final CustomerRepository customerRepository;
	
	@Override
	public void run(String... args) {
		
		beerRepository.count()
		              .flatMap(count -> {
			              if (count == 0L) {
				              return loadBeerData().then(beerRepository.count());
			              }
			              return Mono.just(count);
		              })
		              .doOnSuccess(count -> System.out.println("Beers count: " + count))
		              .block();
		
		customerRepository.count()
		                  .flatMap(count -> {
			                  if (count == 0L) {
				                  return loadCustomerData().then(customerRepository.count());
			                  }
			                  return Mono.just(count);
		                  })
		                  .doOnSuccess(count -> System.out.println("Beers count: " + count))
		                  .block();
	}
	
	private Mono<Void> loadCustomerData() {
		
		Customer customer1 = Customer.builder()
		                             .name("Alina Krawczyk")
		                             .build();
		Customer customer2 = Customer.builder()
		                             .name("Tadeusz Norek")
		                             .build();
		Customer customer3 = Customer.builder()
		                             .name("Roman Kurski")
		                             .build();
		
		Flux<Customer> customers = Flux.just(customer1, customer2, customer3);
		
		return customerRepository.saveAll(customers)
		                         .then();
	}
	
	private Mono<Void> loadBeerData() {
		
		Beer beer1 = Beer.builder()
		                 .name("Galaxy Cat")
		                 .style("Pale Ale")
		                 .upc("12356")
		                 .price(12.99)
		                 .quantity(122)
		                 .build();
		
		Beer beer2 = Beer.builder()
		                 .name("Crank")
		                 .style("Pale Ale")
		                 .upc("12356222")
		                 .price(11.99)
		                 .quantity(392)
		                 .build();
		
		Beer beer3 = Beer.builder()
		                 .name("Sunshine City")
		                 .style("IPA")
		                 .upc("12356")
		                 .price(13.99)
		                 .quantity(144)
		                 .build();
		
		Flux<Beer> beers = Flux.just(beer1, beer2, beer3);
		
		return beerRepository.saveAll(beers)
		                     .then();
	}
}

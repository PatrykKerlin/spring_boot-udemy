package udemy.springframework.spring6reactive.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactive.model.BeerDTO;

public interface BeerService {
	
	Flux<BeerDTO> listBeers();
	
	Mono<BeerDTO> getBeerById(Integer beerId);
	
	Mono<BeerDTO> saveNewBeer(BeerDTO beerDTO);
	
	Mono<BeerDTO> updateBeerById(Integer beerId, BeerDTO beerDTO);
	
	Mono<BeerDTO> patchBeerById(Integer beerId, BeerDTO beerDTO);
	
	Mono<Void> deleteBeerById(Integer beerId);
}

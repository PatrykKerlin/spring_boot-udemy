package udemy.springframework.spring6reactive.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactive.model.BeerDTO;
import udemy.springframework.spring6reactive.services.BeerService;

@RequiredArgsConstructor
@RestController
public class BeerController {
	
	public static final String BEER_PATH = "/api/v2/beer";
	public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";
	
	private final BeerService beerService;
	
	@DeleteMapping(BEER_PATH_ID)
	Mono<ResponseEntity<Void>> deleteExistingBeer(@PathVariable("beerId") Integer beerId) {
		
		return beerService.getBeerById(beerId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(beerDTO -> beerService.deleteBeerById(beerDTO.getId()))
				.thenReturn(ResponseEntity.noContent()
						.build());
	}
	
	@PatchMapping(BEER_PATH_ID)
	Mono<ResponseEntity<Void>> patchExistingBeer(@PathVariable("beerId") Integer beerId,
			@Validated @RequestBody BeerDTO beerDTO) {
		
		return beerService.patchBeerById(beerId, beerDTO)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(savedDTO -> ResponseEntity.ok()
						.build());
	}
	
	@PutMapping(BEER_PATH_ID)
	Mono<ResponseEntity<Void>> updateExistingBeer(@PathVariable("beerId") Integer beerId,
			@Validated @RequestBody BeerDTO beerDTO) {
		
		return beerService.updateBeerById(beerId, beerDTO)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(savedDTO -> ResponseEntity.noContent()
						.build());
	}
	
	@PostMapping(BEER_PATH)
	Mono<ResponseEntity<Void>> createNewBeer(@Validated @RequestBody BeerDTO beerDTO) {
		
		return beerService.saveNewBeer(beerDTO)
				.map(savedDTO -> ResponseEntity.created(
								UriComponentsBuilder.fromHttpUrl("http://localhost:8080/" + BEER_PATH + "/" + savedDTO.getId())
										.build()
										.toUri())
						.build());
	}
	
	@GetMapping(BEER_PATH_ID)
	Mono<BeerDTO> getBeerById(@PathVariable("beerId") Integer beerId) {
		
		return beerService.getBeerById(beerId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}
	
	@GetMapping(BEER_PATH)
	Flux<BeerDTO> listBeers() {
		
		return beerService.listBeers();
	}
}

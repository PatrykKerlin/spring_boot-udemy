package udemy.springframework.rest_mvc.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import udemy.springframework.rest_mvc.model.BeerDTO;
import udemy.springframework.rest_mvc.model.BeerStyle;
import udemy.springframework.rest_mvc.services.BeerService;

import java.util.UUID;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@RequiredArgsConstructor
@RestController
// @RequestMapping(value = "/api/v1/beer")
public class BeerController {
	
	public static final String BEER_PATH = "/api/v1/beer";
	public static final String BEER_PATH_ID = "/api/v1/beer" + "/{beerId}";
	
	private final BeerService beerModelService;
	
	@PatchMapping(value = BEER_PATH_ID)
	public ResponseEntity<Void> patchBeerById(@PathVariable(value = "beerId") UUID beerId, @RequestBody BeerDTO beer) {
		
		if (beerModelService.patchRecordById(beerId, beer)
				.isEmpty()) {
			throw new NotFoundException();
		}
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping(value = BEER_PATH_ID)
	public ResponseEntity<Void> deleteBeerById(@PathVariable(value = "beerId") UUID beerId) {
		
		if (!beerModelService.deleteRecordById(beerId)) {
			throw new NotFoundException();
		}
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping(value = BEER_PATH_ID)
	public ResponseEntity<Void> updateBeerById(@PathVariable(value = "beerId") UUID beerId,
	                                           @RequestBody BeerDTO beer) {
		
		if (beerModelService.updateRecordById(beerId, beer)
				.isEmpty()) {
			throw new NotFoundException();
		}
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = BEER_PATH)
	// @RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> postBeer(@Validated @RequestBody BeerDTO beer) {
		
		BeerDTO savedBeer = beerModelService.saveNewRecord(beer);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "/api/v1/beer/" + savedBeer.getId()
				.toString());
		
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}
	
	@GetMapping(value = BEER_PATH)
	// @RequestMapping(value = BEER_PATH, method = RequestMethod.GET)
	public Page<BeerDTO> listBeers(@RequestParam(required = false) String beerName,
	                               @RequestParam(required = false) BeerStyle beerStyle,
	                               @RequestParam(required = false) Boolean showInventory,
	                               @RequestParam(required = false) Integer pageNumber,
	                               @RequestParam(required = false) Integer pageSize) {
		return beerModelService.listRecords(beerName, beerStyle, showInventory, pageNumber, pageSize);
	}
	
	// @ExceptionHandler(NotFoundException.class)
	// public ResponseEntity<Void> handleNotFoundException() {
	// return ResponseEntity.notFound().build();
	// }
	
	@GetMapping(value = BEER_PATH_ID)
	// @RequestMapping(value = BEER_PATH_ID, method = RequestMethod.GET)
	public BeerDTO getBeerById(@PathVariable(value = "beerId") UUID BeerId) {
		
		log.debug("Get Beer by Id - in controller.");
		
		return beerModelService.getRecordById(BeerId)
				.orElseThrow(NotFoundException::new);
	}
}

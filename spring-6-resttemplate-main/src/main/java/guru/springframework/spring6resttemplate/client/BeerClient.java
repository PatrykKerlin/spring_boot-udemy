package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface BeerClient {
	
	void deleteBeer(UUID beerId);
	
	BeerDTO updateBeer(BeerDTO beerDTO);
	
	BeerDTO createBeer(BeerDTO beerDTO);
	
	BeerDTO getBeerById(UUID beerId);
	
	Page<BeerDTO> listBeers();
	
	Page<BeerDTO> listBeers(String beerName,
	                        BeerStyle beerStyle,
	                        Boolean showInventory,
	                        Integer pageNumber,
	                        Integer pageSize);
}

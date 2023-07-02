package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {
	
	@Autowired
	BeerClient beerClient;
	
	@Test
	void testDeleteBeer() {
		
		BeerDTO
				payload =
				BeerDTO.builder()
				       .beerName("Test Name")
				       .beerStyle(BeerStyle.ALE)
				       .price(new BigDecimal("9.99"))
				       .quantityOnHand(999)
				       .upc("123456")
				       .build();
		
		BeerDTO savedBeerDTO = beerClient.createBeer(payload);
		
		beerClient.deleteBeer(savedBeerDTO.getId());
		
		assertThrows(HttpClientErrorException.class, () -> beerClient.getBeerById(savedBeerDTO.getId()));
	}
	
	@Test
	void testUpdateBeer() {
		
		final String beerName = "Mango Boobs";
		
		Page<BeerDTO> beerDTOs = beerClient.listBeers();
		BeerDTO beerDTO = beerDTOs.getContent().get(0);
		beerDTO.setBeerName(beerName);
		
		BeerDTO newBeerDTO = beerClient.updateBeer(beerDTO);
		
		assertNotNull(newBeerDTO);
		assertEquals(beerName, newBeerDTO.getBeerName());
	}
	
	@Test
	void testCreateBeer() {
		
		BeerDTO
				payload =
				BeerDTO.builder()
				       .beerName("Test Name")
				       .beerStyle(BeerStyle.ALE)
				       .price(new BigDecimal("9.99"))
				       .quantityOnHand(999)
				       .upc("123456")
				       .build();
		
		BeerDTO savedBeerDTO = beerClient.createBeer(payload);
		assertNotNull(savedBeerDTO);
	}
	
	@Test
	void testGetBeerById() {
		
		Page<BeerDTO> beerDTOs = beerClient.listBeers();
		BeerDTO beerDTO = beerDTOs.getContent().get(0);
		BeerDTO beerDTOById = beerClient.getBeerById(beerDTO.getId());
		
		assertNotNull(beerDTOById);
		assertEquals(beerDTO, beerDTOById);
	}
	
	@Test
	void testListBeersNoBeerName() {
		
		Page<BeerDTO> beerDTOs = beerClient.listBeers();
		assertThat(beerDTOs.getTotalElements()).isGreaterThan(0);
	}
	
	@Test
	void testListBeers() {
		
		Page<BeerDTO> beerDTOs = beerClient.listBeers("ALE", null, null, null, null);
		assertEquals(636, beerDTOs.getTotalElements());
	}
}
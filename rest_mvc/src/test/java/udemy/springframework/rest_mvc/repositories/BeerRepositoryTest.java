package udemy.springframework.rest_mvc.repositories;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import udemy.springframework.rest_mvc.bootstrap.BootstrapData;
import udemy.springframework.rest_mvc.entities.Beer;
import udemy.springframework.rest_mvc.model.BeerStyle;
import udemy.springframework.rest_mvc.services.BeerCsvServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
public class BeerRepositoryTest {
	
	@Autowired
	BeerRepository beerRepository;
	
	@Test
	void testGetBeerListByName() {
		Page<Beer> list = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%", null);
		
		assertNotEquals(0, list.getContent()
				.size());
	}
	
	@Test
	void testSaveBeerNameTooLong() {
		assertThrows(ConstraintViolationException.class, () -> {
			beerRepository.save(Beer.builder()
					                    .beerName("012345678901234567890123456789012345678901234567890123456789")
					                    .beerStyle(BeerStyle.ALE)
					                    .upc("1666161881")
					                    .price(new BigDecimal("12.5"))
					                    .build());
			
			beerRepository.flush();
		});
	}
	
	@Test
	void testSaveBeer() {
		Beer beerEntity = beerRepository.save(Beer.builder()
				                                      .beerName("Test Beer")
				                                      .beerStyle(BeerStyle.ALE)
				                                      .upc("1666161881")
				                                      .price(new BigDecimal("12.5"))
				                                      .build());
		
		beerRepository.flush();
		
		assertNotNull(beerEntity);
		assertNotNull(beerEntity.getId());
	}
}

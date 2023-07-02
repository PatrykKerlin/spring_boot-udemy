package udemy.springframework.spring6reactive.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import udemy.springframework.spring6reactive.mappers.BeerMapper;
import udemy.springframework.spring6reactive.mappers.BeerMapperImpl;
import udemy.springframework.spring6reactive.model.BeerDTO;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

@DataR2dbcTest
@Import(BeerMapperImpl.class)
public class BeerRepositoryTest {
	
	@Autowired
	BeerRepository beerRepository;
	
	@Autowired
	BeerMapper beerMapper;
	
	public static BeerDTO getTestBeer() {
		
		return BeerDTO.builder()
		              .name("Test")
		              .style("Test")
		              .upc("Test")
		              .quantity(999)
		              .price(9.99)
		              .build();
	}
	
	@Test
	void testCreateJson() throws JsonProcessingException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		System.out.println(objectMapper.writeValueAsString(getTestBeer()));
	}
	
	@Test
	void saveNewBeer() {
		
		AtomicBoolean atomicBoolean = new AtomicBoolean(false);
		
		beerRepository.save(beerMapper.beerDTOToBeer(getTestBeer()))
		              .subscribe(beer -> {
			              try {
				              Thread.sleep(5000);
			              } catch (InterruptedException e) {
				              throw new RuntimeException(e);
			              }
			              System.out.println(beer.toString());
			              atomicBoolean.set(true);
		              });
		
		await().untilTrue(atomicBoolean);
	}
}
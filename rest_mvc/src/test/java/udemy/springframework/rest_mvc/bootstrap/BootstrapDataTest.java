package udemy.springframework.rest_mvc.bootstrap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import udemy.springframework.rest_mvc.repositories.BeerRepository;
import udemy.springframework.rest_mvc.repositories.CustomerRepository;
import udemy.springframework.rest_mvc.services.BeerCsvService;
import udemy.springframework.rest_mvc.services.BeerCsvServiceImpl;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
@Import(BeerCsvServiceImpl.class)
public class BootstrapDataTest {
	
	@Autowired
	BeerRepository beerRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	BeerCsvService beerCsvService;
	
	BootstrapData bootstrapData;
	
	@BeforeEach
	void setUp() {
		bootstrapData = new BootstrapData(beerRepository, customerRepository, beerCsvService);
	}
	
	@Test
	void testRun() throws Exception {
		bootstrapData.run();
		
		assertNotEquals(0, beerRepository.count());
		assertNotEquals(0, customerRepository.count());
	}
}

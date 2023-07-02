package udemy.springframework.rest_mvc.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import udemy.springframework.rest_mvc.entities.Beer;
import udemy.springframework.rest_mvc.entities.BeerOrder;
import udemy.springframework.rest_mvc.entities.BeerOrderShipment;
import udemy.springframework.rest_mvc.entities.Customer;

@SpringBootTest
class BeerOrderRepositoryTest {
	
	@Autowired
	BeerOrderRepository beerOrderRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	BeerRepository beerRepository;
	
	Customer testCustomer;
	Beer testBeer;
	
	@BeforeEach
	void setUp() {
		testCustomer = customerRepository.findAll().get(0);
		testBeer = beerRepository.findAll().get(0);
	}
	
	@Transactional
	@Test
	void testBeerOrders() {
		BeerOrder beerOrderEntity = BeerOrder.builder()
		                                     .customerRef("Test")
		                                     .customer(testCustomer)
		                                     .beerOrderShipment(BeerOrderShipment.builder()
		                                                                         .trackingNumber("123456789")
		                                                                         .build())
		                                     .build();
		
		BeerOrder savedBeerOrderEntity = beerOrderRepository.save(beerOrderEntity);
	}
}
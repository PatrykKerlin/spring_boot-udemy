package udemy.springframework.rest_mvc.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import udemy.springframework.rest_mvc.entities.Customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CustomerRepositoryTest {
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Test
	void testSaveCustomer() {
		String name = "Test Customer";
		
		Customer customerEntity = customerRepository.save(Customer.builder()
				                                                  .name(name)
				                                                  .build());
		
		assertNotNull(customerEntity);
		assertNotNull(customerEntity.getId());
		assertEquals(customerEntity.getName(), name);
	}
}

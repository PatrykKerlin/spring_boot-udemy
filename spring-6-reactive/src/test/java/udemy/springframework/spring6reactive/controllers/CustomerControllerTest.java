package udemy.springframework.spring6reactive.controllers;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactive.model.BeerDTO;
import udemy.springframework.spring6reactive.model.CustomerDTO;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class CustomerControllerTest {
	
	@Autowired
	WebTestClient webTestClient;
	
	public static CustomerDTO getTestCustomer() {
		
		return CustomerDTO.builder()
		                  .name("Test")
		                  .build();
	}
	
	@Test
	@Order(1)
	void testListCustomers() {
		
		webTestClient.get()
		             .uri(CustomerController.CUSTOMER_PATH)
		             .exchange()
		             .expectStatus()
		             .isOk()
		             .expectHeader()
		             .valueEquals("Content-type", "application/json")
		             .expectBody()
		             .jsonPath("$.size()")
		             .isEqualTo(3);
	}
	
	@Test
	@Order(2)
	void testGetCustomerByID() {
		
		webTestClient.get()
		             .uri(CustomerController.CUSTOMER_PATH_ID, 1)
		             .exchange()
		             .expectStatus()
		             .isOk()
		             .expectHeader()
		             .valueEquals("Content-type", "application/json")
		             .expectBody(BeerDTO.class);
	}
	
	@Test
	@Order(3)
	void testCreateNewCustomer() {
		
		webTestClient.post()
		             .uri(CustomerController.CUSTOMER_PATH)
		             .body(Mono.just(getTestCustomer()), CustomerDTO.class)
		             .header("Content-type", "application/json")
		             .exchange()
		             .expectStatus()
		             .isCreated()
		             .expectHeader()
		             .location("/api/v2/customer/4");
	}
	
	@Test
	@Order(4)
	void testUpdateExistingCustomer() {
		
		webTestClient.put()
		             .uri(CustomerController.CUSTOMER_PATH_ID, 1)
		             .body(Mono.just(getTestCustomer()), CustomerDTO.class)
		             .header("Content-type", "application/json")
		             .exchange()
		             .expectStatus()
		             .isNoContent();
	}
	
	@Test
	@Order(5)
	void testPatchExistingXCustomer() {
		
		webTestClient.patch()
		             .uri(CustomerController.CUSTOMER_PATH_ID, 1)
		             .body(Mono.just(getTestCustomer()), CustomerDTO.class)
		             .header("Content-type", "application/json")
		             .exchange()
		             .expectStatus()
		             .isNoContent();
	}
	
	@Test
	@Order(6)
	void testDeleteExistingCustomer() {
		
		webTestClient.delete()
		             .uri(CustomerController.CUSTOMER_PATH_ID, 1)
		             .exchange()
		             .expectStatus()
		             .isNoContent();
	}
}
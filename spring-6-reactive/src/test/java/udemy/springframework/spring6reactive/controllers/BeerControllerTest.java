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
import udemy.springframework.spring6reactive.repositories.BeerRepositoryTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class BeerControllerTest {
	
	@Autowired
	WebTestClient webTestClient;
	
	@Test
	@Order(1)
	void testListBeers() {
		
		webTestClient.get()
		             .uri(BeerController.BEER_PATH)
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
	void testGetBeerByID() {
		
		webTestClient.get()
		             .uri(BeerController.BEER_PATH_ID, 1)
		             .exchange()
		             .expectStatus()
		             .isOk()
		             .expectHeader()
		             .valueEquals("Content-type", "application/json")
		             .expectBody(BeerDTO.class);
	}
	
	@Test
	@Order(3)
	void testCreateNewBeer() {
		
		webTestClient.post()
		             .uri(BeerController.BEER_PATH)
		             .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
		             .header("Content-type", "application/json")
		             .exchange()
		             .expectStatus()
		             .isCreated()
		             .expectHeader()
		             .location("http://localhost:8080/api/v2/beer/4");
	}
	
	@Test
	@Order(4)
	void testUpdateBeer() {
		
		webTestClient.put()
		             .uri(BeerController.BEER_PATH_ID, 1)
		             .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
		             .header("Content-type", "application/json")
		             .exchange()
		             .expectStatus()
		             .isNoContent();
	}
	
	@Test
	@Order(5)
	void testDeleteExistingBeer() {
		
		webTestClient.delete()
		             .uri(BeerController.BEER_PATH_ID, 1)
		             .exchange()
		             .expectStatus()
		             .isNoContent();
	}
	
	@Test
	void testCreateNewBeerBadData() {
		
		BeerDTO testBeerDTO = BeerRepositoryTest.getTestBeer();
		testBeerDTO.setName("");
		
		webTestClient.post()
		             .uri(BeerController.BEER_PATH)
		             .body(Mono.just(testBeerDTO), BeerDTO.class)
		             .header("Content-type", "application/json")
		             .exchange()
		             .expectStatus()
		             .isBadRequest();
	}
	
	@Test
	void testUpdateBeerBadData() {
		
		BeerDTO testBeerDTO = BeerRepositoryTest.getTestBeer();
		testBeerDTO.setStyle("");
		
		webTestClient.put()
		             .uri(BeerController.BEER_PATH_ID, 1)
		             .body(Mono.just(testBeerDTO), BeerDTO.class)
		             .header("Content-type", "application/json")
		             .exchange()
		             .expectStatus()
		             .isBadRequest();
	}
	
	@Test
	void testGetBeerByIDNotFound() {
		
		webTestClient.get()
		             .uri(BeerController.BEER_PATH_ID, 999)
		             .exchange()
		             .expectStatus()
		             .isNotFound();
	}
	
	@Test
	void testUpdateBeerNotFound() {
		
		webTestClient.put()
		             .uri(BeerController.BEER_PATH_ID, 999)
		             .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
		             .header("Content-type", "application/json")
		             .exchange()
		             .expectStatus()
		             .isNotFound();
	}
	
	@Test
	void testDeleteExistingBeerNotFound() {
		
		webTestClient.delete()
		             .uri(BeerController.BEER_PATH_ID, 999)
		             .exchange()
		             .expectStatus()
		             .isNotFound();
	}
}
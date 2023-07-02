package udemy.springframework.spring6reactive.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactive.domain.Beer;
import udemy.springframework.spring6reactive.mappers.BeerMapper;
import udemy.springframework.spring6reactive.model.BeerDTO;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BeerServiceImplTest {
	
	@Autowired
	BeerService beerService;
	
	@Autowired
	BeerMapper beerMapper;
	
	BeerDTO beerDTO;
	
	public static Beer getTestBeer() {
		
		return Beer.builder()
				.name("Test")
				.style("Test")
				.price(9.99)
				.quantity(9)
				.upc("524163")
				.build();
	}
	
	@BeforeEach
	void setUp() {
		
		beerDTO = beerMapper.beerToBeerDTO(getTestBeer());
	}
	
	@Test
	@DisplayName("Test Save Beer Using Subscriber")
	void testSaveBeerUseSubscriber() {
		
		AtomicBoolean atomicBoolean = new AtomicBoolean(false);
		AtomicReference<BeerDTO> atomicDTO = new AtomicReference<>();
		
		Mono<BeerDTO> savedMono = beerService.saveNewBeer(beerDTO);
		
		savedMono.subscribe(savedDTO -> {
			System.out.println(savedDTO.getId());
			atomicBoolean.set(true);
			atomicDTO.set(savedDTO);
		});
		
		await().untilTrue(atomicBoolean);
		
		BeerDTO persistedDTO = atomicDTO.get();
		
		assertNotNull(persistedDTO);
		assertNotNull(persistedDTO.getId());
	}
	
	@Test
	@DisplayName("Test Save Beer Using Block")
	void testSaveBeerUseBlock() {
		
		BeerDTO savedDTO = beerService.saveNewBeer(Mono.just(getTestBeerDTO())
						.block())
				.block();
		
		assertNotNull(savedDTO);
		assertNotNull(savedDTO.getId());
	}
	
	@Test
	@DisplayName("Test Update Beer Using Block")
	void testUpdateBeerUseBlock() {
		
		final String updatedName = "Updated Test";
		
		BeerDTO savedDTO = getSavedBeerDTO();
		savedDTO.setName(updatedName);
		
		BeerDTO updatedDTO = beerService.saveNewBeer(savedDTO)
				.block();
		
		assert updatedDTO != null;
		BeerDTO fetchedDTO = beerService.getBeerById(updatedDTO.getId())
				.block();
		assert fetchedDTO != null;
		assertEquals(updatedName, fetchedDTO.getName());
	}
	
	@Test
	@DisplayName("Test Update Beer Using Reactive Streams")
	void testUpdateBeerUseStreaming() {
		
		final String updatedName = "Updated Test";
		
		AtomicReference<BeerDTO> atomicDTO = new AtomicReference<>();
		
		beerService.saveNewBeer(getTestBeerDTO())
				.map(savedDTO -> {
					savedDTO.setName(updatedName);
					return savedDTO;
				})
				.flatMap(beerService::saveNewBeer)
				.flatMap(savedUpdatedDTO -> beerService.getBeerById(savedUpdatedDTO.getId()))
				.subscribe(atomicDTO::set);
		
		await().until(() -> atomicDTO.get() != null);
		
		assertEquals(updatedName, atomicDTO.get()
				.getName());
	}
	
	@Test
	void testDeleteBeer() {
		
		BeerDTO beerToDelete = getSavedBeerDTO();
		
		beerService.deleteBeerById(beerToDelete.getId())
				.block();
		
		Mono<BeerDTO> expectedEmptyMono = beerService.getBeerById(beerToDelete.getId());
		
		BeerDTO emptyBeer = expectedEmptyMono.block();
		
		assertNull(emptyBeer);
	}
	
	public BeerDTO getSavedBeerDTO() {
		
		return beerService.saveNewBeer(Mono.just(getTestBeerDTO())
						.block())
				.block();
	}
	
	public BeerDTO getTestBeerDTO() {
		
		return beerMapper.beerToBeerDTO(getTestBeer());
	}
}
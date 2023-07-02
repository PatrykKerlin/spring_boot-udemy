package udemy.springframework.rest_mvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import udemy.springframework.rest_mvc.controllers.BeerController;
import udemy.springframework.rest_mvc.controllers.NotFoundException;
import udemy.springframework.rest_mvc.entities.Beer;
import udemy.springframework.rest_mvc.mappers.BeerMapper;
import udemy.springframework.rest_mvc.model.BeerDTO;
import udemy.springframework.rest_mvc.model.BeerStyle;
import udemy.springframework.rest_mvc.repositories.BeerRepository;
import udemy.springframework.rest_mvc.services.BeerService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerControllerIntegrationTest {
	
	@Autowired
	BeerController beerController;
	
	@Autowired
	BeerRepository beerRepository;
	
	@Autowired
	BeerMapper beerMapper;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	BeerService modelService;
	
	@Autowired
	WebApplicationContext wac;
	
	MockMvc mockMvc;
	
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
	}
	
	@Test
	void testNoAuth() throws Exception {
		
		mockMvc.perform(get(BeerController.BEER_PATH).queryParam("beerStyle", BeerStyle.IPA.name()))
		       .andExpect(status().isUnauthorized());
	}
	
	@Test
	void testUpdateBeerBadVersion() throws Exception {
		Beer beerEntity = beerRepository.findAll().get(0);
		
		BeerDTO beerDTO = beerMapper.beerEntityToBeerDto(beerEntity);
		
		beerDTO.setBeerName("Updated_1");
		
		MvcResult
				mvcResult1 =
				mockMvc.perform(put(BeerController.BEER_PATH_ID,
				                    beerEntity.getId()).with(BeerControllerTest.jwtRequestPostProcessor)
				                                       .contentType(MediaType.APPLICATION_JSON)
				                                       .accept(MediaType.APPLICATION_JSON)
				                                       .content(objectMapper.writeValueAsString(beerDTO)))
				       .andExpect(status().isNoContent())
				       .andReturn();
		
		System.out.println(mvcResult1.getResponse().getContentAsString());
		
		beerDTO.setBeerName("Updated_1");
		
		try {
			MvcResult
					mvcResult2 =
					mockMvc.perform(put(BeerController.BEER_PATH_ID,
					                    beerEntity.getId()).with(BeerControllerTest.jwtRequestPostProcessor)
					                                       .contentType(MediaType.APPLICATION_JSON)
					                                       .accept(MediaType.APPLICATION_JSON)
					                                       .content(objectMapper.writeValueAsString(beerDTO)))
					       .andExpect(status().isNoContent())
					       .andReturn();
			
			System.out.println(mvcResult2.getResponse().getContentAsString());
		} catch (Exception e) {
			assertTrue(e instanceof ServletException);
		}
	}
	
	@Test
	void testListBeersLimit1000() throws Exception {
		mockMvc.perform(get(BeerController.BEER_PATH).queryParam("pageSize", "1500")
		                                             .with(BeerControllerTest.jwtRequestPostProcessor))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.content.size()", is(1000)));
	}
	
	@Test
	void testListBeersByNameAndStyleShowInventoryTruePage2() throws Exception {
		mockMvc.perform(get(BeerController.BEER_PATH).queryParam("beerName", "IPA")
		                                             .queryParam("beerStyle", BeerStyle.IPA.name())
		                                             .queryParam("showInventory", "true")
		                                             .queryParam("pageNumber", "2")
		                                             .queryParam("pageSize", "50")
		                                             .with(BeerControllerTest.jwtRequestPostProcessor))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.content.size()", is(50)))
		       .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
	}
	
	@Test
	void testListBeersByNameAndStyleShowInventoryTrue() throws Exception {
		mockMvc.perform(get(BeerController.BEER_PATH).queryParam("beerName", "IPA")
		                                             .queryParam("beerStyle", BeerStyle.IPA.name())
		                                             .queryParam("showInventory", "true")
		                                             .with(BeerControllerTest.jwtRequestPostProcessor))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.content.size()", is(25)))
		       .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
	}
	
	@Test
	void testListBeersByNameAndStyleShowInventoryFalse() throws Exception {
		mockMvc.perform(get(BeerController.BEER_PATH).queryParam("beerName", "IPA")
		                                             .queryParam("beerStyle", BeerStyle.IPA.name())
		                                             .queryParam("showInventory", "false")
		                                             .with(BeerControllerTest.jwtRequestPostProcessor))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.content.size()", is(25)))
		       .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.nullValue()));
	}
	
	@Test
	void testListBeersByNameAndStyle() throws Exception {
		mockMvc.perform(get(BeerController.BEER_PATH).queryParam("beerName", "IPA")
		                                             .queryParam("beerStyle", BeerStyle.IPA.name())
		                                             .with(BeerControllerTest.jwtRequestPostProcessor))
		       .andExpect(jsonPath("$.content.size()", is(25)));
	}
	
	@Test
	void testListBeersByStyle() throws Exception {
		mockMvc.perform(get(BeerController.BEER_PATH).queryParam("beerStyle", BeerStyle.IPA.name())
		                                             .with(BeerControllerTest.jwtRequestPostProcessor))
		       .andExpect(jsonPath("$.content.size()", is(25)));
	}
	
	@Test
	void testListBeersByName() throws Exception {
		mockMvc.perform(get(BeerController.BEER_PATH).queryParam("beerName", "IPA")
		                                             .with(BeerControllerTest.jwtRequestPostProcessor))
		       .andExpect(jsonPath("$.content.size()", is(25)));
	}
	
	@Test
	void testPatchBeerBadName() throws Exception {
		Beer beerEntity = beerRepository.findAll().get(0);
		
		Map<String, Object> beerMap = new HashMap<>();
		beerMap.put("beerName", "012345678901234567890123456789012345678901234567890123456789");
		
		MvcResult
				mvcResult =
				mockMvc.perform(patch(BeerController.BEER_PATH_ID,
				                      beerEntity.getId()).with(BeerControllerTest.jwtRequestPostProcessor)
				                                         .accept(MediaType.APPLICATION_JSON)
				                                         .contentType(MediaType.APPLICATION_JSON)
				                                         .content(objectMapper.writeValueAsString(beerMap)))
				       .andExpect(status().isBadRequest())
				       .andReturn();
		
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	void testPatchBeerByIdNotFound() {
		assertThrows(NotFoundException.class,
		             () -> beerController.patchBeerById(UUID.randomUUID(), BeerDTO.builder().build()));
	}
	
	@Rollback
	@Transactional
	@Test
	void testPatchBeerById() {
		Beer beerEntity = beerRepository.findAll().get(0);
		
		final String beerName = "Test";
		BeerDTO beerDTO = beerMapper.beerEntityToBeerDto(beerEntity);
		beerDTO.setId(null);
		beerDTO.setVersion(null);
		beerDTO.setBeerName(beerName);
		
		ResponseEntity<Void> responseEntity = beerController.patchBeerById(beerEntity.getId(), beerDTO);
		assertEquals(HttpStatusCode.valueOf(204), responseEntity.getStatusCode());
		Beer updatedBeerEntity = beerRepository.findById(beerEntity.getId()).orElse(null);
		assertNotNull(updatedBeerEntity);
		assertEquals(beerName, updatedBeerEntity.getBeerName());
		assertEquals(beerEntity.getBeerStyle(), updatedBeerEntity.getBeerStyle());
		assertNotNull(updatedBeerEntity.getUpdatedDate());
		assertEquals(beerEntity.getId(), updatedBeerEntity.getId());
	}
	
	@Test
	void deleteBeerNotFound() {
		assertThrows(NotFoundException.class, () -> beerController.deleteBeerById(UUID.randomUUID()));
	}
	
	@Rollback
	@Transactional
	@Test
	void deleteBeerById() {
		Beer beerEntity = beerRepository.findAll().get(0);
		
		ResponseEntity<Void> responseEntity = beerController.deleteBeerById(beerEntity.getId());
		Beer foundBeerEntity = beerRepository.findById(beerEntity.getId()).orElse(null);
		
		assertNull(foundBeerEntity);
		assertEquals(HttpStatusCode.valueOf(204), responseEntity.getStatusCode());
	}
	
	@Test
	void testUpdateNotFound() {
		assertThrows(NotFoundException.class,
		             () -> beerController.updateBeerById(UUID.randomUUID(), BeerDTO.builder().build()));
	}
	
	@Rollback
	@Transactional
	@Test
	void updateExistingBeer() {
		Beer beerEntity = beerRepository.findAll().get(0);
		
		final String beerName = "Test";
		BeerDTO beerDTO = beerMapper.beerEntityToBeerDto(beerEntity);
		beerDTO.setBeerName(beerName);
		beerDTO.setId(null);
		beerDTO.setVersion(null);
		beerDTO.setCreatedDate(null);
		beerDTO.setUpdatedDate(null);
		
		ResponseEntity<Void> responseEntity = beerController.updateBeerById(beerEntity.getId(), beerDTO);
		assertEquals(HttpStatusCode.valueOf(204), responseEntity.getStatusCode());
		Beer updatedBeerEntity = beerRepository.findById(beerEntity.getId()).orElse(null);
		assertNotNull(updatedBeerEntity);
		assertEquals(beerName, updatedBeerEntity.getBeerName());
		assertNotNull(updatedBeerEntity.getUpdatedDate());
	}
	
	@Rollback
	@Transactional
	@Test
	void saveNewBeer() {
		BeerDTO beerDTO = BeerDTO.builder().beerName("Test Beer").build();
		
		ResponseEntity<Void> responseEntity = beerController.postBeer(beerDTO);
		
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getHeaders().getLocation());
		
		String idString = responseEntity.getHeaders().getLocation().getPath().split("/")[4];
		UUID id = UUID.fromString(idString);
		Beer beer = beerRepository.findById(id).orElse(null);
		
		assertNotNull(beer);
	}
	
	@Test
	void testListBeers() {
		Page<BeerDTO> dtos = beerController.listBeers(null, null, false, 1, 25);
		
		assertNotEquals(0, dtos.getContent().size());
	}
	
	@Rollback
	@Transactional
	@Test
	void testEmptyList() {
		beerRepository.deleteAll();
		Page<BeerDTO> dtos = beerController.listBeers(null, null, false, 1, 25);
		
		assertEquals(0, dtos.getContent().size());
	}
	
	@Test
	void testGetBeerById() {
		Beer beerEntity = beerRepository.findAll().get(0);
		
		BeerDTO dto = beerController.getBeerById(beerEntity.getId());
		
		assertNotNull(dto);
	}
	
	@Test
	void testGetBeerByIdNotFound() {
		assertThrows(NotFoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
	}
}

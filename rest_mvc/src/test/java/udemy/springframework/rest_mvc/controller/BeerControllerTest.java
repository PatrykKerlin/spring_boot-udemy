package udemy.springframework.rest_mvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import udemy.springframework.rest_mvc.config.SpringSecurityConfig;
import udemy.springframework.rest_mvc.controllers.BeerController;
import udemy.springframework.rest_mvc.model.BeerDTO;
import udemy.springframework.rest_mvc.services.BeerService;
import udemy.springframework.rest_mvc.services.BeerServiceImpl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
@Import(SpringSecurityConfig.class)
public class BeerControllerTest {
	
	public static final String USERNAME = "user";
	public static final String PASSWORD = "password";
	public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor
			jwtRequestPostProcessor =
			jwt().jwt(jwt -> {
				jwt.claims(claims -> {
					claims.put("scope", "message-read");
					claims.put("scope", "message-write");
				}).subject("messaging-client").notBefore(Instant.now().minusSeconds(5));
			});
	
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@MockBean
	BeerService modelService;
	BeerServiceImpl beerModelService;
	BeerDTO beer;
	ArgumentCaptor<UUID> uuidArgumentCaptor;
	ArgumentCaptor<BeerDTO> beerArgumentCaptor;
	
	@BeforeEach
	void setUp() {
		beerModelService = new BeerServiceImpl();
		beer = beerModelService.listRecords(null, null, false, 1, 25).getContent().get(0);
		
		uuidArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
		beerArgumentCaptor = ArgumentCaptor.forClass(BeerDTO.class);
	}
	
	@Test
	void testCreateBeerNullBeerName() throws Exception {
		BeerDTO beerDTO = BeerDTO.builder().build();
		
		given(modelService.saveNewRecord(any(BeerDTO.class))).willReturn(beerModelService.listRecords(null,
		                                                                                              null,
		                                                                                              false,
		                                                                                              1,
		                                                                                              25)
		                                                                                 .getContent()
		                                                                                 .get(1));
		
		MvcResult
				mvcResult =
				mockMvc.perform(post(BeerController.BEER_PATH).with(jwtRequestPostProcessor)
				                                              .accept(MediaType.APPLICATION_JSON)
				                                              .contentType(MediaType.APPLICATION_JSON)
				                                              .content(objectMapper.writeValueAsString(beerDTO)))
				       .andExpect(status().isBadRequest())
				       .andExpect(jsonPath("$.length()", is(6)))
				       .andReturn();
		
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	void getBeerByIdNotFound() throws Exception {
		
		given(modelService.getRecordById(any(UUID.class))).willReturn(Optional.empty());
		
		mockMvc.perform(get(BeerController.BEER_PATH_ID, UUID.randomUUID()).with(jwtRequestPostProcessor))
		       .andExpect(status().isNotFound());
	}
	
	@Test
	void testPatchBeer() throws Exception {
		
		given(modelService.patchRecordById(any(UUID.class),
		                                   any(BeerDTO.class))).willReturn(Optional.of(BeerDTO.builder().build()));
		
		Map<String, Object> beerMap = new HashMap<>();
		beerMap.put("beerName", "New Name");
		
		mockMvc.perform(patch(BeerController.BEER_PATH_ID, beer.getId()).accept(MediaType.APPLICATION_JSON)
		                                                                .with(jwtRequestPostProcessor)
		                                                                .contentType(MediaType.APPLICATION_JSON)
		                                                                .content(objectMapper.writeValueAsString(beerMap)))
		       .andExpect(status().isNoContent());
		
		verify(modelService).patchRecordById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());
		
		assertEquals(beer.getId(), uuidArgumentCaptor.getValue());
		assertEquals(beerMap.get("beerName"), beerArgumentCaptor.getValue().getBeerName());
	}
	
	@Test
	void testDeleteBeer() throws Exception {
		
		given(modelService.deleteRecordById(any())).willReturn(true);
		
		mockMvc.perform(delete(BeerController.BEER_PATH_ID, beer.getId()).with(jwtRequestPostProcessor)
		                                                                 .accept(MediaType.APPLICATION_JSON))
		       .andExpect(status().isNoContent());
		
		verify(modelService).deleteRecordById(uuidArgumentCaptor.capture());
		
		assertEquals(beer.getId(), uuidArgumentCaptor.getValue());
	}
	
	@Test
	void testUpdateBeer() throws Exception {
		
		given(modelService.updateRecordById(any(), any())).willReturn(Optional.of(beer));
		
		mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId()).with(jwtRequestPostProcessor)
		                                                              .accept(MediaType.APPLICATION_JSON)
		                                                              .contentType(MediaType.APPLICATION_JSON)
		                                                              .content(objectMapper.writeValueAsString(beer)))
		       .andExpect(status().isNoContent());
		
		verify(modelService).updateRecordById(any(UUID.class), any(BeerDTO.class));
	}
	
	@Test
	void testCreateNewBeer() throws Exception {
		
		beer.setId(null);
		
		given(modelService.saveNewRecord(any(BeerDTO.class))).willReturn(beerModelService.listRecords(null,
		                                                                                              null,
		                                                                                              false,
		                                                                                              1,
		                                                                                              25)
		                                                                                 .getContent()
		                                                                                 .get(1));
		mockMvc.perform(post(BeerController.BEER_PATH).with(jwtRequestPostProcessor)
		                                              .accept(MediaType.APPLICATION_JSON)
		                                              .contentType(MediaType.APPLICATION_JSON)
		                                              .content(objectMapper.writeValueAsString(beer)))
		       .andExpect(status().isCreated())
		       .andExpect(header().exists("Location"));
	}
	
	@Test
	void testGetBeerById() throws Exception {
		
		given(modelService.getRecordById(beer.getId())).willReturn(Optional.of(beer));
		
		mockMvc.perform(get(BeerController.BEER_PATH_ID, beer.getId()).with(jwtRequestPostProcessor)
		                                                              .accept(MediaType.APPLICATION_JSON))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		       .andExpect(jsonPath("$.id", is(beer.getId().toString())))
		       .andExpect(jsonPath("$.beerName", is(beer.getBeerName())));
	}
	
	@Test
	void testListBeers() throws Exception {
		
		given(modelService.listRecords(any(), any(), any(), any(), any())).willReturn(beerModelService.listRecords(null,
		                                                                                                           null,
		                                                                                                           false,
		                                                                                                           1,
		                                                                                                           25));
		
		mockMvc.perform(get(BeerController.BEER_PATH).with(jwtRequestPostProcessor).accept(MediaType.APPLICATION_JSON))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		       .andExpect(jsonPath("$.content.length()", is(3)));
	}
}

package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.config.OAuth2ClientInterceptor;
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
class BeerClientImplMockTest {
	
	private static final String URL = "http://localhost:8080";
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	RestTemplateBuilder restTemplateBuilderConfigured;
	
	@Autowired
	ClientRegistrationRepository clientRegistrationRepository;
	
	@Mock
	RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());
	
	BeerClient beerClient;
	MockRestServiceServer server;
	BeerDTO beerDTO;
	String beerDTOJson;
	
	@MockBean
	OAuth2AuthorizedClientManager manager;
	
	@BeforeEach
	void setUp() throws JsonProcessingException {
		
		ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("springauth");
		
		OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
		                                                "test",
		                                                Instant.MIN,
		                                                Instant.MAX);
		
		when(manager.authorize(any())).thenReturn(new OAuth2AuthorizedClient(clientRegistration, "test", token));
		
		RestTemplate restTemplate = restTemplateBuilderConfigured.build();
		server = MockRestServiceServer.bindTo(restTemplate).build();
		when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
		
		beerClient = new BeerClientImpl(mockRestTemplateBuilder);
		
		beerDTO = getBeerDTO();
		beerDTOJson = objectMapper.writeValueAsString(beerDTO);
	}
	
	@Test
	void testListBeersWithQueryParams() throws JsonProcessingException {
		String response = objectMapper.writeValueAsString(getPage());
		
		URI uri = UriComponentsBuilder.fromHttpUrl(URL + BeerClientImpl.GET_BEER_PATH)
		                              .queryParam("beerName", "ALE")
		                              .build()
		                              .toUri();
		
		server.expect(method(HttpMethod.GET)).andExpect(requestTo(uri)).andExpect(header("Authorization",
		                                                                                 "Bearer test")).andExpect(
				queryParam("beerName", "ALE")).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
		
		Page<BeerDTO> responsePage = beerClient.listBeers("ALE", null, null, null, null);
		
		assertThat(responsePage.getContent().size()).isEqualTo(1);
	}
	
	@Test
	void testDeleteNotFound() {
		UUID randomUUID = UUID.randomUUID();
		
		server.expect(method(HttpMethod.DELETE)).andExpect(requestToUriTemplate(
				URL + BeerClientImpl.GET_BEER_BY_ID_PATH, randomUUID)).andRespond(withResourceNotFound());
		
		assertThrows(HttpClientErrorException.class, () -> beerClient.deleteBeer(randomUUID));
		
		server.verify();
	}
	
	@Test
	void testDeleteBeer() {
		
		server.expect(method(HttpMethod.DELETE)).andExpect(requestToUriTemplate(
				URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId())).andRespond(withNoContent());
		
		beerClient.deleteBeer(beerDTO.getId());
		
		server.verify();
	}
	
	@Test
	void testUpdateBeer() {
		
		server.expect(method(HttpMethod.PUT))
		      .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH,
		                                      beerDTO.getId()))
		      .andRespond(withNoContent());
		
		mockGetOperation();
		
		BeerDTO responseDTO = beerClient.updateBeer(beerDTO);
		
		assertThat(responseDTO.getId()).isEqualTo(beerDTO.getId());
	}
	
	@Test
	void testCreateBeer() {
		
		URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH).build(beerDTO.getId());
		
		server.expect(method(HttpMethod.POST)).andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH)).andRespond(
				withAccepted().location(uri));
		
		mockGetOperation();
		
		BeerDTO responseDTO = beerClient.createBeer(beerDTO);
		
		assertThat(responseDTO.getId()).isEqualTo(beerDTO.getId());
	}
	
	@Test
	void testGetBeerById() {
		
		mockGetOperation();
		
		BeerDTO responseDTO = beerClient.getBeerById(beerDTO.getId());
		
		assertThat(responseDTO.getId()).isEqualTo(beerDTO.getId());
	}
	
	@Test
	void testListBeers() throws JsonProcessingException {
		
		String response = objectMapper.writeValueAsString(getPage());
		
		server.expect(method(HttpMethod.GET)).andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH)).andRespond(
				withSuccess(response, MediaType.APPLICATION_JSON));
		
		Page<BeerDTO> dtos = beerClient.listBeers();
		assertThat(dtos.getContent().size()).isGreaterThan(0);
	}
	
	BeerDTO getBeerDTO() {
		return BeerDTO.builder()
		              .id(UUID.randomUUID())
		              .beerName("Test Beer")
		              .beerStyle(BeerStyle.ALE)
		              .price(new BigDecimal("9.99"))
		              .quantityOnHand(999)
		              .upc("364152")
		              .build();
	}
	
	BeerDTOPageImpl getPage() {
		return new BeerDTOPageImpl(Collections.singletonList(getBeerDTO()), 1, 25, 1);
	}
	
	private void mockGetOperation() {
		server.expect(method(HttpMethod.GET)).andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH,
		                                                                     beerDTO.getId())).andRespond(withSuccess(
				beerDTOJson,
				MediaType.APPLICATION_JSON));
	}
	
	@TestConfiguration
	public static class TestConfig {
		
		@Bean
		ClientRegistrationRepository clientRegistrationRepository() {
			return new InMemoryClientRegistrationRepository(ClientRegistration.withRegistrationId("springauth")
			                                                                  .authorizationGrantType(
					                                                                  AuthorizationGrantType.CLIENT_CREDENTIALS)
			                                                                  .clientId("test")
			                                                                  .tokenUri("test")
			                                                                  .build());
		}
		
		@Bean
		OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
			return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
		}
		
		@Bean
		OAuth2ClientInterceptor auth2ClientInterceptor(OAuth2AuthorizedClientManager manager,
		                                               ClientRegistrationRepository clientRegistrationRepository) {
			return new OAuth2ClientInterceptor(manager, clientRegistrationRepository);
		}
	}
}
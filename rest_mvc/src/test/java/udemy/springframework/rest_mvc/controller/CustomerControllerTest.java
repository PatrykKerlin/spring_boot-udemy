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
import org.springframework.test.web.servlet.MockMvc;
import udemy.springframework.rest_mvc.config.SpringSecurityConfig;
import udemy.springframework.rest_mvc.controllers.CustomerController;
import udemy.springframework.rest_mvc.model.CustomerDTO;
import udemy.springframework.rest_mvc.services.CustomerService;
import udemy.springframework.rest_mvc.services.CustomerServiceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(SpringSecurityConfig.class)
public class CustomerControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	CustomerService customerService;
	
	CustomerServiceImpl customerServiceImpl;
	CustomerDTO customerDTO;
	
	ArgumentCaptor<UUID> uuidArgumentCaptor;
	ArgumentCaptor<CustomerDTO> customerArgumentCaptor;
	
	@BeforeEach
	void setUp() {
		customerServiceImpl = new CustomerServiceImpl();
		customerDTO =
				customerServiceImpl.listRecords()
				                   .get(0);
		
		uuidArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
		customerArgumentCaptor = ArgumentCaptor.forClass(CustomerDTO.class);
	}
	
	@Test
	void testGetCustomerByIdNotFound() throws Exception {
		
		given(customerService.getRecordById(any(UUID.class))).willReturn(Optional.empty());
		
		mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID,
		                    UUID.randomUUID()).with(httpBasic(BeerControllerTest.USERNAME,
		                                                      BeerControllerTest.PASSWORD)))
		       .andExpect(status().isNotFound());
	}
	
	@Test
	void testPatchCustomer() throws Exception {
		
		given(customerService.patchRecordById(any(UUID.class), any(CustomerDTO.class))).willReturn(Optional.of(
				CustomerDTO.builder()
				           .build()));
		
		Map<String, Object> customerMap = new HashMap<>();
		customerMap.put("name", "Aneta Krzywonoga");
		
		mockMvc.perform(patch(CustomerController.CUSTOMER_PATH_ID, customerDTO.getId()).with(httpBasic(
				                                                                               BeerControllerTest.USERNAME,
				                                                                               BeerControllerTest.PASSWORD))
		                                                                               .accept(MediaType.APPLICATION_JSON)
		                                                                               .contentType(MediaType.APPLICATION_JSON)
		                                                                               .content(objectMapper.writeValueAsString(
				                                                                               customerMap)))
		       .andExpect(status().isNoContent());
		
		verify(customerService).patchRecordById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());
		
		assertEquals(customerDTO.getId(), uuidArgumentCaptor.getValue());
		assertEquals(customerMap.get("name"),
		             customerArgumentCaptor.getValue()
		                                   .getName());
	}
	
	@Test
	void testDeleteCustomer() throws Exception {
		
		given(customerService.deleteRecordById(any())).willReturn(true);
		
		mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_ID, customerDTO.getId()).with(httpBasic(
				                                                                                BeerControllerTest.USERNAME,
				                                                                                BeerControllerTest.PASSWORD))
		                                                                                .accept(MediaType.APPLICATION_JSON))
		       .andExpect(status().isNoContent());
		
		verify(customerService).deleteRecordById(uuidArgumentCaptor.capture());
		
		assertEquals(customerDTO.getId(), uuidArgumentCaptor.getValue());
	}
	
	@Test
	void testUpdateCustomer() throws Exception {
		
		given(customerService.updateRecordById(any(), any())).willReturn(Optional.of(customerDTO));
		
		mockMvc.perform(put(CustomerController.CUSTOMER_PATH_ID,
		                    customerDTO.getId()).with(httpBasic(BeerControllerTest.USERNAME,
		                                                        BeerControllerTest.PASSWORD))
		                                        .accept(MediaType.APPLICATION_JSON)
		                                        .contentType(MediaType.APPLICATION_JSON)
		                                        .content(objectMapper.writeValueAsString(customerDTO)))
		       .andExpect(status().isNoContent());
		
		verify(customerService).updateRecordById(any(UUID.class), any(CustomerDTO.class));
	}
	
	@Test
	void testCreateNewCustomer() throws Exception {
		
		customerDTO.setId(null);
		
		given(customerService.saveNewRecord(any(CustomerDTO.class))).willReturn(customerServiceImpl.listRecords()
		                                                                                           .get(1));
		mockMvc.perform(post(CustomerController.CUSTOMER_PATH).with(httpBasic(BeerControllerTest.USERNAME,
		                                                                      BeerControllerTest.PASSWORD))
		                                                      .accept(MediaType.APPLICATION_JSON)
		                                                      .contentType(MediaType.APPLICATION_JSON)
		                                                      .content(objectMapper.writeValueAsString(customerDTO)))
		       .andExpect(status().isCreated())
		       .andExpect(header().exists("Location"));
	}
	
	@Test
	void testGetCustomerById() throws Exception {
		
		given(customerService.getRecordById(customerDTO.getId())).willReturn(Optional.of(customerDTO));
		
		mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID,
		                    customerDTO.getId()).with(httpBasic(BeerControllerTest.USERNAME,
		                                                        BeerControllerTest.PASSWORD))
		                                        .accept(MediaType.APPLICATION_JSON))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		       .andExpect(jsonPath("$.id",
		                           is(customerDTO.getId()
		                                         .toString())))
		       .andExpect(jsonPath("$.name", is(customerDTO.getName())));
	}
	
	@Test
	void testListCustomers() throws Exception {
		
		given(customerService.listRecords()).willReturn(customerServiceImpl.listRecords());
		
		mockMvc.perform(get(CustomerController.CUSTOMER_PATH).with(httpBasic(BeerControllerTest.USERNAME,
		                                                                     BeerControllerTest.PASSWORD))
		                                                     .accept(MediaType.APPLICATION_JSON))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		       .andExpect(jsonPath("$.length()", is(3)));
	}
}

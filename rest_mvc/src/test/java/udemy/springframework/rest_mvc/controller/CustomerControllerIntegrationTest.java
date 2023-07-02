package udemy.springframework.rest_mvc.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import udemy.springframework.rest_mvc.controllers.CustomerController;
import udemy.springframework.rest_mvc.controllers.NotFoundException;
import udemy.springframework.rest_mvc.entities.Customer;
import udemy.springframework.rest_mvc.mappers.CustomerMapper;
import udemy.springframework.rest_mvc.model.CustomerDTO;
import udemy.springframework.rest_mvc.repositories.CustomerRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerControllerIntegrationTest {
	
	@Autowired
	CustomerController customerController;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CustomerMapper customerMapper;
	
	@Test
	void testPatchCustomerByIdNotFound() {
		assertThrows(NotFoundException.class,
		             () -> customerController.patchCustomerById(UUID.randomUUID(), CustomerDTO.builder().build()));
	}
	
	@Rollback
	@Transactional
	@Test
	void testPatchCustomerById() {
		Customer customerEntity = customerRepository.findAll().get(0);
		
		final String customerName = "Test";
		CustomerDTO customerDTO = customerMapper.customerEntityToCustomerDto(customerEntity);
		customerDTO.setId(null);
		customerDTO.setVersion(null);
		customerDTO.setName(customerName);
		
		ResponseEntity<Void> responseEntity = customerController.patchCustomerById(customerEntity.getId(), customerDTO);
		assertEquals(HttpStatusCode.valueOf(204), responseEntity.getStatusCode());
		Customer updatedCustomerEntity = customerRepository.findById(customerEntity.getId()).orElse(null);
		assertNotNull(updatedCustomerEntity);
		assertEquals(customerName, updatedCustomerEntity.getName());
		assertNotNull(updatedCustomerEntity.getUpdatedDate());
		assertEquals(customerEntity.getId(), updatedCustomerEntity.getId());
	}
	
	@Test
	void testDeleteCustomerByIdNotFound() {
		assertThrows(NotFoundException.class, () -> customerController.deleteCustomerById(UUID.randomUUID()));
	}
	
	@Rollback
	@Transactional
	@Test
	void testDeleteCustomerById() {
		Customer customerEntity = customerRepository.findAll().get(0);
		
		ResponseEntity<Void> responseEntity = customerController.deleteCustomerById(customerEntity.getId());
		Customer foundCustomerEntity = customerRepository.findById(customerEntity.getId()).orElse(null);
		assertNull(foundCustomerEntity);
		assertEquals(HttpStatusCode.valueOf(204), responseEntity.getStatusCode());
	}
	
	@Test
	void testUpdateExistingCustomerNotFound() {
		assertThrows(NotFoundException.class,
		             () -> customerController.updateCustomerById(UUID.randomUUID(), CustomerDTO.builder().build()));
	}
	
	@Rollback
	@Transactional
	@Test
	void testUpdateExistingCustomer() {
		Customer customerEntity = customerRepository.findAll().get(0);
		
		final String customerName = "Test";
		CustomerDTO customerDTO = CustomerDTO.builder().name(customerName).build();
		
		ResponseEntity<Void>
				responseEntity =
				customerController.updateCustomerById(customerEntity.getId(), customerDTO);
		assertEquals(HttpStatusCode.valueOf(204), responseEntity.getStatusCode());
		
		Customer updatetCustomerEntity = customerRepository.findById(customerEntity.getId()).orElse(null);
		assertNotNull(updatetCustomerEntity);
		assertEquals(customerName, updatetCustomerEntity.getName());
		assertNotNull(updatetCustomerEntity.getUpdatedDate());
	}
	
	@Rollback
	@Transactional
	@Test
	void testSaveNewCustomer() {
		String customerName = "Test";
		CustomerDTO customerDTO = CustomerDTO.builder().name(customerName).build();
		
		ResponseEntity<Void> responseEntity = customerController.postCustomer(customerDTO);
		assertEquals(HttpStatusCode.valueOf(201), responseEntity.getStatusCode());
		assertNotNull(responseEntity.getHeaders().getLocation());
		
		String idString = responseEntity.getHeaders().getLocation().getPath().split("/")[4];
		UUID id = UUID.fromString(idString);
		Customer customerEntity = customerRepository.findById(id).orElse(null);
		assertNotNull(customerEntity);
		assertEquals(customerName, customerEntity.getName());
	}
	
	@Test
	void testListCustomers() {
		List<CustomerDTO> dtos = customerController.listCustomers();
		
		assertEquals(3, dtos.size());
	}
	
	@Rollback
	@Transactional
	@Test
	void testEmptyListCustomers() {
		customerRepository.deleteAll();
		
		List<CustomerDTO> dtos = customerController.listCustomers();
		
		assertEquals(0, dtos.size());
	}
	
	@Test
	void testGetCustomerById() {
		Customer customer = customerRepository.findAll().get(0);
		CustomerDTO customerDTO = customerController.getCustomerById(customer.getId());
		
		assertNotNull(customerDTO);
		assertEquals(customer.getId(), customerDTO.getId());
	}
	
	@Test
	void testCustomerIdNotFound() {
		assertThrows(NotFoundException.class, () -> customerController.getCustomerById(UUID.randomUUID()));
	}
}

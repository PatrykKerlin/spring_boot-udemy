package udemy.springframework.spring6reactive.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactive.model.CustomerDTO;

public interface CustomerService {
	
	Flux<CustomerDTO> listCustomers();
	
	Mono<CustomerDTO> getCustomerByID(Integer customerID);
	
	Mono<CustomerDTO> saveNewCustomer(CustomerDTO customerDTO);
	
	Mono<CustomerDTO> updateExistingCustomer(Integer customerID, CustomerDTO customerDTO);
	
	Mono<CustomerDTO> patchExistingCustomer(Integer customerID, CustomerDTO customerDTO);
	
	Mono<Void> deleteExistingCustomer(Integer customerID);
}

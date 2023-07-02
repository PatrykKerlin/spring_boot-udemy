package udemy.springframework.spring6reactive.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactive.model.CustomerDTO;
import udemy.springframework.spring6reactive.services.CustomerService;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class CustomerController {
	
	public static final String CUSTOMER_ID = "customerID";
	public static final String CUSTOMER_PATH = "/api/v2/customer";
	public static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{" + CUSTOMER_ID + "}";
	private final CustomerService customerService;
	
	@GetMapping(CUSTOMER_PATH)
	Flux<CustomerDTO> listCustomers() {
		
		return customerService.listCustomers();
	}
	
	@GetMapping(CUSTOMER_PATH_ID)
	Mono<CustomerDTO> getCustomerByID(@PathVariable(CUSTOMER_ID) Integer customerID) {
		
		return customerService.getCustomerByID(customerID);
	}
	
	@PostMapping(CUSTOMER_PATH)
	Mono<ResponseEntity<Void>> saveNewCustomer(@Validated @RequestBody CustomerDTO customerDTO,
	                                           UriComponentsBuilder uriComponentsBuilder) {
		
		return customerService.saveNewCustomer(customerDTO)
		                      .map(savedCustomer -> {
			                      String uri = uriComponentsBuilder.path(CUSTOMER_PATH_ID)
			                                                       .buildAndExpand(savedCustomer.getId())
			                                                       .toUriString();
			                      return ResponseEntity.created(URI.create(uri))
			                                           .build();
		                      });
	}
	
	@PutMapping(CUSTOMER_PATH_ID)
	Mono<ResponseEntity<Void>> updateExistingCustomer(@PathVariable(CUSTOMER_ID) Integer customerID,
	                                                  @Validated @RequestBody CustomerDTO customerDTO) {
		
		return customerService.updateExistingCustomer(customerID, customerDTO)
		                      .map(savedCustomer -> ResponseEntity.noContent()
		                                                          .build());
	}
	
	@PatchMapping(CUSTOMER_PATH_ID)
	Mono<ResponseEntity<Void>> patchExistingCustomer(@PathVariable(CUSTOMER_ID) Integer customerID,
	                                                 @Validated @RequestBody CustomerDTO customerDTO) {
		
		return customerService.patchExistingCustomer(customerID, customerDTO)
		                      .map(savedCustomer -> ResponseEntity.noContent()
		                                                          .build());
	}
	
	@DeleteMapping(CUSTOMER_PATH_ID)
	Mono<ResponseEntity<Void>> deleteExistingCustomer(@PathVariable(CUSTOMER_ID) Integer customerID) {
		
		return customerService.deleteExistingCustomer(customerID)
		                      .thenReturn(ResponseEntity.noContent()
		                                                .build());
	}
}

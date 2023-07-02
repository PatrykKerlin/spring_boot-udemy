package udemy.springframework.rest_mvc.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udemy.springframework.rest_mvc.model.CustomerDTO;
import udemy.springframework.rest_mvc.services.CustomerService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
// @RequestMapping(value = "/api/v1/customer")
public class CustomerController {
	
	public static final String CUSTOMER_PATH = "/api/v1/customer";
	public static final String CUSTOMER_PATH_ID = "/api/v1/customer" + "/{customerId}";
	
	private final CustomerService customerService;
	
	@PatchMapping(value = CUSTOMER_PATH_ID)
	public ResponseEntity<Void> patchCustomerById(@PathVariable(value = "customerId") UUID customerId,
	                                              @RequestBody CustomerDTO customer) {
		
		if (customerService.patchRecordById(customerId, customer)
				.isEmpty()) {
			throw new NotFoundException();
		}
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping(value = CUSTOMER_PATH_ID)
	public ResponseEntity<Void> deleteCustomerById(@PathVariable(value = "customerId") UUID customerId) {
		
		if (!customerService.deleteRecordById(customerId)) {
			throw new NotFoundException();
		}
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping(value = CUSTOMER_PATH_ID)
	public ResponseEntity<Void> updateCustomerById(@PathVariable(value = "customerId") UUID customerId,
	                                               @RequestBody CustomerDTO customer) {
		
		if (customerService.updateRecordById(customerId, customer)
				.isEmpty()) {
			throw new NotFoundException();
		}
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = CUSTOMER_PATH)
	public ResponseEntity<Void> postCustomer(@RequestBody CustomerDTO customer) {
		
		CustomerDTO newCustomer = customerService.saveNewRecord(customer);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Location", "/api/v1/customer/" + newCustomer.getId()
				.toString());
		
		return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
	}
	
	@GetMapping(value = CUSTOMER_PATH)
	// @RequestMapping(method = RequestMethod.GET)
	public List<CustomerDTO> listCustomers() {
		return customerService.listRecords();
	}
	
	@GetMapping(value = CUSTOMER_PATH_ID)
	// @RequestMapping(value = CUSTOMER_PATH_ID, method = RequestMethod.GET)
	public CustomerDTO getCustomerById(@PathVariable(value = "customerId") UUID customerId) {
		return customerService.getRecordById(customerId)
				.orElseThrow(NotFoundException::new);
	}
}

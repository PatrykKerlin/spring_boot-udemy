package udemy.springframework.spring6reactive.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactive.mappers.CustomerMapper;
import udemy.springframework.spring6reactive.model.CustomerDTO;
import udemy.springframework.spring6reactive.repositories.CustomerRepository;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {
	
	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;
	
	@Override
	public Flux<CustomerDTO> listCustomers() {
		
		return customerRepository.findAll()
		                         .map(customerMapper::customerToCustomerDTO);
	}
	
	@Override
	public Mono<CustomerDTO> getCustomerByID(Integer customerID) {
		
		return customerRepository.findById(customerID)
		                         .map(customerMapper::customerToCustomerDTO);
	}
	
	@Override
	public Mono<CustomerDTO> saveNewCustomer(CustomerDTO customerDTO) {
		
		return customerRepository.save(customerMapper.customerDTOToCustomer(customerDTO))
		                         .map(customerMapper::customerToCustomerDTO);
	}
	
	@Override
	public Mono<CustomerDTO> updateExistingCustomer(Integer customerID, CustomerDTO customerDTO) {
		
		return customerRepository.findById(customerID)
		                         .map(foundCustomer -> {
			                         foundCustomer.setName(customerDTO.getName());
			                         return foundCustomer;
		                         })
		                         .flatMap(customerRepository::save)
		                         .map(customerMapper::customerToCustomerDTO);
	}
	
	@Override
	public Mono<CustomerDTO> patchExistingCustomer(Integer customerID, CustomerDTO customerDTO) {
		
		return customerRepository.findById(customerID)
		                         .map(foundCustomer -> {
			                         if (customerDTO.getName() != null) {
				                         foundCustomer.setName(customerDTO.getName());
			                         }
			                         return foundCustomer;
		                         })
		                         .flatMap(customerRepository::save)
		                         .map(customerMapper::customerToCustomerDTO);
	}
	
	@Override
	public Mono<Void> deleteExistingCustomer(Integer customerID) {
		
		return customerRepository.deleteById(customerID);
	}
}

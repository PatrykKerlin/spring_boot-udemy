package udemy.springframework.spring6reactive.mappers;

import org.mapstruct.Mapper;
import udemy.springframework.spring6reactive.domain.Customer;
import udemy.springframework.spring6reactive.model.CustomerDTO;

@Mapper
public interface CustomerMapper {
	
	Customer customerDTOToCustomer(CustomerDTO customerDTO);
	
	CustomerDTO customerToCustomerDTO(Customer customer);
}

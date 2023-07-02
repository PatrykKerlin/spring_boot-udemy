package udemy.springframework.rest_mvc.mappers;

import org.mapstruct.Mapper;
import udemy.springframework.rest_mvc.entities.Customer;
import udemy.springframework.rest_mvc.model.CustomerDTO;

@Mapper
public interface CustomerMapper {
	Customer customerDtoToCustomerEntity(CustomerDTO customerDTO);
	
	CustomerDTO customerEntityToCustomerDto(Customer customerEntity);
}

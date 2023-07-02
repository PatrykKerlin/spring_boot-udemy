package udemy.springframework.rest_mvc.services;

import udemy.springframework.rest_mvc.model.CustomerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
	List<CustomerDTO> listRecords();
	
	Optional<CustomerDTO> getRecordById(UUID id);
	
	CustomerDTO saveNewRecord(CustomerDTO record);
	
	Optional<CustomerDTO> updateRecordById(UUID id, CustomerDTO record);
	
	boolean deleteRecordById(UUID id);
	
	Optional<CustomerDTO> patchRecordById(UUID id, CustomerDTO record);
}

package udemy.springframework.rest_mvc.services;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import udemy.springframework.rest_mvc.model.CustomerDTO;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {
	
	private final Map<UUID, CustomerDTO> customerMap;
	
	public CustomerServiceImpl() {
		this.customerMap = new HashMap<>();
		
		CustomerDTO customer1 = CustomerDTO.builder()
				.id(UUID.randomUUID())
				.name("Aneta Cipa")
				.version(1)
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
		
		CustomerDTO customer2 = CustomerDTO.builder()
				.id(UUID.randomUUID())
				.name("Andrzej Kutasko")
				.version(1)
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
		
		CustomerDTO customer3 = CustomerDTO.builder()
				.id(UUID.randomUUID())
				.name("Jan Dupa")
				.version(1)
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
		
		customerMap.put(customer1.getId(), customer1);
		customerMap.put(customer2.getId(), customer2);
		customerMap.put(customer3.getId(), customer3);
	}
	
	@Override
	public Optional<CustomerDTO> getRecordById(UUID customerId) {
		return Optional.of(customerMap.get(customerId));
	}
	
	@Override
	public List<CustomerDTO> listRecords() {
		return new ArrayList<>(customerMap.values());
	}
	
	@Override
	public CustomerDTO saveNewRecord(CustomerDTO customer) {
		
		CustomerDTO newCustomer = CustomerDTO.builder()
				.id(UUID.randomUUID())
				.name(customer.getName())
				.version(customer.getVersion())
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
		
		customerMap.put(newCustomer.getId(), newCustomer);
		
		return newCustomer;
	}
	
	@Override
	public Optional<CustomerDTO> updateRecordById(UUID customerId, CustomerDTO customer) {
		
		CustomerDTO existing = customerMap.get(customerId);
		existing.setName(customer.getName());
		existing.setVersion(customer.getVersion());
		existing.setUpdatedDate(LocalDateTime.now());
		
		customerMap.put(existing.getId(), existing);
		
		return Optional.of(existing);
	}
	
	@Override
	public boolean deleteRecordById(UUID customerId) {
		if (customerMap.containsKey(customerId)) {
			customerMap.remove(customerId);
			return true;
		}
		
		return false;
	}
	
	@Override
	public Optional<CustomerDTO> patchRecordById(UUID customerId, CustomerDTO customer) {
		
		CustomerDTO existing = customerMap.get(customerId);
		
		existing.setUpdatedDate(LocalDateTime.now());
		
		if (StringUtils.hasText(customer.getName())) {
			existing.setName(customer.getName());
		}
		
		if (customer.getVersion() != null) {
			existing.setVersion(customer.getVersion());
		}
		
		customerMap.put(existing.getId(), existing);
		
		return Optional.of(existing);
	}
}

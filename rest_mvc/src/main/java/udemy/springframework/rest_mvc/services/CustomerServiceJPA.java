package udemy.springframework.rest_mvc.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import udemy.springframework.rest_mvc.mappers.CustomerMapper;
import udemy.springframework.rest_mvc.model.CustomerDTO;
import udemy.springframework.rest_mvc.repositories.CustomerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {
	
	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;
	
	@Override
	public List<CustomerDTO> listRecords() {
		return customerRepository.findAll()
				.stream()
				.map(customerMapper::customerEntityToCustomerDto)
				.collect(Collectors.toList());
	}
	
	@Override
	public Optional<CustomerDTO> getRecordById(UUID id) {
		return Optional.ofNullable(customerMapper.customerEntityToCustomerDto(customerRepository.findById(id)
				                                                                      .orElse(null)));
	}
	
	@Override
	public CustomerDTO saveNewRecord(CustomerDTO record) {
		LocalDateTime timeNow = LocalDateTime.now();
		record.setCreatedDate(timeNow);
		record.setUpdatedDate(timeNow);
		
		return customerMapper.customerEntityToCustomerDto(
				customerRepository.save(customerMapper.customerDtoToCustomerEntity(record)));
	}
	
	@Override
	public Optional<CustomerDTO> updateRecordById(UUID id, CustomerDTO record) {
		AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();
		
		customerRepository.findById(id)
				.ifPresentOrElse(foundCustomerEntity -> {
					foundCustomerEntity.setName(record.getName());
					foundCustomerEntity.setUpdatedDate(LocalDateTime.now());
					atomicReference.set(Optional.of(
							customerMapper.customerEntityToCustomerDto(customerRepository.save(foundCustomerEntity))));
				}, () -> atomicReference.set(Optional.empty()));
		
		return atomicReference.get();
	}
	
	@Override
	public boolean deleteRecordById(UUID id) {
		if (customerRepository.existsById(id)) {
			customerRepository.deleteById(id);
			return true;
		}
		
		return false;
	}
	
	@Override
	public Optional<CustomerDTO> patchRecordById(UUID id, CustomerDTO record) {
		AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();
		
		customerRepository.findById(id)
				.ifPresentOrElse(foundCustomerEntity -> {
					if (record.getName() != null) {
						foundCustomerEntity.setName(record.getName());
					}
					foundCustomerEntity.setUpdatedDate(LocalDateTime.now());
					atomicReference.set(Optional.of(
							customerMapper.customerEntityToCustomerDto(customerRepository.save(foundCustomerEntity))));
				}, () -> atomicReference.set(Optional.empty()));
		
		return atomicReference.get();
	}
}

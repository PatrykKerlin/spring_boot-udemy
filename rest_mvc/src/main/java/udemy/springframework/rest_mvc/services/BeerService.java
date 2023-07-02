package udemy.springframework.rest_mvc.services;

import org.springframework.data.domain.Page;
import udemy.springframework.rest_mvc.model.BeerDTO;
import udemy.springframework.rest_mvc.model.BeerStyle;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {
	Page<BeerDTO> listRecords(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber,
	                          Integer pageSize);
	
	Optional<BeerDTO> getRecordById(UUID id);
	
	BeerDTO saveNewRecord(BeerDTO record);
	
	Optional<BeerDTO> updateRecordById(UUID id, BeerDTO record);
	
	boolean deleteRecordById(UUID id);
	
	Optional<BeerDTO> patchRecordById(UUID id, BeerDTO record);
}

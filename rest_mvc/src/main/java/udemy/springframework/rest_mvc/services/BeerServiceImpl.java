package udemy.springframework.rest_mvc.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import udemy.springframework.rest_mvc.model.BeerDTO;
import udemy.springframework.rest_mvc.model.BeerStyle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {
	
	private final Map<UUID, BeerDTO> beerMap;
	
	public BeerServiceImpl() {
		this.beerMap = new HashMap<>();
		
		BeerDTO beer1 = BeerDTO.builder()
				.id(UUID.randomUUID())
				.version(1)
				.beerName("Galaxy Cat")
				.beerStyle(BeerStyle.PALE_ALE)
				.upc("12356")
				.price(new BigDecimal("12.99"))
				.quantityOnHand(122)
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
		
		BeerDTO beer2 = BeerDTO.builder()
				.id(UUID.randomUUID())
				.version(1)
				.beerName("Crank")
				.beerStyle(BeerStyle.PALE_ALE)
				.upc("12356222")
				.price(new BigDecimal("11.99"))
				.quantityOnHand(392)
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
		
		BeerDTO beer3 = BeerDTO.builder()
				.id(UUID.randomUUID())
				.version(1)
				.beerName("Sunshine City")
				.beerStyle(BeerStyle.IPA)
				.upc("12356")
				.price(new BigDecimal("13.99"))
				.quantityOnHand(144)
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
		
		beerMap.put(beer1.getId(), beer1);
		beerMap.put(beer2.getId(), beer2);
		beerMap.put(beer3.getId(), beer3);
	}
	
	@Override
	public Page<BeerDTO> listRecords(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber,
	                                 Integer pageSize) {
		return new PageImpl<>(new ArrayList<>(beerMap.values()));
	}
	
	@Override
	public Optional<BeerDTO> getRecordById(UUID id) {
		
		log.debug("Get Beer by Id - in service. Id: " + id.toString());
		
		return Optional.of(beerMap.get(id));
	}
	
	@Override
	public BeerDTO saveNewRecord(BeerDTO beer) {
		
		BeerDTO savedBeer = BeerDTO.builder()
				.id(UUID.randomUUID())
				.version(beer.getVersion())
				.beerName(beer.getBeerName())
				.beerStyle(beer.getBeerStyle())
				.upc(beer.getUpc())
				.price(beer.getPrice())
				.quantityOnHand(beer.getQuantityOnHand())
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
		
		beerMap.put(savedBeer.getId(), savedBeer);
		
		return savedBeer;
	}
	
	@Override
	public Optional<BeerDTO> updateRecordById(UUID beerId, BeerDTO beer) {
		
		BeerDTO existing = beerMap.get(beerId);
		existing.setVersion(beer.getVersion());
		existing.setBeerName(beer.getBeerName());
		existing.setBeerStyle(beer.getBeerStyle());
		existing.setUpc(beer.getUpc());
		existing.setPrice(beer.getPrice());
		existing.setQuantityOnHand(beer.getQuantityOnHand());
		existing.setUpdatedDate(LocalDateTime.now());
		
		beerMap.put(existing.getId(), existing);
		
		return Optional.of(existing);
	}
	
	@Override
	public boolean deleteRecordById(UUID beerId) {
		if (beerMap.containsKey(beerId)) {
			beerMap.remove(beerId);
			return true;
		}
		
		return false;
	}
	
	@Override
	public Optional<BeerDTO> patchRecordById(UUID beerId, BeerDTO beer) {
		
		BeerDTO existing = beerMap.get(beerId);
		
		existing.setUpdatedDate(LocalDateTime.now());
		
		if (beer.getVersion() != null) {
			existing.setVersion(beer.getVersion());
		}
		
		if (StringUtils.hasText(beer.getBeerName())) {
			existing.setBeerName(beer.getBeerName());
		}
		
		if (beer.getBeerStyle() != null) {
			existing.setBeerStyle(beer.getBeerStyle());
		}
		
		if (StringUtils.hasText(beer.getUpc())) {
			existing.setUpc(beer.getUpc());
		}
		
		if (beer.getPrice() != null) {
			existing.setPrice(beer.getPrice());
		}
		
		if (beer.getQuantityOnHand() != null) {
			existing.setQuantityOnHand(beer.getQuantityOnHand());
		}
		
		beerMap.put(beerId, existing);
		
		return Optional.of(existing);
	}
}

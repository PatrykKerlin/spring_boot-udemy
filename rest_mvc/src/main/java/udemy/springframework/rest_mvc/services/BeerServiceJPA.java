package udemy.springframework.rest_mvc.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import udemy.springframework.rest_mvc.entities.Beer;
import udemy.springframework.rest_mvc.mappers.BeerMapper;
import udemy.springframework.rest_mvc.model.BeerDTO;
import udemy.springframework.rest_mvc.model.BeerStyle;
import udemy.springframework.rest_mvc.repositories.BeerRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {
	
	private final static int DEFAULT_PAGE = 0;
	private final static int DEFAULT_PAGE_SIZE = 25;
	private final static int MAX_PAGE_SIZE = 1000;
	
	private final BeerRepository beerRepository;
	private final BeerMapper beerMapper;
	
	@Override
	public Page<BeerDTO> listRecords(String beerName,
	                                 BeerStyle beerStyle,
	                                 Boolean showInventory,
	                                 Integer pageNumber,
	                                 Integer pageSize) {
		PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
		Page<Beer> beerPage;
		
		if (StringUtils.hasText(beerName) && beerStyle == null) {
			beerPage = listBeersByName(beerName, pageRequest);
		} else if (!StringUtils.hasText(beerName) && beerStyle != null) {
			beerPage = listBeersByStyle(beerStyle, pageRequest);
		} else if (StringUtils.hasText(beerName) && beerStyle != null) {
			beerPage = listBeersByNameAndStyle(beerName, beerStyle, pageRequest);
		} else {
			beerPage = beerRepository.findAll(pageRequest);
		}
		
		if (showInventory != null && !showInventory) {
			beerPage.forEach(beer -> beer.setQuantityOnHand(null));
		}
		
		return beerPage.map(beerMapper::beerEntityToBeerDto);
//		return beerPage.stream().map(beerMapper::beerEntityToBeerDto).collect(Collectors.toList());
	}
	
	private PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
		int queryPageNumber;
		int queryPageSize;
		
		if (pageNumber != null && pageNumber > 0) {
			queryPageNumber = pageNumber - 1;
		} else {
			queryPageNumber = DEFAULT_PAGE;
		}
		
		if (pageSize == null) {
			queryPageSize = DEFAULT_PAGE_SIZE;
		} else {
			if (pageSize > MAX_PAGE_SIZE) {
				queryPageSize = MAX_PAGE_SIZE;
			} else {
				queryPageSize = pageSize;
			}
		}
		
		Sort sort = Sort.by(Sort.Order.asc("beerName"));
		
		return PageRequest.of(queryPageNumber, queryPageSize, sort);
	}
	
	private Page<Beer> listBeersByName(String beerName, Pageable pageable) {
		return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageable);
	}
	
	private Page<Beer> listBeersByStyle(BeerStyle beerStyle, Pageable pageable) {
		return beerRepository.findAllByBeerStyle(beerStyle, pageable);
	}
	
	private Page<Beer> listBeersByNameAndStyle(String beerName, BeerStyle beerStyle, Pageable pageable) {
		return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle, pageable);
	}
	
	@Override
	public Optional<BeerDTO> getRecordById(UUID id) {
		return Optional.ofNullable(beerMapper.beerEntityToBeerDto(beerRepository.findById(id).orElse(null)));
	}
	
	@Override
	public BeerDTO saveNewRecord(BeerDTO record) {
		LocalDateTime timeNow = LocalDateTime.now();
		record.setCreatedDate(timeNow);
		record.setUpdatedDate(timeNow);
		return beerMapper.beerEntityToBeerDto(beerRepository.save(beerMapper.beerDtoToBeerEntity(record)));
	}
	
	@Override
	public Optional<BeerDTO> updateRecordById(UUID id, BeerDTO record) {
//		return Optional.of(beerMapper.beerEntityToBeerDto(beerRepository.save(beerMapper.beerDtoToBeerEntity(record)
//		)));
		
		AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();
		
		beerRepository.findById(id).ifPresentOrElse(foundBeerEntity -> {
			foundBeerEntity.setBeerName(record.getBeerName());
			foundBeerEntity.setBeerStyle(record.getBeerStyle());
			foundBeerEntity.setUpc(record.getUpc());
			foundBeerEntity.setPrice(record.getPrice());
			foundBeerEntity.setQuantityOnHand(record.getQuantityOnHand());
			foundBeerEntity.setVersion(record.getVersion());
			atomicReference.set(Optional.of(beerMapper.beerEntityToBeerDto(beerRepository.save(foundBeerEntity))));
		}, () -> atomicReference.set(Optional.empty()));
		
		return atomicReference.get();
	}
	
	@Override
	public boolean deleteRecordById(UUID id) {
		if (beerRepository.existsById(id)) {
			beerRepository.deleteById(id);
			return true;
		}
		
		return false;
	}
	
	@Override
	public Optional<BeerDTO> patchRecordById(UUID id, BeerDTO record) {
		AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();
		
		beerRepository.findById(id).ifPresentOrElse(foundBeer -> {
			if (record.getBeerName() != null) {
				foundBeer.setBeerName(record.getBeerName());
			}
			if (record.getBeerStyle() != null) {
				foundBeer.setBeerStyle(record.getBeerStyle());
			}
			if (record.getUpc() != null) {
				foundBeer.setUpc(record.getUpc());
			}
			if (record.getQuantityOnHand() != null) {
				foundBeer.setQuantityOnHand(record.getQuantityOnHand());
			}
			if (record.getPrice() != null) {
				foundBeer.setPrice(record.getPrice());
			}
			foundBeer.setUpdatedDate(LocalDateTime.now());
			
			atomicReference.set(Optional.of(beerMapper.beerEntityToBeerDto(beerRepository.save(foundBeer))));
		}, () -> atomicReference.set(Optional.empty()));
		
		return atomicReference.get();
	}
}

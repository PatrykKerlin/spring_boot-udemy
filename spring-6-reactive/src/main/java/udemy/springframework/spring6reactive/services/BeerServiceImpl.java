package udemy.springframework.spring6reactive.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactive.mappers.BeerMapper;
import udemy.springframework.spring6reactive.model.BeerDTO;
import udemy.springframework.spring6reactive.repositories.BeerRepository;

@RequiredArgsConstructor
@Service
public class BeerServiceImpl implements BeerService {
	
	private final BeerRepository beerRepository;
	private final BeerMapper beerMapper;
	
	@Override
	public Flux<BeerDTO> listBeers() {
		
		return beerRepository.findAll()
				.map(beerMapper::beerToBeerDTO);
	}
	
	@Override
	public Mono<BeerDTO> getBeerById(Integer beerId) {
		
		return beerRepository.findById(beerId)
				.map(beerMapper::beerToBeerDTO);
	}
	
	@Override
	public Mono<BeerDTO> saveNewBeer(BeerDTO beerDTO) {
		
		return beerRepository.save(beerMapper.beerDTOToBeer(beerDTO))
				.map(beerMapper::beerToBeerDTO);
	}
	
	@Override
	public Mono<BeerDTO> updateBeerById(Integer beerId, BeerDTO beerDTO) {
		
		return beerRepository.findById(beerId)
				.map(foundBeer -> {
					foundBeer.setName(beerDTO.getName());
					foundBeer.setStyle(beerDTO.getStyle());
					foundBeer.setUpc(beerDTO.getUpc());
					foundBeer.setPrice(beerDTO.getPrice());
					foundBeer.setQuantity(beerDTO.getQuantity());
					return foundBeer;
				})
				.flatMap(beerRepository::save)
				.map(beerMapper::beerToBeerDTO);
	}
	
	@Override
	public Mono<BeerDTO> patchBeerById(Integer beerId, BeerDTO beerDTO) {
		
		return beerRepository.findById(beerId)
				.map(foundBeer -> {
					if (beerDTO.getName() != null) {
						foundBeer.setName(beerDTO.getName());
					}
					if (beerDTO.getStyle() != null) {
						foundBeer.setStyle(beerDTO.getStyle());
					}
					if (beerDTO.getUpc() != null) {
						foundBeer.setUpc(beerDTO.getUpc());
					}
					if (beerDTO.getPrice() != null) {
						foundBeer.setPrice(beerDTO.getPrice());
					}
					if (beerDTO.getQuantity() != null) {
						foundBeer.setQuantity(beerDTO.getQuantity());
					}
					return foundBeer;
				})
				.flatMap(beerRepository::save)
				.map(beerMapper::beerToBeerDTO);
	}
	
	@Override
	public Mono<Void> deleteBeerById(Integer beerId) {
		
		return beerRepository.deleteById(beerId);
	}
}

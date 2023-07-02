package udemy.springframework.spring6reactive.mappers;

import org.mapstruct.Mapper;
import udemy.springframework.spring6reactive.domain.Beer;
import udemy.springframework.spring6reactive.model.BeerDTO;

@Mapper
public interface BeerMapper {
	
	Beer beerDTOToBeer(BeerDTO beerDTO);
	
	BeerDTO beerToBeerDTO(Beer beer);
}

package udemy.springframework.rest_mvc.mappers;

import org.mapstruct.Mapper;
import udemy.springframework.rest_mvc.entities.Beer;
import udemy.springframework.rest_mvc.model.BeerDTO;

@Mapper
public interface BeerMapper {
	Beer beerDtoToBeerEntity(BeerDTO beerDto);
	
	BeerDTO beerEntityToBeerDto(Beer beerEntity);
}

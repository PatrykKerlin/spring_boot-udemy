package udemy.springframework.spring6reactive.web.fn;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class BeerRouterConfig {
	
	public static final String BEER_PATH = "/api/v3/beer";
	public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";
	
	private final BeerHandler beerHandler;
	
	@Bean
	public RouterFunction<ServerResponse> beerRoutes() {
		
		return route().GET(BEER_PATH, accept(MediaType.APPLICATION_JSON), beerHandler::listBeers)
				.GET(BEER_PATH_ID, accept(MediaType.APPLICATION_JSON), beerHandler::getBeerByID)
				.POST(BEER_PATH, accept(MediaType.APPLICATION_JSON),
						request -> beerHandler.saveNewBeer(request, UriComponentsBuilder.newInstance()))
				.PUT(BEER_PATH_ID, accept(MediaType.APPLICATION_JSON), beerHandler::updateBeerById)
				.PATCH(BEER_PATH_ID, accept(MediaType.APPLICATION_JSON), beerHandler::patchBeerById)
				.DELETE(BEER_PATH_ID, accept(MediaType.APPLICATION_JSON), beerHandler::deleteBeerById)
				.build();
	}
}

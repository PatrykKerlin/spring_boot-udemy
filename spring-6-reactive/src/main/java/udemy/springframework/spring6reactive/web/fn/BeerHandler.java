package udemy.springframework.spring6reactive.web.fn;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import udemy.springframework.spring6reactive.model.BeerDTO;
import udemy.springframework.spring6reactive.services.BeerService;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BeerHandler {
	
	private final BeerService beerService;
	
	public Mono<ServerResponse> listBeers(ServerRequest request) {
		
		return ServerResponse.ok()
				.body(beerService.listBeers(), BeerDTO.class);
	}
	
	public Mono<ServerResponse> getBeerByID(ServerRequest request) {
		
		int beerID;
		
		try {
			beerID = Integer.parseInt(request.pathVariable("beerId"));
		} catch (NumberFormatException e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", "Beer ID must be of type int. ");
			
			return ServerResponse.badRequest()
					.contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(errorResponse));
		}
		
		return ServerResponse.ok()
				.body(beerService.getBeerById(beerID), BeerDTO.class);
	}
	
	public Mono<ServerResponse> saveNewBeer(ServerRequest request, UriComponentsBuilder uriComponentsBuilder) {
		
		return request.bodyToMono(BeerDTO.class)
				.flatMap(beerService::saveNewBeer)
				.flatMap(savedBeerDTO -> {
					String uri = uriComponentsBuilder.path(BeerRouterConfig.BEER_PATH_ID)
							.buildAndExpand(savedBeerDTO.getId())
							.toUriString();
					return ServerResponse.created(URI.create(uri))
							.build();
				});
	}
	
	public Mono<ServerResponse> updateBeerById(ServerRequest request) {
		
		return request.bodyToMono(BeerDTO.class)
				.flatMap(
						beerDTO -> beerService.updateBeerById(Integer.valueOf(request.pathVariable("beerId")), beerDTO))
				.flatMap(savedDTO -> ServerResponse.noContent()
						.build());
	}
	
	public Mono<ServerResponse> patchBeerById(ServerRequest request) {
		
		return request.bodyToMono(BeerDTO.class)
				.flatMap(beerDTO -> beerService.patchBeerById(Integer.valueOf(request.pathVariable("beerId")), beerDTO))
				.flatMap(savedDTO -> ServerResponse.noContent()
						.build());
	}
	
	public Mono<ServerResponse> deleteBeerById(ServerRequest request) {
		
		return beerService.deleteBeerById(Integer.valueOf(request.pathVariable("beerId")))
				.then(ServerResponse.noContent()
						.build());
	}
}

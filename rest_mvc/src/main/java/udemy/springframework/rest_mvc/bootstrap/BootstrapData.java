package udemy.springframework.rest_mvc.bootstrap;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import udemy.springframework.rest_mvc.entities.Beer;
import udemy.springframework.rest_mvc.entities.Customer;
import udemy.springframework.rest_mvc.model.BeerCSVRecordDTO;
import udemy.springframework.rest_mvc.model.BeerStyle;
import udemy.springframework.rest_mvc.repositories.BeerRepository;
import udemy.springframework.rest_mvc.repositories.CustomerRepository;
import udemy.springframework.rest_mvc.services.BeerCsvService;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {
	
	private final BeerRepository beerRepository;
	private final CustomerRepository customerRepository;
	private final BeerCsvService beerCsvService;
	
	@Transactional
	@Override
	public void run(String... args) throws Exception {
		loadBeerData();
		loadCsvData();
		loadCustomerData();
	}
	
	private void loadCsvData() throws FileNotFoundException {
		if (beerRepository.count() < 10) {
			File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
			
			List<BeerCSVRecordDTO> records = beerCsvService.convertCSV(file);
			
			records.forEach(record -> {
				BeerStyle beerStyle = switch (record.getStyle()) {
					case "American Pale Lager" -> BeerStyle.LAGER;
					case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
							BeerStyle.ALE;
					case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
					case "American Porter" -> BeerStyle.PORTER;
					case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
					case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
					case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
					case "English Pale Ale" -> BeerStyle.PALE_ALE;
					default -> BeerStyle.PILSNER;
				};
				
				beerRepository.save(Beer.builder()
						                    .beerName(StringUtils.abbreviate(record.getBeer(), 50))
						                    .beerStyle(beerStyle)
						                    .price(BigDecimal.TEN)
						                    .upc(record.getRow()
								                         .toString())
						                    .quantityOnHand(record.getCount())
						                    .build());
			});
		}
	}
	
	private void loadBeerData() {
		if (beerRepository.count() == 0) {
			Beer beerEntity1 = Beer.builder()
					.beerName("Galaxy Cat")
					.beerStyle(BeerStyle.PALE_ALE)
					.upc("12356")
					.price(new BigDecimal("12.99"))
					.quantityOnHand(122)
					.createdDate(LocalDateTime.now())
					.updatedDate(LocalDateTime.now())
					.build();
			
			Beer beerEntity2 = Beer.builder()
					.beerName("Crank")
					.beerStyle(BeerStyle.PALE_ALE)
					.upc("12356222")
					.price(new BigDecimal("11.99"))
					.quantityOnHand(392)
					.createdDate(LocalDateTime.now())
					.updatedDate(LocalDateTime.now())
					.build();
			
			Beer beerEntity3 = Beer.builder()
					.beerName("Sunshine City")
					.beerStyle(BeerStyle.IPA)
					.upc("12356")
					.price(new BigDecimal("13.99"))
					.quantityOnHand(144)
					.createdDate(LocalDateTime.now())
					.updatedDate(LocalDateTime.now())
					.build();
			
			beerRepository.save(beerEntity1);
			beerRepository.save(beerEntity2);
			beerRepository.save(beerEntity3);
		}
	}
	
	private void loadCustomerData() {
		if (customerRepository.count() == 0) {
			Customer customerEntity1 = Customer.builder()
					.name("Aneta Cipa")
					.createdDate(LocalDateTime.now())
					.updatedDate(LocalDateTime.now())
					.build();
			
			Customer customerEntity2 = Customer.builder()
					.name("Andrzej Kutasko")
					.createdDate(LocalDateTime.now())
					.updatedDate(LocalDateTime.now())
					.build();
			
			Customer customerEntity3 = Customer.builder()
					.name("Jan Dupa")
					.createdDate(LocalDateTime.now())
					.updatedDate(LocalDateTime.now())
					.build();
			
			customerRepository.saveAll(Arrays.asList(customerEntity1, customerEntity2, customerEntity3));
		}
	}
}

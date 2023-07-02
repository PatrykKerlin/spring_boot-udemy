package udemy.springframework.rest_mvc.services;

import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import udemy.springframework.rest_mvc.model.BeerCSVRecordDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BeerCsvServiceImplTest {
	
	BeerCsvService beerCsvService = new BeerCsvServiceImpl();
	
	@Test
	void convertCSV() throws FileNotFoundException {
		File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
		
		List<BeerCSVRecordDTO> records = beerCsvService.convertCSV(file);
		
		System.out.println(records.size());
		
		assertNotEquals(0, records.size());
	}
}

package udemy.springframework.rest_mvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;
import udemy.springframework.rest_mvc.model.BeerCSVRecordDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
public class BeerCsvServiceImpl implements BeerCsvService {
	@Override
	public List<BeerCSVRecordDTO> convertCSV(File csvFile) {
		try {
			List<BeerCSVRecordDTO> beerCSVRecordDTOList =
					new CsvToBeanBuilder<BeerCSVRecordDTO>(new FileReader(csvFile)).withType(BeerCSVRecordDTO.class)
							.build()
							.parse();
			return beerCSVRecordDTOList;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}

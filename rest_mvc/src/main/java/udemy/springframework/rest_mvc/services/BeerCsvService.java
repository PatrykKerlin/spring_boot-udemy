package udemy.springframework.rest_mvc.services;

import udemy.springframework.rest_mvc.model.BeerCSVRecordDTO;

import java.io.File;
import java.util.List;

public interface BeerCsvService {
	List<BeerCSVRecordDTO> convertCSV(File csvFile);
}

package udemy.springframework.rest_mvc.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import udemy.springframework.rest_mvc.entities.Beer;
import udemy.springframework.rest_mvc.entities.Category;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CategoryRepositoryTest {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	BeerRepository beerRepository;
	
	Beer testBeerEntity;
	
	@BeforeEach
	void setUp() {
		testBeerEntity = beerRepository.findAll().get(0);
	}
	
	@Transactional
	@Test
	void testAddCategory() {
		Category savedCategory = categoryRepository.save(Category.builder().description("Test").build());
		
		testBeerEntity.addCategory(savedCategory);
		Beer savedBeer = beerRepository.save(testBeerEntity);
		
		assertEquals(1, savedCategory.getBeers().size());
		assertEquals(1, savedBeer.getCategories().size());
	}
}
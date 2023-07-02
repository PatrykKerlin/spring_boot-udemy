package udemy.springframework.rest_mvc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import udemy.springframework.rest_mvc.entities.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}

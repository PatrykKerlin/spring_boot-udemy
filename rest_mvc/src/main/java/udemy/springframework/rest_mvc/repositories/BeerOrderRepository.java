package udemy.springframework.rest_mvc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import udemy.springframework.rest_mvc.entities.BeerOrder;

import java.util.UUID;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {
}

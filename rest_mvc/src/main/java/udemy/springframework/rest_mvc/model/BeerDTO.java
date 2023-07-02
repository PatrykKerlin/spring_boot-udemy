package udemy.springframework.rest_mvc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class BeerDTO {
	
	private UUID id;
	private Integer version;
	
	@NotNull
	@NotBlank
	@Size(max = 50)
	private String beerName;
	
	@NotNull
	private BeerStyle beerStyle;
	
	@NotNull
	@NotBlank
	private String upc;
	
	@NotNull
	private BigDecimal price;
	
	private Integer quantityOnHand;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
}

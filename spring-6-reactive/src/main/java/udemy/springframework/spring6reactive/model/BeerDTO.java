package udemy.springframework.spring6reactive.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDTO {
	
	private Integer id;
	private Integer version;
	
	@NotBlank
	@Size(min = 3, max = 255)
	private String name;
	
	@NotBlank
	@Size(min = 1, max = 255)
	private String style;
	
	@Size(min = 1, max = 25)
	private String upc;
	
	private Integer quantity;
	private Double price;
	private LocalDateTime created;
	private LocalDateTime modified;
}

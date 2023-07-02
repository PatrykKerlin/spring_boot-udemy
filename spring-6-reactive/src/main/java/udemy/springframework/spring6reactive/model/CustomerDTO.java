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
public class CustomerDTO {
	
	private Integer id;
	private Integer version;
	
	@NotBlank
	@Size(min = 3, max = 255)
	private String name;
	
	private LocalDateTime created;
	private LocalDateTime modified;
}

package udemy.springframework.rest_mvc.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {
	
	@Id
	@GeneratedValue(generator = "UUID")
	@JdbcTypeCode(SqlTypes.CHAR)
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(length = 36, columnDefinition = "char(36)", updatable = false, nullable = false)
	private UUID id;
	
	@Version
	private Integer version;
	
	private String name;
	
	@Size(max = 255)
	@Column(length = 255)
	@Email
	private String email;
	
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	
	@Builder.Default
	@OneToMany(mappedBy = "customer")
	private Set<BeerOrder> beerOrders = new HashSet<>();
}

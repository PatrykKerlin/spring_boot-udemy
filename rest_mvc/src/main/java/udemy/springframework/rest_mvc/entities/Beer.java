package udemy.springframework.rest_mvc.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import udemy.springframework.rest_mvc.model.BeerStyle;

import java.math.BigDecimal;
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
public class Beer {
	
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(length = 36, columnDefinition = "char(36)", updatable = false, nullable = false)
	private UUID id;
	
	@Version
	private Integer version;
	
	@NotNull
	@NotBlank
	@Size(max = 50)
	@Column(length = 50)
	private String beerName;
	
	@NotNull
	private BeerStyle beerStyle;
	
	@NotNull
	@NotBlank
	@Size(max = 255)
	private String upc;
	
	@NotNull
	private BigDecimal price;
	
	private Integer quantityOnHand;
	
	@CreationTimestamp
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	private LocalDateTime updatedDate;
	
	@OneToMany(mappedBy = "beer")
	private Set<BeerOrderLine> beerOrderLines;
	
	@Builder.Default
	@ManyToMany
	@JoinTable(name = "beer_category", joinColumns = @JoinColumn(name = "beer_id"),
			inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Set<Category> categories = new HashSet<>();
	
	public void addCategory(Category category) {
		categories.add(category);
		category.getBeers().add(this);
	}
	
	public void removeCategory(Category category) {
		categories.remove(category);
		category.getBeers().remove(this);
	}
}

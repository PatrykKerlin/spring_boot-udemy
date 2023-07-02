package udemy.springframework.rest_mvc.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class CustomerDTO {
	
	private UUID id;
	private String name;
	
	@Email
	@Size(max = 255)
	private String email;
	
	private Integer version;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	
	// private Customer(Builder builder) {
	// this.id = builder.id;
	// this.name = builder.name;
	// this.version = builder.version;
	// this.createdDate = builder.createdDate;
	// this.updatedDate = builder.createdDate;
	// }
	
	// public UUID getId() {
	// return id;
	// }
	
	// public String getName() {
	// return name;
	// }
	
	// public Double getVersion() {
	// return version;
	// }
	
	// public LocalDateTime getCreatedDate() {
	// return createdDate;
	// }
	
	// public LocalDateTime getUpdatedDate() {
	// return updatedDate;
	// }
	
	// public static class Builder {
	// private UUID id;
	// private String name;
	// private Double version;
	// private LocalDateTime createdDate;
	// private LocalDateTime updatedDate;
	
	// public Builder id(UUID id) {
	// this.id = id;
	// return this;
	// }
	
	// public Builder name(String name) {
	// this.name = name;
	// return this;
	// }
	
	// public Builder version(Double version) {
	// this.version = version;
	// return this;
	// }
	
	// public Builder createdDate(LocalDateTime createdDate) {
	// this.createdDate = createdDate;
	// return this;
	// }
	
	// public Builder updatedDate(LocalDateTime updatedDate) {
	// this.updatedDate = updatedDate;
	// return this;
	// }
	
	// public Customer build() {
	// return new Customer(this);
	// }
	// }
}

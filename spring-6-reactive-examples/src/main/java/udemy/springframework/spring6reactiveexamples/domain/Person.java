package udemy.springframework.spring6reactiveexamples.domain;

import lombok.Builder;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
@Builder
public record Person(Integer id, String firstName, String lastName) {
}

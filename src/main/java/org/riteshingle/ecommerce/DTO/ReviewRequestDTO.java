package org.riteshingle.ecommerce.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Setter
@Getter
public class ReviewRequestDTO {

    @Min(0)
    @Max((5))
    private Integer rating;

    @Column(length = 1000)
    private String comment;
    private Long userId;
    private Long productId;
}

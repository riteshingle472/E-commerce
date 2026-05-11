package org.riteshingle.ecommerce.DTO;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReviewResponseDTO {

    private Long id;
    private Long userId;
    private Long productId;
    private String comment;
    private Integer rating;
    private LocalDateTime createdAt;

}

package org.riteshingle.ecommerce.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String productName;
    private String category;
    private String productDescription;
    private Double averageRating;
    private Double off;
    private Integer stock;
    private Integer totalReview;
    private Integer totalItemsSold;
    private Boolean approved;
    private BigDecimal price;
    private List<String> imageUrl;
    private List<ReviewResponseDTO> reviewList;

}

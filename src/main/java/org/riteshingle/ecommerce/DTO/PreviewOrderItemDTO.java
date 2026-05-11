package org.riteshingle.ecommerce.DTO;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreviewOrderItemDTO {
    private Long productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal price;
    private Double off;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal subTotal;

}

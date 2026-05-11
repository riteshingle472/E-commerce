package org.riteshingle.ecommerce.DTO;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;
    private Long productId;
    private Long orderId;
    private String productName;
    private String imageUrl;
    private String status;
    private Integer quantity;
    private Double off;
    private BigDecimal discount;
    private BigDecimal finalPrice;
    private BigDecimal price;
    private BigDecimal subTotal;
}

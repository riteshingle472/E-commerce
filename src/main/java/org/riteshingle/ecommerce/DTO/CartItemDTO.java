package org.riteshingle.ecommerce.DTO;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    private Long cartItemId;
    private Long productId;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal priceAtAdd;
    private BigDecimal totalPrice;
}

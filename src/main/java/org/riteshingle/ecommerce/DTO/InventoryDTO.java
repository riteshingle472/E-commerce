package org.riteshingle.ecommerce.DTO;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDTO {
    private Long productId;
    private String productName;
    private String category;
    private String stockStatus;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private Double averageRating;
    private Integer currentStock;
    private Integer totalItemBought;
    private Long currentMonthSold;
    private Long lastMonthSold;
}
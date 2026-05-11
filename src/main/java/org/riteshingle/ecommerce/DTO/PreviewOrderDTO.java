package org.riteshingle.ecommerce.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PreviewOrderDTO {
    List<PreviewOrderItemDTO> itemList;
    private Integer totalItems;
    private BigDecimal finalPrice;
    private BigDecimal totalAmount;
    private BigDecimal deliveryCharge;
    private BigDecimal discount;
    private BigDecimal discountAmount;
}

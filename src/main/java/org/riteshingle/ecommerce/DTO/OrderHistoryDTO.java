package org.riteshingle.ecommerce.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryDTO {
    private Long orderId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private String status;
    private Double discount;
    private LocalDateTime orderDate;
}

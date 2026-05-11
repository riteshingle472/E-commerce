package org.riteshingle.ecommerce.DTO;

import lombok.*;
import org.riteshingle.ecommerce.Entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private LocalDateTime createdAt;
    private String  status;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private Double discount;
    private BigDecimal deliveryCharges;
    private BigDecimal discountAmount;
    private List<OrderItemDTO> orderItemList;

}

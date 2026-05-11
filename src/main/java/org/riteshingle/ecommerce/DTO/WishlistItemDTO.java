package org.riteshingle.ecommerce.DTO;

import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Data
public class WishlistItemDTO {
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Integer productStock;
    private BigDecimal productPrice;
    private LocalDateTime addedAt;
}

package org.riteshingle.ecommerce.DTO;

import lombok.*;
import org.riteshingle.ecommerce.Entity.UserEntity;

import java.time.LocalDateTime;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerDTO {
    private Long id;
    private String businessName;
    private String businessDescription;
    private Boolean approved;
}


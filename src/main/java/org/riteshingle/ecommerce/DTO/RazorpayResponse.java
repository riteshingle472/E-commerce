package org.riteshingle.ecommerce.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RazorpayResponse {
    private String orderId;
    private Integer amount;
    private String currency;
}

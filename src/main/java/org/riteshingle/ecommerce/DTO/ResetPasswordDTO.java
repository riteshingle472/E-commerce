package org.riteshingle.ecommerce.DTO;

import jakarta.persistence.Column;
import lombok.*;

@Builder
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDTO {
    private String email;

    @Column(length = 6)
    private String otp;
    private String newPassword;
}

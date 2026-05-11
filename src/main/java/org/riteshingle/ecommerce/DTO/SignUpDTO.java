package org.riteshingle.ecommerce.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDTO {

    private Long id;
    private String name;
    private String email;
    private String newPassword;
    private String address;
    private Long contactNo;
    private Integer pinCode;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


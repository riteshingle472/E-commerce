package org.riteshingle.ecommerce.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Table(name = "tbl_order")
@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private BigDecimal discount;
    private BigDecimal discountAmount;
    private BigDecimal deliveryCharges;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private String paymentStatus; // PENDING, SUCCESS, FAILED

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "orderEntity",cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> orderItemList;



}

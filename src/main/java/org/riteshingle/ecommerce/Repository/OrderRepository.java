package org.riteshingle.ecommerce.Repository;

import org.riteshingle.ecommerce.Entity.OrderEntity;
import org.riteshingle.ecommerce.Entity.OrderStatus;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByUser(UserEntity user, Pageable pageable);

    Optional<OrderEntity> findByUser(UserEntity user);

    Page<OrderEntity> findByUserAndStatus(UserEntity user, OrderStatus status, Pageable pageable);

    Optional<OrderEntity> findByRazorpayOrderId(String razorpayOrderId);

    Boolean existsByUserIdAndOrderItemListProductId(Long userId, Long productId);
}

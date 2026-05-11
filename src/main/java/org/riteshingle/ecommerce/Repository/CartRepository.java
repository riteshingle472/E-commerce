package org.riteshingle.ecommerce.Repository;

import org.riteshingle.ecommerce.Entity.Cart;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser(UserEntity user);
    Boolean existsByUser(UserEntity user);
}

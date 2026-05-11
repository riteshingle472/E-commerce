package org.riteshingle.ecommerce.Repository;

import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    boolean existsByUser(UserEntity user);

    Optional<Wishlist> findByUser(UserEntity user);
}

package org.riteshingle.ecommerce.Repository;

import org.riteshingle.ecommerce.Entity.Seller;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller,Long> {
    Boolean existsByUser(UserEntity user);
    Optional<Seller> findByUser(UserEntity user);

    List<Seller> findByIsApprovedFalse();
}

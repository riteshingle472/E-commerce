package org.riteshingle.ecommerce.Repository;

import org.riteshingle.ecommerce.Entity.Product;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem,Long> {
//    boolean existsByUserAndProduct(UserEntity user, Product product);

    boolean existsByWishlistIdAndProductId(Long id, Long id1);

//    WishlistItem findByIdAndWishlistUserId(Long wishlistItemId, Long id);

}

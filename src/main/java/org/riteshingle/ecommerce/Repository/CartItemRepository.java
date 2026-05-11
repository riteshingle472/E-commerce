package org.riteshingle.ecommerce.Repository;

import org.riteshingle.ecommerce.Entity.Cart;
import org.riteshingle.ecommerce.Entity.CartItem;
import org.riteshingle.ecommerce.Entity.Product;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    List<CartItem>  findByCart(Cart cart);

}

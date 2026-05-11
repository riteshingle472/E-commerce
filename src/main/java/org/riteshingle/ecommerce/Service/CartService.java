package org.riteshingle.ecommerce.Service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.Entity.Cart;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final AuthService authService;

    public Cart createCart(UserEntity user){
        if(!cartRepository.existsByUser(user)) return cartRepository.save(Cart.builder().user(user).build());
        else throw new RuntimeException("Cart Already Exist");
    }
}

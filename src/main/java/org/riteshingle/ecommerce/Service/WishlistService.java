package org.riteshingle.ecommerce.Service;

import lombok.AllArgsConstructor;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Entity.Wishlist;
import org.riteshingle.ecommerce.Repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class WishlistService {

    private WishlistRepository wishlistRepository;

    public Wishlist createWishList(UserEntity user){
        if(!wishlistRepository.existsByUser(user)) return wishlistRepository.save(Wishlist.builder().user(user).build());
        else throw new RuntimeException("Wishlist is already exist for : "+user.getEmail());
    }
}

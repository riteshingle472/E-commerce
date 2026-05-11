package org.riteshingle.ecommerce.Controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.WishlistItemDTO;
import org.riteshingle.ecommerce.DTO.WishlistItemDTO;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Entity.WishlistItem;
import org.riteshingle.ecommerce.Service.AuthService;
import org.riteshingle.ecommerce.Service.WishlistItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final AuthService authService;
    private final WishlistItemService wishlistItemService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add-to-wishlist")
    public ResponseEntity<WishlistItemDTO> addToWishList(@RequestParam Long productId){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(wishlistItemService.addToWishList(productId,currentProfile));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/remove-product-from-wishlist/{wishlistItemId}")
    public ResponseEntity<String> removeProductFromWishlist(@PathVariable Long wishlistItemId){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(wishlistItemService.removeProductFromWishlist(wishlistItemId,currentProfile));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/wishlist")
    public ResponseEntity<List<WishlistItemDTO>> wishlist(){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(wishlistItemService.wishlist(currentProfile));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/clear-wishlist")
    public ResponseEntity<String> clearWishlist(){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(wishlistItemService.clearWishlist(currentProfile));
    }


}

package org.riteshingle.ecommerce.Controller;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.CartItemDTO;
import org.riteshingle.ecommerce.Entity.CartItem;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Service.AuthService;
import org.riteshingle.ecommerce.Service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;
    private final AuthService authService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add-to-cart")
    public ResponseEntity<CartItemDTO> addToCart(
            @RequestParam Long productId ,
            @RequestParam(defaultValue = "1",required = false) Integer quantity){
        return ResponseEntity.ok(cartItemService.addToCart(productId,quantity));
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/cart")
    public ResponseEntity<List<CartItemDTO>> getCartItems(){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(cartItemService.getCartItems(currentProfile));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/remove-from-cart")
    public ResponseEntity<CartItemDTO> removeCartItem(@RequestParam Long cartItemId){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(cartItemService.removeCartItem(cartItemId,currentProfile));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/clear-cart")
    public ResponseEntity<String> clearCart(){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(cartItemService.clearCart(currentProfile));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/increase-quantity")
    public ResponseEntity<CartItemDTO> increaseQuantity(@RequestParam Long cartItemId){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(cartItemService.increaseQuantity(cartItemId , currentProfile));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/decrease-quantity")
    public ResponseEntity<CartItemDTO> decreaseQuantity(@RequestParam Long cartItemId){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(cartItemService.decreaseQuantity(cartItemId , currentProfile));
    }
}

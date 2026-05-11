package org.riteshingle.ecommerce.Service;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.CartItemDTO;
import org.riteshingle.ecommerce.Entity.Cart;
import org.riteshingle.ecommerce.Entity.CartItem;
import org.riteshingle.ecommerce.Entity.Product;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Repository.CartItemRepository;
import org.riteshingle.ecommerce.Repository.CartRepository;
import org.riteshingle.ecommerce.Repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final AuthService authService;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;

    public CartItemDTO addToCart(Long productId, Integer quantity) {
        UserEntity currentProfile = authService.getCurrentProfile();
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if(!product.getApproved()){
            throw new RuntimeException("Product is not approved you can't add it ..");
        }

        Cart cart = cartRepository.findByUser(currentProfile).orElseGet(() -> cartService.createCart(currentProfile));
        CartItem item = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        }

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPriceAtAdd(product.getPrice());
            item.setProductImage(product.getImageUrl().getFirst());
        }
        item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        cartItemRepository.save(item);
        return this.toDTO(item);
    }

    public List<CartItemDTO> getCartItems(UserEntity user) {
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found for : "+user.getEmail()));
        return cartItemRepository.findByCart(cart).stream().map(this::toDTO).toList();
    }

    public CartItemDTO removeCartItem(Long carItemId, UserEntity user) {
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found for : "+user.getEmail()));
        CartItem cartItem = cartItemRepository.findById(carItemId).orElseThrow(() -> new RuntimeException("CartItem not found"));
        if (cartItem.getCart().getId().equals(cart.getId())) {
            cartItemRepository.delete(cartItem);
            return toDTO(cartItem);
        }
        throw new RuntimeException("Only true cart owner can remove cart item only ...");

    }

    public String clearCart(UserEntity user) {
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found for : "+user.getEmail()));
        cart.getCartItemList().clear();
        cartRepository.save(cart);
        return "Cart clear";
    }

    public CartItemDTO increaseQuantity(Long cartItemId, UserEntity user) {
//        if(quantity <= 0)   throw new RuntimeException("Quantity must be greater than 0");
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new RuntimeException("Item not found .."));
        if(!cartItem.getCart().getUser().getId().equals(user.getId())){
            throw new RuntimeException("You are not allowed to modify this cart");
        }
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        cartItem.setTotalPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cartItemRepository.save(cartItem);

        return this.toDTO(cartItem);
    }

    public CartItemDTO decreaseQuantity(Long cartItemId, UserEntity user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new RuntimeException("Item not found .."));

        if(!cartItem.getCart().getUser().getId().equals(user.getId())){
            throw new RuntimeException("You are not allowed to modify this cart");
        }
        cartItem.setQuantity(cartItem.getQuantity() - 1);

        if(cartItem.getQuantity() <= 0){
            cartItemRepository.delete(cartItem);
            return this.toDTO(cartItem);
        }
        cartItem.setTotalPrice (cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cartItemRepository.save(cartItem);
        return this.toDTO(cartItem);
    }

    //    Helping methods
    private CartItemDTO toDTO(CartItem cartItem) {
        return CartItemDTO.builder()
                .cartItemId(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .priceAtAdd(cartItem.getPriceAtAdd())
                .imageUrl(cartItem.getProductImage())
                .totalPrice(cartItem.getTotalPrice())
                .quantity(cartItem.getQuantity())
                .build();
    }
}

package org.riteshingle.ecommerce.Service;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.WishlistItemDTO;
import org.riteshingle.ecommerce.Entity.Product;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Entity.Wishlist;
import org.riteshingle.ecommerce.Entity.WishlistItem;
import org.riteshingle.ecommerce.Repository.ProductRepository;
import org.riteshingle.ecommerce.Repository.WishlistItemRepository;
import org.riteshingle.ecommerce.Repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistItemService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final AuthService authService;
    private final WishlistService wishlistService;

    public WishlistItemDTO addToWishList(Long productId, UserEntity user){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found .."));
        Wishlist wishlist = wishlistRepository.findByUser(user).orElseGet(() -> wishlistService.createWishList(user));

        if(wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(),product.getId())){
            throw new RuntimeException("Product is already in wishlist ...");
        }

        WishlistItem wishlistItem = WishlistItem.builder()
                .product(product)
                .wishlist(wishlist)
                .build();

        wishlistItemRepository.save(wishlistItem);
        return toDTO(wishlistItem);
    }

    public String removeProductFromWishlist(Long wishlistItemId , UserEntity user){
//        WishlistItem wishlistItem = wishlistItemRepository.findByIdAndWishlistUserId(wishlistItemId, user.getId());
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId).orElseThrow(() -> new RuntimeException("Wishlist product not found"));

        if(wishlistItem.getWishlist().getUser().getId().equals(user.getId())){
            wishlistItemRepository.delete(wishlistItem);
            return "Product removed from wishlist";
        }
        throw new RuntimeException("Only true wishlist owner can remove wishlist item only ...");
    }

    public List<WishlistItemDTO> wishlist(UserEntity user){
        Wishlist wishlist = wishlistRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Wishlist not found ..."));
        return wishlist.getWishlistItems().stream().map(this::toDTO).toList();
    }

    public String clearWishlist(UserEntity user){
        Wishlist wishlist = wishlistRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Wishlist not found .. "));
        wishlist.getWishlistItems().clear();
        wishlistRepository.save(wishlist);
        return "Wishlist clear";
    }

    private WishlistItemDTO toDTO(WishlistItem wishlistItem){
        List<String> images = wishlistItem.getProduct().getImageUrl();
        Product product = wishlistItem.getProduct();
        return WishlistItemDTO.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .productStock(product.getStock())
                .productImageUrl((images != null && !images.isEmpty()) ? images.getFirst() : null)
                .productPrice(product.getPrice())
                .addedAt(wishlistItem.getCreatedAt())
                .build();
    }


}

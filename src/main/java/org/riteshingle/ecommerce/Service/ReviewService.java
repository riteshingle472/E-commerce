package org.riteshingle.ecommerce.Service;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.ReviewRequestDTO;
import org.riteshingle.ecommerce.DTO.ReviewResponseDTO;
import org.riteshingle.ecommerce.Entity.Product;
import org.riteshingle.ecommerce.Entity.Review;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public ReviewResponseDTO addReview(ReviewRequestDTO requestDTO ,Long productId , Long userId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product Not found with : " + productId));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not found with : " + userId));
        Boolean hasPurchased = orderRepository.existsByUserIdAndOrderItemListProductId(userId, productId);

        if(!hasPurchased) {
            throw new RuntimeException("You can only review purchased products");
        }

        Boolean hasReview = reviewRepository.existsByUserIdAndProductId(userId,productId);
        if(hasReview){
            throw new RuntimeException("You already reviewed this product");
        }
        Review review = toEntity(requestDTO);
        review.setUser(user);
        review.setProduct(product);
        if(product.getReviews() == null){
            product.setReviews(new ArrayList<>());
        }
        product.getReviews().add(review);
        reviewRepository.save(review);

        Double averageRating = reviewRepository.getAverageRatingByProductId(productId);
        Integer totalReview = reviewRepository.countReviews(product.getId());
        System.out.println(totalReview);

        product.setAverageRating((averageRating != null) ? averageRating : 0.0);
        product.setTotalReview(totalReview);
        productRepository.save(product);
        return toResponse(review);
    }

    public String deleteReview(Long productId, Long userId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product Not found with : " + productId));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not found with : " + userId));
        Boolean hasReview = reviewRepository.existsByUserIdAndProductId(user.getId(),product.getId());

        if(!hasReview){
            throw new RuntimeException("Unauthorized access");
        }
        Review review = reviewRepository.findByUserIdAndProductId(userId, productId).orElseThrow(() -> new RuntimeException("Review not found of user  : " + userId));

        reviewRepository.delete(review);
        Double averageRating = reviewRepository.getAverageRatingByProductId(productId);
        Integer totalReview = reviewRepository.countReviews(product.getId());

        System.out.println(totalReview);
        product.setAverageRating((averageRating != null)? averageRating : 0.0);
        product.setTotalReview(totalReview);
        productRepository.save(product);

        return "Review Delete Successfully";
    }

    public List<ReviewResponseDTO> getAllReview(Long productId , Pageable page) {
        List<Review> reviewList = reviewRepository.findByProductId(productId,page);
        List<ReviewResponseDTO> reviewResponseList = new ArrayList<>();
        for (Review review : reviewList){
            ReviewResponseDTO responseDTO = toResponse(review);
            reviewResponseList.add(responseDTO);
        }

        return reviewResponseList;
    }

//    helping methods
    private ReviewResponseDTO toResponse(Review review){
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .productId(review.getProduct().getId())
                .comment(review.getComment())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private Review toEntity(ReviewRequestDTO requestDTO) {
        return Review.builder()
                .rating(requestDTO.getRating())
                .comment(requestDTO.getComment())
                .build();
    }


}

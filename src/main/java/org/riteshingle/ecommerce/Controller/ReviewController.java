package org.riteshingle.ecommerce.Controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.riteshingle.ecommerce.DTO.ReviewRequestDTO;
import org.riteshingle.ecommerce.DTO.ReviewResponseDTO;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Service.AuthService;
import org.riteshingle.ecommerce.Service.ReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService authService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/review")
    public ResponseEntity<ReviewResponseDTO> addReview(@RequestBody ReviewRequestDTO requestDTO , @RequestParam Long productId ){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(reviewService.addReview(requestDTO,productId,currentProfile.getId()));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete-review")
    public ResponseEntity<String > deleteReview(@RequestParam Long productId ){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(reviewService.deleteReview(productId,currentProfile.getId()));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReview(
            @RequestParam(required = false , defaultValue = "1") int pageNo,
            @RequestParam(required = false , defaultValue = "5") int pageSize,
            Long productId
    ){
        return ResponseEntity.ok(reviewService.getAllReview(productId ,PageRequest.of(pageNo-1,pageSize)));
    }
}

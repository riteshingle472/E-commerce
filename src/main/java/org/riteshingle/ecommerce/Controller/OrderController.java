package org.riteshingle.ecommerce.Controller;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.OrderDTO;
import org.riteshingle.ecommerce.DTO.OrderHistoryDTO;
import org.riteshingle.ecommerce.DTO.PreviewOrderDTO;
import org.riteshingle.ecommerce.Entity.OrderStatus;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Repository.UserRepository;
import org.riteshingle.ecommerce.Service.AuthService;
import org.riteshingle.ecommerce.Service.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;
    private final UserRepository userRepository;


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/buy-now")
    public ResponseEntity<OrderDTO> createOrder(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(orderService.createOrder(currentProfile, productId, quantity));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/order-cart")
    public ResponseEntity<OrderDTO> createOrderForCart() {
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(orderService.createOrderForCart(currentProfile));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/preview-cart-order")
    public ResponseEntity<PreviewOrderDTO> previewCartOrder() {
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(orderService.previewCartOrder(currentProfile));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/preview-order")
    public ResponseEntity<PreviewOrderDTO> previewOrder(@RequestParam Long productId, @RequestParam Integer quantity) {
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(orderService.previewOrder(productId, quantity));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/order-history/{id}")
    public ResponseEntity<List<OrderHistoryDTO>> getOrderHistory(
            @PathVariable Long id,
            @RequestParam(required = false,defaultValue = "1") Integer pageNo,
            @RequestParam(required = false,defaultValue = "10") Integer pageSize,
            @RequestParam(required = false,defaultValue = "totalAmount") String sortBy,
            @RequestParam(required = false,defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false,defaultValue = "PENDING") OrderStatus status
    ){
        UserEntity currentProfile = authService.getCurrentProfile();
        Sort sort = (sortDirection.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(orderService.getOrderHistory(currentProfile,PageRequest.of(pageNo-1,pageSize,sort),status));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/order-details/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId){
        UserEntity currentProfile = authService.getCurrentProfile();
        return ResponseEntity.ok(orderService.getOrder(orderId,currentProfile));
    }
}

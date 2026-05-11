package org.riteshingle.ecommerce.Controller;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.Entity.OrderEntity;
import org.riteshingle.ecommerce.Entity.OrderStatus;
import org.riteshingle.ecommerce.Entity.PaymentStatus;
import org.riteshingle.ecommerce.Repository.OrderRepository;
import org.riteshingle.ecommerce.Service.RazorpayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class RazorpayController {

    private final RazorpayService razorpayService;
    private final OrderRepository repository;

    @Value("${razorpay.key.secret}")
    private String secret;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<Map<String ,Object>> createOrder(
            @RequestParam Long orderId
    ){

        OrderEntity orderEntity = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        try {
            Order razorpayOrder = razorpayService.createOrder(orderEntity.getTotalAmount().intValue(), orderEntity.getId());
            orderEntity.setRazorpayOrderId(razorpayOrder.get("id"));
            orderEntity.setPaymentStatus("PENDING");
            repository.save(orderEntity);

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", razorpayOrder.get("id"));
            response.put("amount", razorpayOrder.get("amount"));
            response.put("currency", razorpayOrder.get("currency"));

            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
    }

}

package org.riteshingle.ecommerce.Service;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.*;
import org.riteshingle.ecommerce.Entity.*;
import org.riteshingle.ecommerce.Repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemService cartItemService;

    public OrderDTO createOrder(UserEntity user, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Product out of stock ..");
        }

        if(!product.getApproved()){
            throw new RuntimeException("Product is not Approved ..");
        }

        OrderEntity orderEntity = OrderEntity.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setProductName(product.getProductName());
        orderItem.setQuantity(quantity);
        orderItem.setOff((product.getOff() == null) ? 0 : product.getOff());
        orderItem.setOrderEntity(orderEntity);
        orderItem.setImageUrl(product.getImageUrl().get(0));
        orderItem.setOrderItemStatus(OrderItemStatus.PENDING);

        // 🔹 Item Price Calculation
        BigDecimal price = product.getPrice();
        BigDecimal subTotal = price.multiply(BigDecimal.valueOf(quantity));

        BigDecimal itemDiscountAmount = subTotal.multiply(
                BigDecimal.valueOf(orderItem.getOff())
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.HALF_UP);

        BigDecimal itemFinalPrice = subTotal.subtract(itemDiscountAmount)
                .setScale(2, RoundingMode.HALF_UP);

        orderItem.setPrice(price);
        orderItem.setSubTotal(subTotal);
        orderItem.setDiscountAmount(itemDiscountAmount);
        orderItem.setFinalPrice(itemFinalPrice);

        List<OrderItem> orderItemsList = new ArrayList<>();
        orderItemsList.add(orderItem);

        orderEntity.setTotalItems(orderItemsList.size());
        orderEntity.setOrderItemList(orderItemsList);

        // 🔹 Order Level Calculation
        BigDecimal orderAmount = itemFinalPrice;

        double discountPercent = orderAmount.compareTo(BigDecimal.valueOf(30000)) >= 0 ? 15.0 :
                orderAmount.compareTo(BigDecimal.valueOf(15000)) >= 0 ? 10.0 : 5.0;

        BigDecimal discountPercentBD = BigDecimal.valueOf(discountPercent);

        BigDecimal orderDiscountAmount = orderAmount.multiply(
                discountPercentBD.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.HALF_UP);

        // 🔹 Delivery Charges
        BigDecimal deliveryCharges = orderAmount.compareTo(BigDecimal.valueOf(5000)) >= 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(500);

        // 🔹 Final Payable
        BigDecimal finalPayable = orderAmount
                .subtract(orderDiscountAmount)
                .add(deliveryCharges)
                .setScale(2, RoundingMode.HALF_UP);

        // 🔹 Set Order Fields
        orderEntity.setDiscount(discountPercentBD);
        orderEntity.setDiscountAmount(orderDiscountAmount);
        orderEntity.setTotalAmount(finalPayable);
        orderEntity.setDeliveryCharges(deliveryCharges);

        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);

        // 🔹 Update Stock
        product.setStock(product.getStock() - quantity);
        product.setSalesVolume(product.getSalesVolume() != null ? product.getSalesVolume()+quantity : quantity);
        productRepository.save(product);

        return toDTO(savedOrderEntity);
    }

    public OrderDTO createOrderForCart(UserEntity user) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found with : " + user.getEmail()));

        if (cart.getCartItemList().isEmpty()) {
            throw new RuntimeException("Empty cart ! No product found");
        }

        List<CartItem> itemList = cart.getCartItemList();
        List<OrderItem> orderItemsList = new ArrayList<>();

        OrderEntity orderEntity = OrderEntity.builder()
                .createdAt(LocalDateTime.now())
                .user(user)
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Convert CartItem → OrderItem
        for (CartItem cartItem : itemList) {
            if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Only "
                        + cartItem.getProduct().getStock()
                        + " items available for "
                        + cartItem.getProduct().getProductName());
            }

            OrderItem orderItem = toOrderItem(cartItem);
            orderItem.setOrderEntity(orderEntity);
            orderItem.setImageUrl(cartItem.getProductImage().toString());
            orderItem.setOrderItemStatus(OrderItemStatus.PENDING);
            orderItemsList.add(orderItem);

            totalAmount = totalAmount.add(orderItem.getFinalPrice());
        }

        // Order Discount %
        double discountPercent = totalAmount.compareTo(BigDecimal.valueOf(30000)) >= 0 ? 15.0 :
                totalAmount.compareTo(BigDecimal.valueOf(15000)) >= 0 ? 10.0 : 5.0;

        BigDecimal discountPercentBD = BigDecimal.valueOf(discountPercent);

        // Discount Amount
        BigDecimal discountAmount = totalAmount.multiply(
                discountPercentBD.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.HALF_UP);

        // Delivery Charges
        BigDecimal deliveryCharges = totalAmount.compareTo(BigDecimal.valueOf(5000)) >= 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(500);

        // Final amount
        BigDecimal finalAmount = totalAmount
                .subtract(discountAmount)
                .add(deliveryCharges)
                .setScale(2, RoundingMode.HALF_UP);

        // Set Order Fields
        orderEntity.setDiscount(discountPercentBD);
        orderEntity.setDiscountAmount(discountAmount);
        orderEntity.setDeliveryCharges(deliveryCharges);
        orderEntity.setTotalAmount(finalAmount);
        orderEntity.setTotalItems(orderItemsList.size());
        orderEntity.setOrderItemList(orderItemsList);

        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);

        // 🔹 Update Stock
        for (OrderItem item : orderItemsList) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product Not found with " + item.getProduct().getId()));

            product.setStock(product.getStock() - item.getQuantity());
            product.setSalesVolume(product.getSalesVolume() != null ? product.getSalesVolume()+item.getQuantity() : item.getQuantity());
            productRepository.save(product);
        }

        // 🔹 Clear Cart (recommended)
         cartItemService.clearCart(user);

        return toDTO(savedOrderEntity);
    }

    public PreviewOrderDTO previewCartOrder(UserEntity user) {
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found with : " + user.getEmail()));
        List<PreviewOrderItemDTO> orderItemList = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        BigDecimal deliveryCharge = BigDecimal.ZERO;
        Double discount = 0.0;

        for (CartItem cartItem : cart.getCartItemList()) {

            if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Only "
                        + cartItem.getProduct().getStock()
                        + " items available for "
                        + cartItem.getProduct().getProductName());
            }

            PreviewOrderItemDTO dto = previewOrderItemDTO(cartItem);
            totalAmount = totalAmount.add(dto.getTotalAmount());
            orderItemList.add(dto);
        }

        deliveryCharge = totalAmount.compareTo(BigDecimal.valueOf(5000)) >= 0 ? BigDecimal.valueOf(0) : BigDecimal.valueOf(500);
        discount = (totalAmount.doubleValue() >= 30000) ? 15.0 : (totalAmount.doubleValue() >= 15000) ? 10.0 : 5.0;

//        order.setDiscount((totalAmount.doubleValue() >= 30000) ? 15.0 : (totalAmount.doubleValue() >= 15000) ? 10.0 : 5.0);
        BigDecimal discountAmount = (totalAmount.multiply(BigDecimal.valueOf(discount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
        finalAmount = totalAmount.add(deliveryCharge).subtract(discountAmount);

        PreviewOrderDTO dto = PreviewOrderDTO.builder()
                .deliveryCharge(deliveryCharge)
                .itemList(orderItemList)
                .finalPrice(finalAmount)
                .totalItems(orderItemList.size())
                .totalAmount(totalAmount)
                .discount(BigDecimal.valueOf(discount))
                .discountAmount(discountAmount)
                .build();

        return dto;
    }

    public PreviewOrderDTO previewOrder(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        List<PreviewOrderItemDTO> orderItemList = new ArrayList<>();
        if (product.getStock() < quantity) {
            throw new RuntimeException("Product out of stock");
        }

        BigDecimal price = product.getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal itemDiscountAmount = subtotal.multiply(
                BigDecimal.valueOf(product.getOff())
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.subtract(itemDiscountAmount).setScale(2, RoundingMode.HALF_UP);

        PreviewOrderItemDTO dto = PreviewOrderItemDTO.builder()
                .productId(productId)
                .productName(product.getProductName())
                .imageUrl(product.getImageUrl().get(0))
                .price(product.getPrice())
                .quantity(quantity)
                .off(product.getOff())
                .discountAmount(itemDiscountAmount)
                .subTotal(subtotal)
                .totalAmount(totalAmount)
                .build();

        orderItemList.add(dto);
        Double discount = (dto.getTotalAmount().doubleValue() >= 30000) ? 15.0 : (dto.getTotalAmount().doubleValue() >= 15000) ? 10.0 : 5;
        BigDecimal discountAmount = totalAmount.multiply(
                BigDecimal.valueOf(discount)
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.HALF_UP);
        BigDecimal deliveryCharges = totalAmount.compareTo(BigDecimal.valueOf(5000)) >= 0 ? BigDecimal.valueOf(0) : BigDecimal.valueOf(500);

        PreviewOrderDTO orderDTO = PreviewOrderDTO
                .builder()
                .deliveryCharge(deliveryCharges)
                .totalItems(orderItemList.size())
                .itemList(orderItemList)
                .totalAmount(dto.getTotalAmount())
                .finalPrice(totalAmount.add(deliveryCharges).subtract(discountAmount).setScale(2, RoundingMode.HALF_UP))
                .discount(BigDecimal.valueOf(discount))
                .discountAmount(discountAmount)
                .build();

        return orderDTO;
    }

    // order history Only for user
    public List<OrderHistoryDTO> getOrderHistory(UserEntity user, Pageable page, OrderStatus status) {
        Page<OrderEntity> orders;

        if (status != null) {
            orders = orderRepository.findByUserAndStatus(user, status, page);
        } else {
            orders = orderRepository.findByUser(user, page);
        }

        Double discount = 0.0;
        Double totalAmount = 0.0;

        for (OrderEntity orderEntity : orders) {
            totalAmount = totalAmount + orderEntity.getTotalAmount().doubleValue();
        }
        System.out.println("Total Amount : " + totalAmount);

        if (totalAmount >= 30000) {
            discount = 15.0;
        } else if (discount >= 15000) {
            discount = 7.0;
        } else {
            discount = 5.0;
        }

        Double finalDiscount = discount;
        return orders.map(orderEntity -> {
            OrderHistoryDTO dto = orderHistoryDTO(orderEntity);
            dto.setDiscount(finalDiscount);
            return dto;
        }).getContent();
    }

    //    order detail
    public OrderDTO getOrder(Long orderId, UserEntity user) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order Not found with" + orderId));

        if (orderEntity.getUser().getId().equals(user.getId())) {
            return toDTO(orderEntity);
        } else throw new RuntimeException("Unauthorized access !");
    }


    private OrderDTO toDTO(OrderEntity orderEntity) {
        List< OrderItemDTO> orderItemList = orderEntity.getOrderItemList().stream()
                .map(item -> OrderItemDTO.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .imageUrl(item.getImageUrl())
                        .status(String.valueOf(item.getOrderItemStatus()))
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .off(item.getOff())
                        .discount(item.getDiscountAmount())
                        .finalPrice(item.getFinalPrice())
                        .subTotal(item.getSubTotal())
                        .orderItemId(item.getId())
                        .orderId(item.getOrderEntity().getId())
                        .build()
                ).toList();
        return OrderDTO.builder()
                .orderId(orderEntity.getId())
                .orderItemList(orderItemList)
                .totalAmount(orderEntity.getTotalAmount())
                .userId(orderEntity.getUser().getId())
                .status(orderEntity.getStatus().name())
                .deliveryCharges(orderEntity.getDeliveryCharges())
                .totalItems(orderEntity.getTotalItems())
                .discount((orderEntity.getTotalAmount().doubleValue() >= 30000) ? 15.0 : (orderEntity.getTotalAmount().doubleValue() >= 15000) ? 10.0 : 5)
                .discountAmount(orderEntity.getDiscountAmount())
                .createdAt(orderEntity.getCreatedAt())
                .build();
    }

    private OrderItem toOrderItem(CartItem cartItem) {
        BigDecimal price = cartItem.getProduct().getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        BigDecimal discountAmount = subtotal.multiply(BigDecimal.valueOf(cartItem.getProduct().getOff()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
        BigDecimal finalPrice = subtotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        System.out.println(discountAmount);
        return OrderItem.builder()
                .product(cartItem.getProduct())
                .productName(cartItem.getProduct().getProductName())
                .quantity(cartItem.getQuantity())
                .price(price)
                .off((cartItem.getProduct().getOff() == null) ? 2 : cartItem.getProduct().getOff())
                .subTotal(subtotal)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .build();
    }

    private PreviewOrderItemDTO previewOrderItemDTO(CartItem cartItem) {

        BigDecimal price = cartItem.getProduct().getPrice();
        BigDecimal subTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        BigDecimal discountAmount = subTotal.multiply(BigDecimal.valueOf(cartItem.getProduct().getOff())).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalAmount = subTotal.subtract(discountAmount);
        return PreviewOrderItemDTO.builder()
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getProductName())
                .imageUrl(cartItem.getProductImage())
                .price(price)
                .off(cartItem.getProduct().getOff())
                .quantity(cartItem.getQuantity())
                .subTotal(subTotal)
                .totalAmount(finalAmount)
                .discountAmount(discountAmount)
                .build();
    }

    private OrderHistoryDTO orderHistoryDTO(OrderEntity orderEntity) {
        return OrderHistoryDTO.builder()
                .orderId(orderEntity.getId())
                .totalItems(orderEntity.getTotalItems())
                .orderDate(orderEntity.getCreatedAt())
                .status(orderEntity.getStatus().toString())
                .totalAmount(orderEntity.getTotalAmount())
                .build();

    }
}

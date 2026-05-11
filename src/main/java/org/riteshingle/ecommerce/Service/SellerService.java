package org.riteshingle.ecommerce.Service;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.*;
import org.riteshingle.ecommerce.Entity.*;
import org.riteshingle.ecommerce.Repository.*;
import org.riteshingle.ecommerce.Specification.ProductSpecifications;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;

    public String upgradeToSeller(SellerDTO sellerDTO) {
        UserEntity user = authService.getCurrentProfile();
        Role role = roleRepository.findById(2L).orElseThrow();

        if (!user.getRole().contains(role)) {
            user.getRole().add(role);
        }

        if (sellerRepository.existsByUser(user)) {
            throw new RuntimeException("User already exist");
        }

        sellerRepository.save(Seller.builder()
                .businessName(sellerDTO.getBusinessName())
                .businessDescription(sellerDTO.getBusinessDescription())
                .isApproved(false)
                .user(user)
                .build());
        userRepository.save(user);
        return "User upgrade to seller . Please login again";
    }

    public Seller findSeller(UserEntity user){
        if (sellerRepository.existsByUser(user)){
            return sellerRepository.findByUser(user).orElseThrow(() -> new RuntimeException("User not found"));
        }else {
            throw new RuntimeException("Seller not found");
        }
    }

//    Seller inventory
    public List<InventoryDTO> inventoryReport(String name, String category, Double minPrice, Double maxPrice, Double averageRating, Pageable pageable){
        Seller seller = findSeller(authService.getCurrentProfile());
        List<Product> products = productRepository.findBySeller(seller);
        List<InventoryDTO> inventoryReport = new ArrayList<>();

        Specification<Product> specification = Specification
                .where(ProductSpecifications.hasName(name))
                .and(ProductSpecifications.hasAverageRating(averageRating))
                .and(ProductSpecifications.hasMinPrice(minPrice))
                .and(ProductSpecifications.hasMaxPrice(maxPrice))
                .and(ProductSpecifications.hasCategory(category));

        Map<Long, ProductSalesProjection> map = orderItemRepository.getMonthlySales()
                .stream()
                .collect(Collectors.toMap(ProductSalesProjection::getProductId, p -> p));

        String stockStatus;

        for (Product product : products){
            ProductSalesProjection projection = map.get(product.getId());
            long currentMonth = 0L;
            long lastMonth = 0L;

            if(projection != null){
                currentMonth = projection.getCurrentMonthSold() != null ? projection.getCurrentMonthSold() : 0;
                lastMonth = projection.getLastMonthSold() != null ? projection.getProductId() : 0;
            }

            if(product.getStock() == 0) stockStatus ="OUT_OF_STOCK";
            else if(product.getStock() <= 50) stockStatus ="LOW_STOCK";
            else  stockStatus  = "IN_STOCK";

            InventoryDTO dto = InventoryDTO.builder()
                    .productId(product.getId())
                    .productName(product.getProductName())
                    .unitPrice(product.getPrice())
                    .category(product.getCategory())
                    .currentStock(product.getStock())
                    .averageRating(product.getAverageRating())
                    .totalValue(product.getPrice().multiply(BigDecimal.valueOf(product.getStock())))
                    .stockStatus(stockStatus)
                    .totalItemBought(product.getSalesVolume())
                    .currentMonthSold(currentMonth)
                    .lastMonthSold(lastMonth)
                    .build();

            inventoryReport.add(dto);
        }
        return inventoryReport;
    }

    @Transactional
    public OrderDTO changeOrderStatus(Long orderItemId,OrderItemStatus orderItemStatus){
        UserEntity currentProfile = authService.getCurrentProfile();
        Seller seller = findSeller(currentProfile);
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new RuntimeException("OrderItem not found : " + orderItemId));

        System.out.println("Seller id : "+seller.getId());
        System.out.println("Item Seller id : "+orderItem.getProduct().getSeller().getId());

        if(!orderItem.getProduct().getSeller().getId().equals(seller.getId())){
           throw new RuntimeException("Unauthorized access");
        }
        validateStatusTransition(orderItem.getOrderItemStatus(),orderItemStatus);
        orderItem.setOrderItemStatus(orderItemStatus);
        OrderEntity order = orderItem.getOrderEntity();
        recalculateOrderStatus(order);

        return toDTO(orderRepository.save(order));
    }

    private void recalculateOrderStatus(OrderEntity order) {

        List<OrderItemStatus> statuses = order.getOrderItemList()
                .stream()
                .map(OrderItem::getOrderItemStatus)
                .toList();

        boolean allDelivered = statuses.stream()
                .allMatch(s -> s == OrderItemStatus.DELIVERED);

        boolean anyDelivered = statuses.stream()
                .anyMatch(s -> s == OrderItemStatus.DELIVERED);

        boolean allShipped = statuses.stream()
                .allMatch(s -> s == OrderItemStatus.SHIPPED);

        boolean allConfirmed = statuses.stream()
                .allMatch(s -> s == OrderItemStatus.CONFIRMED);

        boolean anyConfirmed = statuses.stream()
                .anyMatch(s -> s == OrderItemStatus.CONFIRMED);

        boolean anyShipped = statuses.stream()
                .anyMatch(s -> s == OrderItemStatus.SHIPPED);

        if (allDelivered) {
            order.setStatus(OrderStatus.COMPLETED);
        }
        if (anyDelivered) {
            order.setStatus(OrderStatus.PARTIALLY_CONFIRMED);
        }
        else if (allShipped) {
            order.setStatus(OrderStatus.SHIPPED);
        }
        else if (anyShipped) {
            order.setStatus(OrderStatus.PARTIALLY_SHIPPED);
        }
        else if (allConfirmed) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        else if (anyConfirmed) {
            order.setStatus(OrderStatus.PARTIALLY_CONFIRMED);
        }
        else {
            order.setStatus(OrderStatus.PENDING);
        }
    }

    private void validateStatusTransition(OrderItemStatus status , OrderItemStatus next){

        if(status == null){
            throw new RuntimeException("Current status is null in DB");
        }

        if(status == OrderItemStatus.DELIVERED || status == OrderItemStatus.CANCELLED)
            throw new RuntimeException("Status cannot be changed");

        if(status == OrderItemStatus.PENDING && next == OrderItemStatus.CONFIRMED)
            throw new RuntimeException("Invalid Status");

        if (status == OrderItemStatus.CONFIRMED && next != OrderItemStatus.SHIPPED)
            throw new RuntimeException("Invalid transition");

        if (status == OrderItemStatus.SHIPPED && next != OrderItemStatus.DELIVERED)
            throw new RuntimeException("Invalid transition");
        if(status == OrderItemStatus.PENDING && next == OrderItemStatus.DELIVERED)
            throw new RuntimeException("Invalid transaction");
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

}

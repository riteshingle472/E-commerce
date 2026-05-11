package org.riteshingle.ecommerce.Repository;
import org.riteshingle.ecommerce.DTO.ProductSalesProjection;
import org.riteshingle.ecommerce.Entity.OrderItem;
import org.riteshingle.ecommerce.Entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

//    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi " +
//            "JOIN oi.orderEntity o " +
//            "WHERE o.status = 'CONFIRMED' " +
//            "AND MONTH(o.createdAt) = :month " +
//            "AND YEAR(o.createdAt) = :year " +
//            "AND oi.product.id = :productId")
//    Integer getCurrentMonthSold(Long productId, int month, int year);
//
//    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi " +
//            "JOIN oi.orderEntity o " +
//            "WHERE o.status = 'CONFIRMED' " +
//            "AND MONTH(o.createdAt) = :lastMonth " +
//            "AND YEAR(o.createdAt) = :year " +
//            "AND oi.product.id = :productId")
//    Integer getLastMonthSold(Long productId, int lastMonth, int year);

    @Query("SELECT oi.product.id as productId, " +
            "SUM(CASE WHEN MONTH(o.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE) THEN oi.quantity ELSE 0 END) as currentMonthSold, " +
            "SUM(CASE WHEN MONTH(o.createdAt) = MONTH(CURRENT_DATE) - 1 " +
            "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE) THEN oi.quantity ELSE 0 END) as lastMonthSold " +
            "FROM OrderItem oi " +
            "JOIN oi.orderEntity o " +
            "WHERE o.status = 'PENDING' " +
            "GROUP BY oi.product.id")
    List<ProductSalesProjection> getMonthlySales();
}

package org.riteshingle.ecommerce.DTO;

public interface ProductSalesProjection {

    Long getProductId();           // ✅
    Long getCurrentMonthSold();    // ✅
    Long getLastMonthSold();       // ✅
}
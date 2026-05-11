package org.riteshingle.ecommerce.Controller;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.InventoryDTO;
import org.riteshingle.ecommerce.DTO.ProductDTO;
import org.riteshingle.ecommerce.DTO.SellerDTO;
import org.riteshingle.ecommerce.Entity.OrderStatus;
import org.riteshingle.ecommerce.Entity.Seller;
import org.riteshingle.ecommerce.Service.AdminService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/unapproved-seller")
    public ResponseEntity<List<SellerDTO>> pendingSeller(
            @RequestParam(required = false , defaultValue = "1") int pageNo,
            @RequestParam(required = false , defaultValue = "5") int pageSize
    ){
        return ResponseEntity.ok(adminService.unApprovedSellers(PageRequest.of(pageNo-1,pageSize)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/approve-seller/{id}")
    public ResponseEntity<String> approveSeller(@PathVariable Long id){
        return ResponseEntity.ok(adminService.approveSeller(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/approve-product/{id}")
    public ResponseEntity<ProductDTO> approveProduct(@PathVariable Long id){
        return ResponseEntity.ok(adminService.approveProduct(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/unapproved-products")
    public ResponseEntity<List<ProductDTO>> unApproveProducts(
            @RequestParam(required = false , defaultValue = "1") int pageNo,
            @RequestParam(required = false , defaultValue = "5") int pageSize
    ){
        return ResponseEntity.ok(adminService.unApproveProduct(PageRequest.of(pageNo-1,pageSize)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/inventory-report")
    public ResponseEntity<List<InventoryDTO>> inventoryReport(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false , defaultValue = "1") int pageNo,
            @RequestParam(required = false , defaultValue = "5") int pageSize,
            @RequestParam(required = false , defaultValue = "productName") String sortBy,
            @RequestParam(required = false , defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) Double averageRating
    ){

        Sort sort = (sortDirection != null && sortDirection.equalsIgnoreCase("DESC")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(adminService.inventoryReport(name,category,minPrice,maxPrice,averageRating,PageRequest.of(pageNo -1 ,pageSize,sort)));
    }
}

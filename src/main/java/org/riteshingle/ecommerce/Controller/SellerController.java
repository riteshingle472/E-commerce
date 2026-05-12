package org.riteshingle.ecommerce.Controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.InventoryDTO;
import org.riteshingle.ecommerce.DTO.OrderDTO;
import org.riteshingle.ecommerce.DTO.ProductDTO;
import org.riteshingle.ecommerce.DTO.SellerDTO;
import org.riteshingle.ecommerce.Entity.OrderItemStatus;
import org.riteshingle.ecommerce.Service.ProductService;
import org.riteshingle.ecommerce.Service.SellerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/upgrade-to-seller")
    public ResponseEntity<String > upgradeToSeller(@RequestBody SellerDTO dto){
        return ResponseEntity.ok(sellerService.upgradeToSeller(dto));
    }

    @PreAuthorize("hasRole('SELLER')")
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
        return ResponseEntity.ok(sellerService.inventoryReport(name,category,minPrice,maxPrice,averageRating, PageRequest.of(pageNo -1 ,pageSize,sort)));
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/update-status")
    public ResponseEntity<OrderDTO> changeOrderStatus(@RequestParam Long orderItemId , @RequestParam OrderItemStatus orderItemStatus){
        return ResponseEntity.ok(sellerService.changeOrderStatus(orderItemId,orderItemStatus));
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/add-stock")
    public ResponseEntity<String> addStock(@RequestParam Long productId , @RequestParam Integer stock){
        return ResponseEntity.ok(sellerService.addStock(productId,stock));
    }
}

package org.riteshingle.ecommerce.Controller;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.ProductDTO;
import org.riteshingle.ecommerce.Entity.Product;
import org.riteshingle.ecommerce.Service.ProductService;
import org.riteshingle.ecommerce.Service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/add-product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO){
        return ResponseEntity.ok(productService.addProduct(productDTO));
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/remove-product/{id}")
    public ResponseEntity<ProductDTO> removeProduct(@PathVariable Long id){
        return ResponseEntity.ok( productService.removeProduct(id));
    }

//    @PreAuthorize("hasRole('USER')")
//    @GetMapping("/search-product")
//    public ResponseEntity<List<ProductDTO>> searchProductByKeyword(
//            @RequestParam String keyword,
//            @RequestParam(required = false , defaultValue = "1") int pageNo,
//            @RequestParam(required = false , defaultValue = "5") int pageSize,
//            @RequestParam(required = false , defaultValue = "productName") String sortBy,
//            @RequestParam(required = false , defaultValue = "ASC") String sortDirection){
//
//        Sort sort = (sortDirection.equalsIgnoreCase("ASC")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//        return ResponseEntity.ok(productService.searchProduct(PageRequest.of(pageNo-1, pageSize, sort), keyword));
//    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getProducts(
            @RequestParam(required = false , defaultValue = "1") int pageNo,
            @RequestParam(required = false , defaultValue = "5") int pageSize,
            @RequestParam(required = false , defaultValue = "productName") String sortBy,
            @RequestParam(required = false , defaultValue = "ASC") String sortDirection){
        Sort sort = (sortDirection.equalsIgnoreCase("ASC")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(productService.getProducts(PageRequest.of(pageNo-1,pageSize,sort)));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/filter-products")
    public ResponseEntity<List<ProductDTO>> getFilterProducts(
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
        List<ProductDTO> products= productService.filterProducts(name, category, minPrice, maxPrice,averageRating, pageNo, pageSize, sortBy,sortDirection);
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/product-detail/{id}")
    public ResponseEntity<ProductDTO> getProductDetails(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductDetails(id));
    }
}

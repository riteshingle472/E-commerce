package org.riteshingle.ecommerce.Service;



import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.*;
import org.riteshingle.ecommerce.Entity.Product;
import org.riteshingle.ecommerce.Entity.Seller;
import org.riteshingle.ecommerce.Repository.OrderItemRepository;
import org.riteshingle.ecommerce.Repository.ProductRepository;
import org.riteshingle.ecommerce.Repository.SellerRepository;
import org.riteshingle.ecommerce.Specification.ProductSpecifications;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final SellerRepository sellerRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    //    pending Sellers
    public List<SellerDTO> unApprovedSellers(Pageable pageable) {
        List<SellerDTO> unApprovedSeller = new ArrayList<>();
        List<Seller> list = sellerRepository.findAll(pageable).stream().filter(seller -> !seller.isApproved()).toList();
        for (Seller seller : list) {
            SellerDTO dto = new SellerDTO();
            dto.setId(seller.getId());
            dto.setApproved(seller.isApproved());
            dto.setBusinessName(seller.getBusinessName());
            dto.setBusinessDescription(seller.getBusinessDescription());
            unApprovedSeller.add(dto);
        }
        return unApprovedSeller;

//        return sellerRepository.findAll().stream().filter(seller -> !seller.isApproved()).toList();
    }

    public String approveSeller(Long SellerId) {
        Seller seller = sellerRepository.findById(SellerId).orElseThrow(() -> new RuntimeException("Seller not found..."));
        if (!seller.isApproved()) {
            seller.setApproved(true);
            sellerRepository.save(seller);
            return "Seller approved";
        }else throw new RuntimeException("Seller is already approved");
    }

    public ProductDTO approveProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getApproved()) {
            product.setApproved(true);
        }
        productRepository.save(product);
        return toDTO(product);
    }

    public List<ProductDTO> unApproveProduct(Pageable pageable){
        List<ProductDTO> unApprovedProductList = new ArrayList<>();
        List<Product> list = productRepository.findByApproved(false,pageable).getContent();
        for (Product product : list){
            ProductDTO dto = toDTO(product);
            unApprovedProductList.add(dto);
        }
        return unApprovedProductList;
    }


    public List<InventoryDTO> inventoryReport(String name, String category, Double minPrice, Double maxPrice, Double averageRating, Pageable pageable){
        Specification<Product> specification = Specification
                .where(ProductSpecifications.hasName(name))
                .and(ProductSpecifications.hasAverageRating(averageRating))
                .and(ProductSpecifications.hasMinPrice(minPrice))
                .and(ProductSpecifications.hasMaxPrice(maxPrice))
                .and(ProductSpecifications.hasCategory(category));

        List<Product> products = productRepository.findAll(specification, pageable).stream().filter(Product::getApproved).toList();
        List<InventoryDTO> inventoryReport = new ArrayList<>();

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

    private ProductDTO toDTO(Product product) {
        List<ReviewResponseDTO> reviewResponseDTOList= (product.getReviews() == null) ? Collections.emptyList() : product.getReviews()
                .stream()
                .map(r -> new ReviewResponseDTO(
                        r.getId(),
                        r.getProduct().getId(),
                        r.getUser().getId(),
                        r.getComment(),
                        r.getRating(),
                        r.getCreatedAt()
                )).toList();
        return ProductDTO.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productDescription(product.getProductDescription())
                .imageUrl(product.getImageUrl())
                .off(product.getOff())
                .averageRating(product.getAverageRating())
                .reviewList(reviewResponseDTOList)
                .totalReview(product.getTotalReview())
                .approved(product.getApproved())
                .stock(product.getStock())
                .price(product.getPrice())
                .category(product.getCategory())
                .build();
    }

}

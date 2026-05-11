package org.riteshingle.ecommerce.Service;

import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.DTO.ProductDTO;
import org.riteshingle.ecommerce.DTO.ReviewResponseDTO;
import org.riteshingle.ecommerce.Entity.Product;
import org.riteshingle.ecommerce.Entity.Seller;
import org.riteshingle.ecommerce.Entity.UserEntity;
import org.riteshingle.ecommerce.Repository.ProductRepository;
import org.riteshingle.ecommerce.Specification.ProductSpecifications;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final AuthService authService;
    private final SellerService sellerService;

    public ProductDTO addProduct(ProductDTO dto) {
        Product product = toEntity(dto);
        UserEntity currentProfile = authService.getCurrentProfile();
        Seller seller = sellerService.findSeller(currentProfile);

        if (currentProfile.getRole().stream()
                .noneMatch(r -> r.getName().equals("ROLE_SELLER"))) {
            throw new RuntimeException("Only Seller can add product");
        }

        if(seller.isApproved()){
            product.setSeller(seller);
            productRepository.save(product);
            return toDTO(product);
        }else throw new RuntimeException("Only Approved seller can live their product ..");
    }

    public ProductDTO removeProduct(Long id) {
        UserEntity currentProfile = authService.getCurrentProfile();
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        if(!product.getSeller().getUser().getId().equals(currentProfile.getId()) && !product.getSeller().isApproved()){
            throw new RuntimeException("You are not allowed to delete product");
        }

        productRepository.delete(product);
        return toDTO(product);
    }

    public List<ProductDTO> filterProducts(
            String name,
            String category,
            Double minPrice,
            Double maxPrice,
            Double averageRating,
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDirection
    ){
        Specification<Product> specification = Specification
                .where(ProductSpecifications.hasName(name))
                .and(ProductSpecifications.hasCategory(category))
                .and(ProductSpecifications.hasAverageRating(averageRating))
                .and(ProductSpecifications.hasMinPrice(minPrice))
                .and(ProductSpecifications.hasMaxPrice(maxPrice));

        Sort sort = (sortDirection != null && sortDirection.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending()
        );
        List<Product> productList = productRepository.findAll(specification, PageRequest.of(pageNo-1,pageSize, sort)).getContent();
        return productList.stream().filter(Product::getApproved).map(this::toDTO).toList();
    }


    public List<ProductDTO> getProducts(Pageable pageable){
        List<Product> productList = productRepository.findAll(pageable).getContent();
        List<ProductDTO> products = new ArrayList<>();
        for(Product product : productList){
            if(product.getApproved()){
                ProductDTO dto = toDTO(product);
                products.add(dto);
            }
        }
        return products;
    }

    public ProductDTO getProductDetails(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found..."));
        return this.toDTO(product);
    }

    private Product toEntity(ProductDTO productDTO) {
        return Product.builder()
                .productName(productDTO.getProductName())
                .category(productDTO.getCategory())
                .imageUrl(productDTO.getImageUrl())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .off(productDTO.getOff())
                .productDescription(productDTO.getProductDescription())
                .build();

    }

    private ProductDTO toDTO(Product product) {
//        List<ReviewResponseDTO> reviewResponseDTOList= (product.getReviews() == null) ? Collections.emptyList() : product.getReviews()
//                .stream()
//                .map(r -> new ReviewResponseDTO(
//                        r.getId(),
//                        r.getProduct().getId(),
//                        r.getUser().getId(),
//                        r.getComment(),
//                        r.getRating(),
//                        r.getCreatedAt()
//                )).toList();
        return ProductDTO.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productDescription(product.getProductDescription())
                .imageUrl(product.getImageUrl())
                .off(product.getOff())
                .averageRating(product.getAverageRating())
                .totalItemsSold(product.getSalesVolume())
//                .reviewList(reviewResponseDTOList)
                .totalReview((product.getTotalReview() == null) ? 0 : product.getTotalReview())
                .approved(product.getApproved())
                .stock(product.getStock())
                .price(product.getPrice())
                .category(product.getCategory())
                .build();
    }

    private ProductDTO productDetails(Product product) {
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
                .totalItemsSold(product.getSalesVolume())
//                .reviewList(reviewResponseDTOList)
                .totalReview(product.getTotalReview())
                .approved(product.getApproved())
                .stock(product.getStock())
                .price(product.getPrice())
                .category(product.getCategory())
                .build();
    }

}

package org.riteshingle.ecommerce.Specification;

import jakarta.persistence.criteria.Join;
import org.riteshingle.ecommerce.Entity.Product;
import org.riteshingle.ecommerce.Entity.Review;
import org.riteshingle.ecommerce.Entity.Seller;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProductSpecifications {

    public static Specification<Product> hasName(String name){
        return (root, query, criteriaBuilder) ->
                name == null ? null :
                criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")),"%" +name.toLowerCase() +"%" );
    }

    public static Specification<Product> hasCategory(String category){
        return ((root, query, criteriaBuilder) ->
                category == null ? null :criteriaBuilder.equal(root.get("category"),category));
    }

    public static Specification<Product> hasMinPrice(Double minPrice){
        return (root, query, criteriaBuilder) ->
                minPrice == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("price"),minPrice);
    }

    public static Specification<Product> hasMaxPrice(Double maxPrice){
        return (root, query, criteriaBuilder) ->
                maxPrice == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("price"),maxPrice);
    }

    public static Specification<Product> hasAverageRating(Double averageRating) {
        return (root, query, cb) -> {
            if (averageRating == null) return null;
            Join<Product, Review> reviewJoin = root.join("reviews");
            query.groupBy(root.get("id"));
            query.having(cb.ge(cb.avg(reviewJoin.get("rating")), averageRating));
            return cb.conjunction(); // dummy predicate
        };
    }

    public static Specification<Product> hasSeller(Seller seller) {

        return (root, query, cb) -> {

            if (seller == null) {
                return null;
            }

            return cb.equal(root.get("seller"), seller);
        };
    }
}

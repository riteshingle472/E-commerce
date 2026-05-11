package org.riteshingle.ecommerce.Repository;

import org.riteshingle.ecommerce.Entity.Product;
import org.riteshingle.ecommerce.Entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product> {

    Page<Product> findByApproved(Boolean approved , Pageable pageable);

    List<Product> findBySeller(Seller seller);
}

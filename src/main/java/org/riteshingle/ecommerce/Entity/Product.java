package org.riteshingle.ecommerce.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String category;
    private Boolean approved;
    private Double off;
    private Double averageRating;
    private BigDecimal price;
    private Integer stock;
    private Integer totalReview;
    private Integer salesVolume;

    @ElementCollection
    @CollectionTable(name = "product_images",joinColumns = @JoinColumn(name = "product_id"))
    private List<String> imageUrl;

    @Column(length = 2000)
    private String productDescription;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @OneToMany(mappedBy = "product" , fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Review> reviews;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItemList;

    @PrePersist
    public void prePersist(){
        this.approved = false;
        this.totalReview = 0;
        this.salesVolume = 0;
        this.averageRating = 0.0;
    }

}

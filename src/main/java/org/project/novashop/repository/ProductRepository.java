package org.project. novashop.repository;

import org.project.novashop. model.Product;
import org. springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework. data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype. Repository;

import java.util. Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndDeletedFalse(Long id);

    boolean existsByNomAndDeletedFalse(String nom);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p " +
            "WHERE p.nom = :nom AND p.deleted = false AND p.id != :id")
    boolean existsByNomAndDeletedFalseAndIdNot(@Param("nom") String nom, @Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE " +
            "(:nom IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
            "(:minPrice IS NULL OR p.prixUnitaire >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.prixUnitaire <= :maxPrice) AND " +
            "(:inStock IS NULL OR (:inStock = true AND p.stock > 0) OR (:inStock = false)) AND " +
            "p.deleted = false")
    Page<Product> findAllWithFilters(
            @Param("nom") String nom,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable
    );

    long countByDeletedFalse();
}
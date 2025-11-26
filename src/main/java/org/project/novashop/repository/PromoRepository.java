package org.project.novashop.repository;

import org.project.novashop.model.Promo;
import org.springframework.data.jpa. repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoRepository extends JpaRepository<Promo, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    Optional<Promo> findByCode(String code);
}
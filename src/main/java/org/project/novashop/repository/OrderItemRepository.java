package org. project.novashop.repository;

import org.project.novashop.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util. List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByCommandeId(Long commandeId);

    List<OrderItem> findByProductId(Long productId);
}
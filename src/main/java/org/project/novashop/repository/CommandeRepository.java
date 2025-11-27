package org.project.novashop. repository;

import org.project. novashop.model.Commande;
import org.project. novashop.enums.OrderStatus;
import org.springframework.data.jpa.repository. JpaRepository;
import org. springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    Optional<Commande> findByNumeroCommande(String numeroCommande);

    boolean existsByNumeroCommande(String numeroCommande);

    List<Commande> findByClientIdOrderByDateCreationDesc(Long clientId);

    List<Commande> findByStatutOrderByDateCreationDesc(OrderStatus statut);

    List<Commande> findByClientIdAndStatutOrderByDateCreationDesc(Long clientId, OrderStatus statut);

    long countByClientId(Long clientId);

    long countByClientIdAndStatut(Long clientId, OrderStatus statut);
}
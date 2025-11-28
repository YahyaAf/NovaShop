package org.project.novashop. repository;

import org.project. novashop.enums.PaymentStatus;
import org.project.novashop. enums.PaymentType;
import org.project.novashop. model.Payment;
import org. springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa. repository.Query;
import org. springframework.data.repository.query. Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCommandeIdOrderByDatePaiementDesc(Long commandeId);

    Optional<Payment> findByNumeroPaiement(Integer numeroPaiement);

    boolean existsByNumeroPaiement(Integer numeroPaiement);

    List<Payment> findByCommandeIdAndStatut(Long commandeId, PaymentStatus statut);

    List<Payment> findByCommandeIdAndTypePaiement(Long commandeId, PaymentType typePaiement);

    @Query("SELECT COALESCE(SUM(p.montant), 0. 0) FROM Payment p " +
            "WHERE p.commande.id = :commandeId AND p.statut = 'ENCAISSE'")
    Double calculateTotalEncaisseByCommande(@Param("commandeId") Long commandeId);

    long countByCommandeId(Long commandeId);

    List<Payment> findByTypePaiementAndStatut(PaymentType typePaiement, PaymentStatus statut);
}
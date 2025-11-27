package org.project.novashop.repository;

import org.project.novashop.enums.CustomerTier;
import org.project.novashop.model.Client;
import org.springframework.data.jpa.repository. JpaRepository;
import org. springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    Optional<Client> findByTelephone(String telephone);
    boolean existsByTelephone(String telephone);
    boolean existsByTelephoneAndIdNot(String telephone, Long id);
    List<Client> findByNiveauFidelite(CustomerTier niveauFidelite);
    List<Client> findByTotalSpentGreaterThanEqual(Double montant);

}
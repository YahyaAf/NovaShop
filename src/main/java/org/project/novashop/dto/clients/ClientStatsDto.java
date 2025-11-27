package org.project.novashop.dto.clients;

import org.project.novashop.enums.CustomerTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientStatsDto {

    private Long clientId;
    private String nom;
    private CustomerTier niveauFidelite;
    private Integer totalOrders;
    private Double totalSpent;

    private CustomerTier prochainNiveau;
    private Integer commandesRestantes;
    private Double montantRestant;
}
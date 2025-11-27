package org.project.novashop.dto.commandes;

import org.project.novashop.enums.CustomerTier;
import org.project.novashop.enums.OrderStatus;
import lombok. AllArgsConstructor;
import lombok.Builder;
import lombok. Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeResponseDto {

    private Long id;
    private String numeroCommande;
    private LocalDateTime dateCreation;

    private Long clientId;
    private String clientNom;
    private CustomerTier clientNiveauFidelite;

    private List<OrderItemResponseDto> items;

    private Double sousTotalHt;
    private Double montantRemiseFidelite;
    private Double montantRemisePromo;
    private Double montantRemiseTotal;
    private Double montantHtApresRemise;
    private Double tva;
    private Double totalTTC;

    private String codePromo;

    private Double montantRestant;

    private OrderStatus statut;
}
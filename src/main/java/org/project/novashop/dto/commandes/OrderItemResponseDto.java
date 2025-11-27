package org.project.novashop.dto.commandes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok. NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDto {

    private Long id;
    private Long productId;
    private String productNom;
    private Integer quantite;
    private Double prixUnitaire;
    private Double totalLigne;
}
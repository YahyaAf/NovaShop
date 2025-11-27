package org.project.novashop. dto.commandes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeCalculationDto {

    private Double sousTotalHt;
    private Double remiseFidelitePourcentage;
    private Double montantRemiseFidelite;
    private Double remisePromoPourcentage;
    private Double montantRemisePromo;
    private Double montantRemiseTotal;
    private Double montantHtApresRemise;
    private Double tauxTva;
    private Double montantTva;
    private Double totalTTC;
}
package org.project.novashop.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSummaryDto {

    private Long commandeId;
    private String numeroCommande;
    private Double totalTTC;
    private Double totalPaye;
    private Double montantRestant;
    private Boolean estCompletementPaye;

    private List<PaymentResponseDto> payments;
}
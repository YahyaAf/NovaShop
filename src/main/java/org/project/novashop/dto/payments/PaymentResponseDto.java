package org.project.novashop.dto.payments;

import lombok. AllArgsConstructor;
import lombok.Builder;
import lombok. Data;
import lombok.NoArgsConstructor;
import org.project.novashop.enums.PaymentStatus;
import org.project.novashop.enums.PaymentType;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

    private Long id;
    private Integer numeroPaiement;
    private Double montant;
    private PaymentType typePaiement;
    private PaymentStatus statut;
    private LocalDate datePaiement;
    private LocalDate dateEncaissement;
    private String reference;
    private String banque;
    private LocalDate echeance;

    private Long commandeId;
    private String numeroCommande;
    private Double commandeMontantRestant;
}
package org.project.novashop.dto. payments;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.novashop.enums. PaymentType;

import java. time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {

    @NotNull(message = "L'ID de la commande est requis")
    private Long commandeId;

    @NotNull(message = "Le montant est requis")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private Double montant;

    @NotNull(message = "Le type de paiement est requis")
    private PaymentType typePaiement;

    @Size(max = 100, message = "La référence ne peut pas dépasser 100 caractères")
    private String reference;

    @Size(max = 100, message = "Le nom de la banque ne peut pas dépasser 100 caractères")
    private String banque;

    @Future(message = "La date d'échéance doit être dans le futur")
    private LocalDate echeance;
}
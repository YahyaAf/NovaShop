package org.project.novashop. dto.payments;

import jakarta. validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok. Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project. novashop.enums.PaymentStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePaymentStatusDto {

    @NotNull(message = "Le statut est requis")
    private PaymentStatus statut;

    private LocalDate dateEncaissement;
}
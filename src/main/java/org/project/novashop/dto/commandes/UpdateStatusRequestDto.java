package org.project.novashop.dto.commandes;

import jakarta. validation.constraints.NotNull;
import org.project.novashop.enums.OrderStatus;
import lombok. AllArgsConstructor;
import lombok.Builder;
import lombok. Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusRequestDto {

    @NotNull(message = "Le statut est requis")
    private OrderStatus statut;
}
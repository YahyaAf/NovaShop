package org.project.novashop.dto.commandes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeRequestDto {

    @NotEmpty(message = "La commande doit contenir au moins un produit")
    @Valid
    private List<OrderItemRequestDto> items;

    @Size(max = 50, message = "Le code promo ne peut pas dépasser 50 caractères")
    private String codePromo;
}
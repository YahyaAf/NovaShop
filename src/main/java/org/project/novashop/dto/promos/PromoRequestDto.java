package org.project.novashop.dto. promos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoRequestDto {

    @NotBlank(message = "Le code promo est requis")
    @Size(min = 3, max = 50, message = "Le code doit contenir entre 3 et 50 caractères")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Le code doit contenir uniquement des lettres majuscules et chiffres")
    private String code;

    @NotNull(message = "Le nombre maximum d'utilisations est requis")
    @Min(value = 1, message = "Le nombre maximum d'utilisations doit être au moins 1")
    @Max(value = 10000, message = "Le nombre maximum d'utilisations ne peut pas dépasser 10000")
    private Integer maxUsage;
}
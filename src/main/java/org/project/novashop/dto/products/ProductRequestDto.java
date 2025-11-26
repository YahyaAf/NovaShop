package org. project.novashop.dto. products;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "Le nom du produit est requis")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @NotNull(message = "Le prix unitaire est requis")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
    @DecimalMax(value = "999999.99", message = "Le prix ne peut pas dépasser 999999.99")
    private Double prixUnitaire;

    @NotNull(message = "Le stock est requis")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    @Max(value = 999999, message = "Le stock ne peut pas dépasser 999999")
    private Integer stock;
}
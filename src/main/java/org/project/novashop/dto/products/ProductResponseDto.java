package org.project.novashop.dto.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok. NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private Long id;
    private String nom;
    private String description;
    private Double prixUnitaire;
    private Integer stock;
    private Boolean deleted;
}
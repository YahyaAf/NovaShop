package org.project.novashop.dto.clients;

import org.project.novashop.enums.CustomerTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDto {

    private Long id;
    private String telephone;
    private String adresse;
    private CustomerTier niveauFidelite;
    private Long userId;
    private String username;
}
package org.project.novashop.dto.clients;

import org.project.novashop.dto.users.UserRequestDto;
import org.project.novashop.enums.CustomerTier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequestDto {

    @NotBlank(message = "Telephone is required")
    @Pattern(regexp = "^(\\+212|0)[5-7][0-9]{8}$", message = "Invalid Moroccan phone number")
    private String telephone;

    @NotBlank(message = "Adresse is required")
    @Size(min = 10, max = 255, message = "Adresse must be between 10 and 255 characters")
    private String adresse;

    @NotNull(message = "Niveau de fidélité is required")
    private CustomerTier niveauFidelite;

    @Valid
    @NotNull(message = "User information is required")
    private UserRequestDto user;
}
package org.project. novashop.mapper;

import org.mapstruct. Mapper;
import org.mapstruct.Mapping;
import org.project.novashop.dto.payments.PaymentResponseDto;
import org.project. novashop.model.Payment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "commande.id", target = "commandeId")
    @Mapping(source = "commande.numeroCommande", target = "numeroCommande")
    @Mapping(source = "commande.montantRestant", target = "commandeMontantRestant")
    PaymentResponseDto toResponseDto(Payment payment);

    List<PaymentResponseDto> toResponseDtoList(List<Payment> payments);
}
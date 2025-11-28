package org.project.novashop.service;

import org. project.novashop.dto. api.ApiResponse;
import org.project. novashop.dto.payments.*;
import org.project.novashop.enums.PaymentStatus;
import org.project.novashop. enums.PaymentType;
import org.project.novashop. exception.ResourceNotFoundException;
import org.project.novashop.mapper.PaymentMapper;
import org.project.novashop.model. Commande;
import org.project.novashop.model.Payment;
import org.project.novashop.repository.CommandeRepository;
import org.project.novashop.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time. LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CommandeRepository commandeRepository;
    private final PaymentMapper paymentMapper;

    private static final Double ESPECES_LIMIT = 20000.0;

    public PaymentService(PaymentRepository paymentRepository,
                          CommandeRepository commandeRepository,
                          PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.commandeRepository = commandeRepository;
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public ApiResponse<PaymentResponseDto> create(PaymentRequestDto requestDto) {
        Commande commande = commandeRepository.findById(requestDto.getCommandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande", requestDto.getCommandeId()));

        if (requestDto.getMontant() > commande.getMontantRestant()) {
            throw new IllegalArgumentException(
                    "Montant supérieur au restant dû.  " +
                            "Restant: " + commande.getMontantRestant() + " DH, " +
                            "Demandé: " + requestDto.getMontant() + " DH"
            );
        }

        if (requestDto. getMontant() <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");
        }

        if (requestDto.getTypePaiement() == PaymentType.ESPECES) {
            if (requestDto. getMontant() > ESPECES_LIMIT) {
                throw new IllegalArgumentException(
                        "Paiement en espèces limité à " + ESPECES_LIMIT +
                                " DH (Art. 193 CGI).  Montant demandé: " +
                                requestDto.getMontant() + " DH"
                );
            }
        }

        Payment payment = Payment.builder()
                .numeroPaiement(generateNumeroPaiement())
                .montant(requestDto.getMontant())
                .typePaiement(requestDto.getTypePaiement())
                .statut(PaymentStatus.ENCAISSE)
                .datePaiement(LocalDate.now())
                .dateEncaissement(LocalDate.now())
                .reference(requestDto.getReference())
                .banque(requestDto. getBanque())
                .commande(commande)
                . build();

        Payment savedPayment = paymentRepository.save(payment);

        updateMontantRestant(commande);

        PaymentResponseDto responseDto = paymentMapper.toResponseDto(savedPayment);
        return new ApiResponse<>("Paiement de " + savedPayment.getMontant() + " DH encaissé avec succès", responseDto);
    }


    public ApiResponse<PaymentResponseDto> findById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        PaymentResponseDto responseDto = paymentMapper.toResponseDto(payment);
        return new ApiResponse<>("Paiement récupéré avec succès", responseDto);
    }

    public ApiResponse<List<PaymentResponseDto>> findByCommande(Long commandeId) {
        if (!commandeRepository.existsById(commandeId)) {
            throw new ResourceNotFoundException("Commande", commandeId);
        }

        List<Payment> payments = paymentRepository.findByCommandeIdOrderByDatePaiementDesc(commandeId);
        List<PaymentResponseDto> responseDtos = paymentMapper.toResponseDtoList(payments);

        return new ApiResponse<>("Paiements de la commande récupérés avec succès", responseDtos);
    }

    public ApiResponse<PaymentSummaryDto> getPaymentSummary(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", commandeId));

        List<Payment> payments = paymentRepository.findByCommandeIdOrderByDatePaiementDesc(commandeId);
        List<PaymentResponseDto> paymentDtos = paymentMapper.toResponseDtoList(payments);

        Double totalPaye = paymentRepository.calculateTotalEncaisseByCommande(commandeId);

        PaymentSummaryDto summary = PaymentSummaryDto.builder()
                .commandeId(commande.getId())
                . numeroCommande(commande.getNumeroCommande())
                . totalTTC(commande.getTotalTTC())
                .totalPaye(totalPaye)
                .montantRestant(commande.getMontantRestant())
                .estCompletementPaye(commande.getMontantRestant() == 0.0)
                .payments(paymentDtos)
                .build();

        return new ApiResponse<>("Résumé des paiements récupéré avec succès", summary);
    }

    private void updateMontantRestant(Commande commande) {
        Double totalEncaisse = paymentRepository.calculateTotalEncaisseByCommande(commande.getId());
        Double montantRestant = commande. getTotalTTC() - totalEncaisse;

        commande.setMontantRestant(Math.max(0.0, montantRestant));
        commandeRepository.save(commande);
    }

    private Integer generateNumeroPaiement() {
        Integer numero;
        do {
            numero = (int) (Math.random() * 100000) + 1;
        } while (paymentRepository.existsByNumeroPaiement(numero));
        return numero;
    }
}
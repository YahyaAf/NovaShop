package org.project.novashop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.project.novashop.dto.api.ApiResponse;
import org.project.novashop.dto.commandes.CommandeRequestDto;
import org.project.novashop.dto.commandes.CommandeResponseDto;
import org.project.novashop.dto.commandes.OrderItemRequestDto;
import org.project.novashop.enums.CustomerTier;
import org.project.novashop.enums.OrderStatus;
import org.project.novashop.enums.UserRole;
import org.project.novashop.exception.ResourceNotFoundException;
import org.project.novashop.mapper.CommandeMapper;
import org.project.novashop.model.*;
import org.project.novashop.repository.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandeServiceTest {

    @Mock private CommandeRepository commandeRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CommandeMapper commandeMapper;

    @InjectMocks private CommandeService commandeService;

    private Client client;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("Test user")
                .password("test133")
                .role(UserRole.CLIENT)
                .build();

        client = Client.builder()
                .id(1L)
                .user(user)
                .niveauFidelite(CustomerTier.BASIC)
                .totalOrders(0)
                .totalSpent(0.0)
                .build();

        product = Product.builder()
                .id(1L)
                .nom("Test Product")
                .prixUnitaire(100.0)
                .stock(10)
                .deleted(false)
                .build();
    }

    @Test
    void testCreateCommande_Success() {
        CommandeRequestDto requestDto = CommandeRequestDto.builder()
                .clientId(client.getId())
                .items(Collections.singletonList(OrderItemRequestDto.builder()
                        .productId(product.getId())
                        .quantite(2)
                        .build()))
                .build();

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(productRepository.findByIdAndDeletedFalse(product.getId())).thenReturn(Optional.of(product));
        when(commandeRepository.existsByNumeroCommande(anyString())).thenReturn(false);
        when(commandeRepository.save(any(Commande.class)))
                .thenAnswer(invocation -> {
                    Commande commande = invocation.getArgument(0);
                    commande.setId(55L);
                    return commande;
                });
        CommandeResponseDto responseDto = CommandeResponseDto.builder()
                .id(55L)
                .numeroCommande("CMD-2025-0001")
                .statut(OrderStatus.PENDING)
                .build();
        when(commandeMapper.toResponseDto(any(Commande.class))).thenReturn(responseDto);

        ApiResponse<CommandeResponseDto> response = commandeService.create(client.getId(), requestDto);

        assertThat(response.getMessage()).isEqualTo("Commande créée avec succès");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getId()).isEqualTo(55L);
        assertThat(response.getData().getStatut()).isEqualTo(OrderStatus.PENDING);
        verify(productRepository).save(any(Product.class));
        verify(commandeRepository).save(any(Commande.class));
    }

    @Test
    void testCreateCommande_StockInsuffisant_Rejected() {
        Product productOutOfStock = Product.builder()
                .id(2L)
                .nom("Produit épuisé")
                .prixUnitaire(500.0)
                .stock(1)
                .deleted(false)
                .build();

        CommandeRequestDto requestDto = CommandeRequestDto.builder()
                .clientId(client.getId())
                .items(Collections.singletonList(OrderItemRequestDto.builder()
                        .productId(productOutOfStock.getId())
                        .quantite(5)
                        .build()))
                .build();

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(productRepository.findByIdAndDeletedFalse(productOutOfStock.getId())).thenReturn(Optional.of(productOutOfStock));
        when(commandeRepository.existsByNumeroCommande(anyString())).thenReturn(false);
        when(commandeRepository.save(any(Commande.class))).thenAnswer(invocation -> {
            Commande commande = invocation.getArgument(0);
            commande.setId(99L);
            return commande;
        });

        ApiResponse<CommandeResponseDto> response = commandeService.create(client.getId(), requestDto);

        assertThat(response.getMessage()).contains("Commande rejetée");
        assertThat(response.getData()).isNull();
        verify(commandeRepository).save(any(Commande.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testConfirmCommande_Success() {
        Commande commande = Commande.builder()
                .id(77L)
                .statut(OrderStatus.PENDING)
                .montantRestant(0.0)
                .client(client)
                .totalTTC(1000.0)
                .build();

        when(commandeRepository.findById(commande.getId())).thenReturn(Optional.of(commande));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(commandeRepository.save(any(Commande.class))).thenAnswer(inv -> inv.getArgument(0));
        CommandeResponseDto responseDto = CommandeResponseDto.builder()
                .id(commande.getId())
                .statut(OrderStatus.CONFIRMED)
                .build();
        when(commandeMapper.toResponseDto(any(Commande.class))).thenReturn(responseDto);

        ApiResponse<CommandeResponseDto> response = commandeService.confirm(commande.getId());

        assertThat(response.getMessage()).isEqualTo("Commande confirmée avec succès");
        assertThat(response.getData().getStatut()).isEqualTo(OrderStatus.CONFIRMED);
        verify(clientRepository).save(any(Client.class));
        verify(commandeRepository).save(any(Commande.class)); // CHANGE from times(2) to just once!
    }

    @Test
    void testConfirmCommande_MontantRestantNotZero_ThrowsException() {
        Commande commande = Commande.builder()
                .id(81L)
                .statut(OrderStatus.PENDING)
                .montantRestant(100.0)
                .build();

        when(commandeRepository.findById(commande.getId())).thenReturn(Optional.of(commande));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> commandeService.confirm(commande.getId()));
        assertEquals("Impossible de confirmer la commande. Montant restant à payer: 100.0 DH", ex.getMessage());
    }

    @Test
    void testCancelCommande_PaymentExists_ThrowsException() {
        Payment payment = Payment.builder().montant(100.0).build();
        List<Payment> payments = List.of(payment);

        Commande commande = Commande.builder()
                .id(101L)
                .statut(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .payments(payments)
                .build();

        when(commandeRepository.findById(commande.getId())).thenReturn(Optional.of(commande));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> commandeService.cancel(commande.getId()));
        assertEquals("Impossible d'annuler la commande. Des paiements ont été réalisés.", ex.getMessage());
    }

    @Test
    void testFindById_ThrowsNotFound() {
        when(commandeRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commandeService.findById(999L));
    }

    @Test
    void testFindAll_ReturnsList() {
        Commande commande1 = Commande.builder().id(1L).build();
        Commande commande2 = Commande.builder().id(2L).build();

        when(commandeRepository.findAll()).thenReturn(List.of(commande1, commande2));
        when(commandeMapper.toResponseDto(any(Commande.class)))
                .thenReturn(CommandeResponseDto.builder().build());

        ApiResponse<List<CommandeResponseDto>> response = commandeService.findAll();
        assertThat(response.getData()).hasSize(2);
    }

}

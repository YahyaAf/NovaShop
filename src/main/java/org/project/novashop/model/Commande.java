package org.project.novashop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.novashop.enums.OrderStatus;

import java.time.LocalDateTime;
import java. util.ArrayList;
import java. util.List;

@Entity
@Table(name = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String numeroCommande;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(nullable = false)
    private Double sousTotalHt;

    @Column(nullable = false)
    @Builder.Default
    private Double montantRemise = 0.0;

    @Column(nullable = false)
    private Double tva;

    @Column(nullable = false)
    private Double totalTTC;

    @Column(length = 50)
    private String codePromo;

    @Column(nullable = false)
    @Builder. Default
    private Double montantRestant = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus statut = OrderStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "promo_id")
    private Promo promo;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
}
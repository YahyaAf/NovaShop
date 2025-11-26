package org.project.novashop. model;

import org.project.novashop.enums.CustomerTier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String adresse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CustomerTier niveauFidelite = CustomerTier.BASIC;

    @OneToOne(cascade = {CascadeType. PERSIST, CascadeType. MERGE}, orphanRemoval = false)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
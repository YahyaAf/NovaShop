package org.project.novashop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxUsage = 1;

    @Column(nullable = false)
    @Builder.Default
    private Integer usageCount = 0;
}
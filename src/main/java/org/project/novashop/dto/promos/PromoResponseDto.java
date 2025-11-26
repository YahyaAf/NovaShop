package org.project. novashop.dto.promos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoResponseDto {

    private Long id;
    private String code;
    private Integer maxUsage;
    private Integer usageCount;

    public boolean isValid() {
        return usageCount < maxUsage;
    }

    public int getRemainingUses() {
        return maxUsage - usageCount;
    }
}
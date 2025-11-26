package org.project.novashop.service;

import org.project.novashop.dto.api.ApiResponse;
import org.project.novashop. dto.promos.PromoRequestDto;
import org.project. novashop.dto.promos.PromoResponseDto;
import org.project.novashop. exception.DuplicateResourceException;
import org.project.novashop.exception.ResourceNotFoundException;
import org.project.novashop.mapper.PromoMapper;
import org.project.novashop.model.Promo;
import org.project.novashop.repository.PromoRepository;
import org.springframework.stereotype. Service;
import org.springframework. transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PromoService {

    private final PromoRepository promoRepository;
    private final PromoMapper promoMapper;

    public PromoService(PromoRepository promoRepository, PromoMapper promoMapper) {
        this.promoRepository = promoRepository;
        this.promoMapper = promoMapper;
    }

    @Transactional
    public ApiResponse<PromoResponseDto> create(PromoRequestDto requestDto) {
        if (promoRepository.existsByCode(requestDto.getCode())) {
            throw new DuplicateResourceException("Promo", "code", requestDto.getCode());
        }

        Promo promo = promoMapper.toEntity(requestDto);
        Promo savedPromo = promoRepository.save(promo);
        PromoResponseDto responseDto = promoMapper.toResponseDto(savedPromo);

        return new ApiResponse<>("Code promo créé avec succès", responseDto);
    }

    @Transactional
    public ApiResponse<PromoResponseDto> update(Long id, PromoRequestDto requestDto) {
        Promo promo = promoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo", id));

        if (promoRepository.existsByCodeAndIdNot(requestDto.getCode(), id)) {
            throw new DuplicateResourceException("Promo", "code", requestDto.getCode());
        }

        promoMapper.updateEntityFromDto(requestDto, promo);
        Promo updatedPromo = promoRepository.save(promo);
        PromoResponseDto responseDto = promoMapper.toResponseDto(updatedPromo);

        return new ApiResponse<>("Code promo mis à jour avec succès", responseDto);
    }

    public ApiResponse<PromoResponseDto> findById(Long id) {
        Promo promo = promoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo", id));

        PromoResponseDto responseDto = promoMapper.toResponseDto(promo);

        return new ApiResponse<>("Code promo récupéré avec succès", responseDto);
    }

    public ApiResponse<List<PromoResponseDto>> findAll() {
        List<Promo> promos = promoRepository.findAll();

        List<PromoResponseDto> responseDtos = promos.stream()
                .map(promoMapper::toResponseDto)
                .collect(Collectors. toList());

        return new ApiResponse<>("Liste des codes promo récupérée avec succès", responseDtos);
    }

    @Transactional
    public ApiResponse<Void> delete(Long id) {
        if (!promoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promo", id);
        }

        promoRepository.deleteById(id);

        return new ApiResponse<>("Code promo supprimé avec succès");
    }

    @Transactional
    public ApiResponse<PromoResponseDto> validateAndApply(String code) {
        Promo promo = promoRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo", code));

        if (promo.getUsageCount() >= promo.getMaxUsage()) {
            throw new IllegalArgumentException("Code promo épuisé");
        }

        promo.setUsageCount(promo.getUsageCount() + 1);
        Promo updatedPromo = promoRepository.save(promo);
        PromoResponseDto responseDto = promoMapper.toResponseDto(updatedPromo);

        return new ApiResponse<>("Code promo appliqué avec succès", responseDto);
    }

    public ApiResponse<PromoResponseDto> checkValidity(String code) {
        Promo promo = promoRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo", code));

        PromoResponseDto responseDto = promoMapper.toResponseDto(promo);

        if (! responseDto.isValid()) {
            throw new IllegalArgumentException("Code promo épuisé");
        }

        return new ApiResponse<>("Code promo valide", responseDto);
    }
}
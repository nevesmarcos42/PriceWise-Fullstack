package br.com.marcos.pricewiseapi.service;

import br.com.marcos.pricewiseapi.dto.CreateCouponDTO;
import br.com.marcos.pricewiseapi.dto.CouponResponseDTO;
import br.com.marcos.pricewiseapi.model.Coupon;
import br.com.marcos.pricewiseapi.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponResponseDTO create(CreateCouponDTO dto) {
        couponRepository.findByCodeIgnoreCase(dto.getCode()).ifPresent(c -> {
            throw new IllegalArgumentException("Já existe um cupom com o código informado.");
        });

        if (dto.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de expiração precisa ser futura.");
        }

        Coupon coupon = couponRepository.save(Coupon.builder()
                .code(dto.getCode())
                .discountType(dto.getDiscountType())
                .discountValue(dto.getDiscountValue())
                .expirationDate(dto.getExpirationDate())
                .build());

        return CouponResponseDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType().name())
                .discountValue(coupon.getDiscountValue())
                .expirationDate(coupon.getExpirationDate())
                .build();
    }
}

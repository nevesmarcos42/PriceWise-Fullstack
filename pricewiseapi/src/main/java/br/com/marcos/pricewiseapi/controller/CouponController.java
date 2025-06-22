package br.com.marcos.pricewiseapi.controller;

import br.com.marcos.pricewiseapi.dto.CreateCouponDTO;
import br.com.marcos.pricewiseapi.dto.CouponResponseDTO;
import br.com.marcos.pricewiseapi.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponResponseDTO> create(@RequestBody @Valid CreateCouponDTO dto) {
        CouponResponseDTO created = couponService.create(dto);
        return ResponseEntity
                .created(URI.create("/api/v1/coupons/" + created.getId()))
                .body(created);
    }
}

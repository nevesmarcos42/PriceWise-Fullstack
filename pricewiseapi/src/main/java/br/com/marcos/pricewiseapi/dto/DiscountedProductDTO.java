package br.com.marcos.pricewiseapi.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountedProductDTO {
    private Long productId;
    private String productName;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private String appliedCoupon;
}

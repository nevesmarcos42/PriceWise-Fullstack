package br.com.marcos.pricewiseapi.mapper;

import br.com.marcos.pricewiseapi.model.Product;
import br.com.marcos.pricewiseapi.dto.ProductResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .build();
    }
}

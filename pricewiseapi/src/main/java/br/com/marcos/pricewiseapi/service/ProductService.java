package br.com.marcos.pricewiseapi.service;

import br.com.marcos.pricewiseapi.dto.CreateProductDTO;
import br.com.marcos.pricewiseapi.dto.ProductResponseDTO;
import br.com.marcos.pricewiseapi.exeception.ProductNotFoundException;
import br.com.marcos.pricewiseapi.model.Product;
import br.com.marcos.pricewiseapi.repository.ProductRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Long create(CreateProductDTO dto) {
        String normalizedName = normalize(dto.getName());

        boolean exists = repository.existsByNameIgnoreCase(normalizedName);
        if (exists) {
            throw new EntityExistsException("Produto com esse nome já existe.");
        }

        Product entity = Product.builder()
                .name(dto.getName().trim())
                .description(dto.getDescription())
                .stock(dto.getStock())
                .price(dto.getPrice())
                .createdAt(OffsetDateTime.now())
                .build();

        return repository.save(entity).getId();
    }

    private String normalize(String input) {
        if (input == null)
            return null;
        return Normalizer
                .normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();
    }

    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(product -> ProductResponseDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .stock(product.getStock())
                        .price(product.getPrice())
                        .build());
    }

    public ProductResponseDTO findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .build();
    }
}
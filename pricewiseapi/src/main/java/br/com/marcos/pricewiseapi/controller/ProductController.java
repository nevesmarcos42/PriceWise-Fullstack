package br.com.marcos.pricewiseapi.controller;

import br.com.marcos.pricewiseapi.dto.CreateProductDTO;
import br.com.marcos.pricewiseapi.dto.ProductResponseDTO;
import br.com.marcos.pricewiseapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateProductDTO dto) {
        Long id = productService.create(dto);
        URI location = URI.create("/api/v1/products/" + id);
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> findAll(Pageable pageable) {
        Page<ProductResponseDTO> result = productService.findAll(pageable);
        return ResponseEntity.ok(result);
    }
}
//
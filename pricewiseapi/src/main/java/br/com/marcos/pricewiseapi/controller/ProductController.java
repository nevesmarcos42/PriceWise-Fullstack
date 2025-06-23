package br.com.marcos.pricewiseapi.controller;

import br.com.marcos.pricewiseapi.dto.CreateProductDTO;
import br.com.marcos.pricewiseapi.dto.DiscountedProductDTO;
import br.com.marcos.pricewiseapi.dto.ProductResponseDTO;
import br.com.marcos.pricewiseapi.model.Product;
import br.com.marcos.pricewiseapi.mapper.ProductMapper;
//import br.com.marcos.pricewiseapi.model.Product;
import br.com.marcos.pricewiseapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final ProductMapper mapper;

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

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
        ProductResponseDTO dto = productService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/discounted")
    public ResponseEntity<DiscountedProductDTO> getDiscountedProduct(
            @PathVariable Long id,
            @RequestParam String coupon) {
        DiscountedProductDTO dto = productService.getDiscountedProduct(id, coupon);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@PathVariable Long id) {
        productService.softDelete(id);
    }

    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restore(@PathVariable Long id) {
        productService.restore(id);
    }

    @GetMapping("/deleted")
    public Page<ProductResponseDTO> findAllDeleted(Pageable pageable) {
        return productService.findAllDeleted(pageable);
    }

    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ProductResponseDTO patch(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException {
        Product patched = productService.applyMergePatch(id, patch);
        return mapper.toResponseDTO(patched);
    }
}
package br.com.marcos.pricewiseapi.service;

import br.com.marcos.pricewiseapi.dto.CreateProductDTO;
import br.com.marcos.pricewiseapi.dto.DiscountedProductDTO;
import br.com.marcos.pricewiseapi.dto.ProductResponseDTO;
import br.com.marcos.pricewiseapi.exeception.CouponExpiredException;
import br.com.marcos.pricewiseapi.exeception.CouponNotFoundException;
import br.com.marcos.pricewiseapi.exeception.ProductNotFoundException;
import br.com.marcos.pricewiseapi.exeception.NotFoundException;
import br.com.marcos.pricewiseapi.exeception.BusinessException;
import br.com.marcos.pricewiseapi.model.Coupon;
import br.com.marcos.pricewiseapi.model.Product;
import br.com.marcos.pricewiseapi.repository.CouponRepository;
import br.com.marcos.pricewiseapi.repository.ProductRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final CouponRepository couponRepository;
    private final ObjectMapper objectMapper;

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
        return repository.findAllByDeletedAtIsNull(pageable)
                .map(product -> ProductResponseDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .stock(product.getStock())
                        .price(product.getPrice())
                        .build());
    }

    public Page<ProductResponseDTO> findAllDeleted(Pageable pageable) {
        return repository.findAllByDeletedAtIsNotNull(pageable)
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

    public DiscountedProductDTO getDiscountedProduct(Long productId, String couponCode) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Coupon coupon = couponRepository.findByCodeIgnoreCase(couponCode)
                .orElseThrow(() -> new CouponNotFoundException(couponCode));

        if (coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new CouponExpiredException(couponCode);
        }

        BigDecimal discount = switch (coupon.getDiscountType()) {
            case PERCENTAGE -> product.getPrice().multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
            case FIXED -> coupon.getDiscountValue();
        };

        BigDecimal finalPrice = product.getPrice().subtract(discount).max(BigDecimal.ZERO);

        return DiscountedProductDTO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .originalPrice(product.getPrice())
                .discountedPrice(finalPrice)
                .appliedCoupon(coupon.getCode())
                .build();
    }

    public void softDelete(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        if (product.getDeletedAt() != null) {
            throw new BusinessException("Produto já foi deletado");
        }

        product.setDeletedAt(OffsetDateTime.now());

        repository.save(product); // ← ESSA LINHA FAZ TODA A DIFERENÇA!
    }

    public void restore(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        if (product.getDeletedAt() == null) {
            throw new BusinessException("Produto já está ativo");
        }

        product.setDeletedAt(null);
        repository.save(product);
    }

    public Product applyMergePatch(Long id, JsonMergePatch patch) {
        try {
            // Busca o produto existente
            Product existing = repository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

            // Converte o objeto atual para JsonNode
            JsonNode original = objectMapper.valueToTree(existing);

            // Aplica o patch no JSON
            JsonNode patched = patch.apply(original);

            // Converte de volta para Product
            Product updated = objectMapper.treeToValue(patched, Product.class);
            updated.setId(id); // garante que o ID não muda

            return repository.save(updated);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new RuntimeException("Erro ao aplicar atualização parcial", e);
        }
    }
}
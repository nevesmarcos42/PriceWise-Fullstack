package br.com.marcos.pricewiseapi.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-_,.]+$", message = "O nome contém caracteres inválidos.")
    private String name;

    @Size(max = 300, message = "A descrição pode ter no máximo 300 caracteres.")
    private String description;

    @NotNull(message = "O estoque é obrigatório.")
    @Min(value = 0, message = "O estoque não pode ser negativo.")
    @Max(value = 999_999, message = "O estoque não pode ultrapassar 999.999 unidades.")
    private Integer stock;

    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01", inclusive = true, message = "O preço deve ser no mínimo R$ 0,01.")
    @DecimalMax(value = "1000000.00", inclusive = true, message = "O preço não pode ultrapassar R$ 1.000.000,00.")
    private BigDecimal price;
}

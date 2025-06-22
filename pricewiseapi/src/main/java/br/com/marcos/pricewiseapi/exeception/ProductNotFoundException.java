package br.com.marcos.pricewiseapi.exeception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Produto não encontrado com ID: " + id);
    }
}

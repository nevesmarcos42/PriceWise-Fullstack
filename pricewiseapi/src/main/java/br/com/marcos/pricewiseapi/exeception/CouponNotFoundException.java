package br.com.marcos.pricewiseapi.exeception;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(String code) {
        super("Cupom com código '" + code + "' não encontrado.");
    }
}

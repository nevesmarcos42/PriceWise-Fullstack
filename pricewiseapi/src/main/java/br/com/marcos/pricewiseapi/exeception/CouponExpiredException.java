package br.com.marcos.pricewiseapi.exeception;

public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException(String code) {
        super("O cupom '" + code + "' está expirado.");
    }
}

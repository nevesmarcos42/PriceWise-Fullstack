package br.com.marcos.pricewiseapi.exeception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // ou BAD_REQUEST, se preferir
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
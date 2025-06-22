package br.com.marcos.pricewiseapi.exeception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()));
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<?> handleCouponNotFound(CouponNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "Cupom não encontrado",
                "message", ex.getMessage()));
    }

    @ExceptionHandler(CouponExpiredException.class)
    public ResponseEntity<?> handleCouponExpired(CouponExpiredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Cupom expirado",
                "message", ex.getMessage()));
    }
}

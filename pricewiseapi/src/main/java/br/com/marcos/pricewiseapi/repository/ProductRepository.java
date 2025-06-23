package br.com.marcos.pricewiseapi.repository;

import br.com.marcos.pricewiseapi.model.Product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameIgnoreCase(String name);

    Page<Product> findAllByDeletedAtIsNull(Pageable pageable);
}

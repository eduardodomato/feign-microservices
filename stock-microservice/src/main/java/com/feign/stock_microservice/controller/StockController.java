package com.feign.stock_microservice.controller;

import dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@Slf4j
public class StockController {

    @PostMapping
    public ResponseEntity<String> saveProductType(@RequestBody ProductDTO product) {
        if (product == null) {
            return ResponseEntity.badRequest().body("Product must not be null");
        }
        String name = product.name() == null ? "<no-name>" : product.name();
        // Minimal processing — in a real app we'd persist or update stock here
        log.info("Received product: " + name);
        return ResponseEntity.ok("Received product: " + name);
    }

}

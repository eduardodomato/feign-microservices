package com.feign.product_microservice.client;

import dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="stock-client", url = "${application.services.stock.url}")
public interface StockClient {
    @PostMapping("/api/stock")
    ResponseEntity<String> submitNewProduct(@RequestBody ProductDTO product);

}

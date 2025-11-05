package com.feign.product_microservice.service;

import com.feign.product_microservice.client.StockClient;
import com.feign.product_microservice.domain.Product;
import dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.feign.product_microservice.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockClient stockClient;

    public List<ProductDTO> findAll() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO save(ProductDTO dto) {
        // Convert DTO to Entity
        Product product = convertToEntity(dto);
        
        // Save to database
        Product savedProduct = productRepository.save(product);
        
        // Convert saved entity back to DTO for stock service
        ProductDTO savedProductDTO = convertToDTO(savedProduct);
        
        // Call stock microservice via Feign client
        stockClient.submitNewProduct(savedProductDTO);
        
        // Return saved product DTO
        return savedProductDTO;
    }

    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        // Set ID only if provided (for updates)
        if (dto.id() != null) {
            product.setId(dto.id());
        }
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setImageUrl(dto.imageURL());
        return product;
    }

    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl()
        );
    }
}

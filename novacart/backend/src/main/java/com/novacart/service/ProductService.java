package com.novacart.service;

import com.novacart.model.Product;
import com.novacart.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Business logic for product catalog management (CRUD + search/filter). */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    public List<Product> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> search(String keyword) {
        return productRepository.search(keyword);
    }

    public List<Product> filterProducts(String category, Double maxPrice, Double minRating) {
        return productRepository.filter(category, maxPrice, minRating);
    }
    

    public int addProduct(Product product) {
        return productRepository.save(product);
    }

    public void updateProduct(int id, Product product) {
        productRepository.update(id, product);
    }

    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }
}

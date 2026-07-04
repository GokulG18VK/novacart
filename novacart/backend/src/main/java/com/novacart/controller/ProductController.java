package com.novacart.controller;

import com.novacart.model.Product;
import com.novacart.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST endpoints for the product catalog — listing, search, filter and admin CRUD.
 * Base path: /api/products
 */
@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** GET /api/products?category=Fashion&maxPrice=5000&minRating=4 */
    @GetMapping
    public List<Product> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String search
    ) {
        if (search != null && !search.isEmpty()) return productService.search(search);
        if (category != null || maxPrice != null || minRating != null) {
            return productService.filterProducts(category, maxPrice, minRating);
        }
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable int id) {
        return productService.getProductById(id).orElse(null);
    }

    @PostMapping
    public Map<String, Object> addProduct(@RequestBody Product product) {
        int id = productService.addProduct(product);
        return Map.of("success", true, "id", id);
    }

    @PutMapping("/{id}")
public Map<String, Object> updateProduct(@PathVariable int id, @RequestBody Product product) {
    System.out.println("UPDATE PRODUCT ID: " + id);
    System.out.println("RATING RECEIVED: " + product.getRating());

    productService.updateProduct(id, product);
    return Map.of("success", true, "message", "Product updated");
}

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return Map.of("success", true, "message", "Product deleted");
    }
}

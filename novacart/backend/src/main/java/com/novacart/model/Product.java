package com.novacart.model;

import java.math.BigDecimal;

/** Represents a row in the `products` table. */
public class Product {
    private int id;
    private String name;
    private Integer categoryId;
    private String categoryName; // populated via JOIN for convenience
    private String description;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private String imageUrl;
    private BigDecimal rating;
    private int reviewsCount;
    private String badge;
    private int stock;

    public Product() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOldPrice() { return oldPrice; }
    public void setOldPrice(BigDecimal oldPrice) { this.oldPrice = oldPrice; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public int getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(int reviewsCount) { this.reviewsCount = reviewsCount; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}

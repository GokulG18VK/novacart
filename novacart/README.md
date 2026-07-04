# NovaCart — Premium E-Commerce Platform

A complete full-stack e-commerce website: **HTML5 / CSS3 / JavaScript** frontend with a **black, white & gold premium theme**, and a **Java Spring Boot + JDBC + MySQL** backend exposing REST APIs.

```
novacart/
├── frontend/                  ← Static site (open directly or serve with Live Server)
│   ├── index.html
│   ├── products.html
│   ├── product-details.html
│   ├── cart.html
│   ├── wishlist.html
│   ├── checkout.html
│   ├── order-success.html
│   ├── orders.html
│   ├── login.html
│   ├── register.html
│   ├── admin.html
│   ├── css/style.css
│   └── js/script.js
│
└── backend/                   ← Spring Boot REST API (Java + JDBC + MySQL)
    ├── pom.xml
    └── src/main/
        ├── java/com/novacart/
        │   ├── NovaCartApplication.java
        │   ├── config/WebConfig.java          (CORS)
        │   ├── model/                         (User, Product, Category, CartItem, WishlistItem, Order, OrderItem)
        │   ├── repository/                    (JdbcTemplate / PreparedStatement data access)
        │   ├── service/                       (business logic)
        │   └── controller/                    (REST endpoints)
        └── resources/
            ├── application.properties
            └── schema.sql                     (creates novacart_db tables + seed data)
```

---

## 1. Frontend — How It Works

The frontend is **pure HTML/CSS/JS** — no build step required. It ships with demo product data (`DEMO_PRODUCTS` in `js/script.js`) so the **entire UI works standalone**, even before the backend is running. Every action (login, register, add product, place order) first **tries the real REST API** at `http://localhost:8080/api`, and gracefully falls back to local demo/localStorage behavior if the backend isn't reachable — so you can demo the UI immediately and wire up the database afterward.

### Run the frontend
- **Easiest:** double-click `frontend/index.html` to open in a browser, or
- **Recommended:** in VS Code, install the "Live Server" extension, right-click `index.html` → "Open with Live Server" (avoids any local file CORS quirks).

---

## 2. MySQL Database Setup

1. Install MySQL Server (8.x recommended) and make sure it's running.
2. Open a MySQL client (Workbench, CLI, or DBeaver) and run:
   ```sql
   CREATE DATABASE IF NOT EXISTS novacart_db;
   ```
3. You do **not** need to run `schema.sql` manually — Spring Boot will auto-execute it on startup (see `application.properties`: `spring.sql.init.mode=always`). It creates all 7 tables (`users`, `categories`, `products`, `cart`, `wishlist`, `orders`, `order_items`) and inserts seed categories, 8 sample products, and a default admin user.
4. **Default Admin Login** (seeded automatically):
   - Email: `admin@novacart.com`
   - Password: `admin123`

   *(If you'd rather run schema.sql manually, just execute the file directly in your MySQL client.)*

---

## 3. JDBC Connection Setup

Edit `backend/src/main/resources/application.properties` to match your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/novacart_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

The backend uses **Spring's `JdbcTemplate`** (built on `PreparedStatement`) throughout — see any class under `repository/` — so all queries are parameterized and safe from SQL injection. No JPA/Hibernate is used, per the project requirements.

---

## 4. Running the Spring Boot Backend

### Option A — VS Code
1. Open the `backend/` folder in VS Code.
2. Install the **Extension Pack for Java** and **Spring Boot Extension Pack** (from the Extensions marketplace).
3. Open `NovaCartApplication.java`.
4. Click **Run** above the `main()` method (or press `F5`).
5. Console should print:
   ```
   =========================================
    NovaCart Backend running on port 8080
    API Base URL: http://localhost:8080/api
   =========================================
   ```

### Option B — Command line (Maven)
```bash
cd backend
mvn spring-boot:run
```

### Option C — Build a runnable JAR
```bash
cd backend
mvn clean package
java -jar target/novacart-backend.jar
```

> **Requires:** JDK 17+ and Maven 3.6+. If `mvn` isn't installed, you can also just open the folder in **IntelliJ IDEA** (Community Edition works fine) — it will auto-detect the `pom.xml` and download dependencies for you.

---

## 5. Connecting Frontend ↔ Backend

`frontend/js/script.js` has the API base URL at the top:
```js
const API_BASE = "http://localhost:8080/api";
```
Once the backend is running on port 8080, simply reload the frontend pages — `index.html`, `products.html`, and `admin.html` will automatically start pulling real data from MySQL instead of the demo arrays, and login/register/checkout/admin CRUD actions will persist to the database.

---

## 6. REST API Reference

| Method | Endpoint                              | Description                          |
|--------|----------------------------------------|---------------------------------------|
| GET    | `/api/products`                        | List/search/filter products (`?category=`, `?maxPrice=`, `?minRating=`, `?search=`) |
| GET    | `/api/products/{id}`                   | Get single product                    |
| POST   | `/api/products`                        | Add product (admin)                   |
| PUT    | `/api/products/{id}`                   | Edit product (admin)                  |
| DELETE | `/api/products/{id}`                   | Delete product (admin)                |
| GET    | `/api/categories`                      | List all categories                   |
| POST   | `/api/users/register`                  | Register new user                     |
| POST   | `/api/users/login`                     | Login                                 |
| GET    | `/api/users`                           | List users (admin)                    |
| DELETE | `/api/users/{id}`                      | Delete user (admin)                   |
| GET    | `/api/cart/{userId}`                   | Get user's cart                       |
| POST   | `/api/cart`                            | Add to cart `{userId, productId, quantity}` |
| PUT    | `/api/cart`                            | Update quantity                       |
| DELETE | `/api/cart/{userId}/{productId}`       | Remove item                           |
| GET    | `/api/wishlist/{userId}`               | Get user's wishlist                   |
| POST   | `/api/wishlist`                        | Add to wishlist `{userId, productId}` |
| DELETE | `/api/wishlist/{userId}/{productId}`   | Remove item                           |
| POST   | `/api/orders`                          | Place an order (checkout)             |
| GET    | `/api/orders`                          | All orders (admin)                    |
| GET    | `/api/orders/user/{userId}`            | A user's order history                |
| PUT    | `/api/orders/{orderId}/status`         | Update order status (admin)           |

---

## 7. API Testing Instructions

Use **Postman**, **Insomnia**, or `curl`. Examples:

**Register a user**
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","phone":"9876543210","password":"pass123"}'
```

**Login**
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@novacart.com","password":"admin123"}'
```

**Get all products**
```bash
curl http://localhost:8080/api/products
```

**Filter products**
```bash
curl "http://localhost:8080/api/products?category=Electronics&maxPrice=10000&minRating=4"
```

**Add a product (admin)**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","categoryId":1,"price":999,"imageUrl":"https://example.com/img.jpg","stock":50}'
```

**Place an order**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"total":7999,"paymentMethod":"card","items":[{"id":2,"name":"Wireless Headphones","price":7999,"qty":1}],"address":{"fullName":"John Doe","phone":"9876543210","address":"123 Main St","city":"Mumbai","state":"MH","pincode":"400001"}}'
```

---

## 8. Troubleshooting

- **CORS errors in browser console** → confirm the backend is running and `WebConfig.java` is present (it allows all origins on `/api/**`).
- **`Communications link failure`** → MySQL isn't running, or wrong host/port in `application.properties`.
- **`Unknown database 'novacart_db'`** → run `CREATE DATABASE novacart_db;` manually first.
- **Port 8080 already in use** → change `server.port` in `application.properties`.
- **Frontend shows demo data even with backend running** → check the browser console for fetch errors; verify `API_BASE` matches your backend URL/port.

---

## 9. Tech Stack Summary

- **Frontend:** HTML5, CSS3 (custom design system, glassmorphism, gradients, animations), Vanilla JavaScript (Fetch API, localStorage for demo/offline mode)
- **Backend:** Java 17, Spring Boot 3.2, Spring Web (REST), Spring JDBC (`JdbcTemplate`/`PreparedStatement` — no ORM)
- **Database:** MySQL 8 (`novacart_db`) — 7 tables: `users`, `categories`, `products`, `cart`, `wishlist`, `orders`, `order_items`

Built as a professional, portfolio-ready full-stack project. 🚀

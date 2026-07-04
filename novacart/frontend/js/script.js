/* ===========================================================
   NovaCart — script.js
   Handles: API calls, Cart, Wishlist, Auth, Animations, Search
   Backend base URL can be changed once Spring Boot is running.
   =========================================================== */

const API_BASE = window.location.hostname === "localhost"
    ? "http://localhost:8080/api"
    : "https://novacart-moz6.onrender.com/api";

/* ---------- DEMO PRODUCT DATA (fallback if backend is offline) ---------- */
/* In production these come from GET /api/products (MySQL via JdbcTemplate) */
const DEMO_PRODUCTS = [
  {id:1, name:"Classic Leather Jacket", category:"Fashion", price:4999, oldPrice:6999, rating:4.5, reviews:128, image:"https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500", badge:"NEW"},
  {id:2, name:"Wireless Noise-Cancel Headphones", category:"Electronics", price:7999, oldPrice:9999, rating:4.7, reviews:340, image:"https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500", badge:"-20%"},
  {id:3, name:"Minimalist Table Lamp", category:"Home & Living", price:1899, oldPrice:2499, rating:4.3, reviews:76, image:"https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=500"},
  {id:4, name:"Rose Gold Perfume Set", category:"Beauty & Personal Care", price:2599, oldPrice:3199, rating:4.6, reviews:210, image:"https://images.unsplash.com/photo-1541643600914-78b084683601?w=500", badge:"NEW"},
  {id:5, name:"Pro Yoga Mat", category:"Sports & Fitness", price:1299, oldPrice:1799, rating:4.4, reviews:98, image:"https://images.unsplash.com/photo-1592432678016-e910b452f9a2?w=500"},
  {id:6, name:"Organic Almonds 1kg", category:"Groceries", price:899, oldPrice:1099, rating:4.2, reviews:55, image:"https://images.unsplash.com/photo-1508061253366-f7da158b6d46?w=500"},
  {id:7, name:"Gold Chronograph Watch", category:"Watches & Accessories", price:12999, oldPrice:15999, rating:4.8, reviews:412, image:"https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=500", badge:"BESTSELLER"},
  {id:8, name:"RGB Mechanical Keyboard", category:"Gaming", price:5499, oldPrice:6999, rating:4.6, reviews:230, image:"https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500"},
  {id:9, name:"Premium Denim Jeans", category:"Fashion", price:2299, oldPrice:2899, rating:4.3, reviews:88, image:"https://images.unsplash.com/photo-1542272604-787c3835535d?w=500"},
  {id:10, name:"4K Smart LED TV 55\"", category:"Electronics", price:42999, oldPrice:49999, rating:4.7, reviews:521, image:"https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=500", badge:"-15%"},
  {id:11, name:"Scandinavian Sofa Set", category:"Home & Living", price:34999, oldPrice:39999, rating:4.5, reviews:64, image:"https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=500"},
  {id:12, name:"Matte Lipstick Combo", category:"Beauty & Personal Care", price:799, oldPrice:999, rating:4.1, reviews:142, image:"https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=500"},
  {id:13, name:"Adjustable Dumbbells Set", category:"Sports & Fitness", price:6499, oldPrice:7999, rating:4.6, reviews:177, image:"https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=500"},
  {id:14, name:"Cold Pressed Olive Oil 1L", category:"Groceries", price:649, oldPrice:799, rating:4.4, reviews:69, image:"https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500"},
  {id:15, name:"Sterling Silver Bracelet", category:"Watches & Accessories", price:1999, oldPrice:2599, rating:4.5, reviews:101, image:"https://images.unsplash.com/photo-1611591437281-460bfbe1220a?w=500", badge:"NEW"},
  {id:16, name:"Pro Gaming Mouse", category:"Gaming", price:2199, oldPrice:2799, rating:4.7, reviews:289, image:"https://images.unsplash.com/photo-1527814050087-3793815479db?w=500"},
];

const CATEGORIES = [
  {id:1, name:"Fashion", image:"https://images.unsplash.com/photo-1445205170230-053b83016050?w=500", count:"320+ items"},
  {id:2, name:"Electronics", image:"https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500", count:"540+ items"},
  {id:3, name:"Home & Living", image:"https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=500", count:"260+ items"},
  {id:4, name:"Beauty & Personal Care", image:"https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=500", count:"180+ items"},
  {id:5, name:"Sports & Fitness", image:"https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=500", count:"210+ items"},
  {id:6, name:"Groceries", image:"https://images.unsplash.com/photo-1542838132-92c53300491e?w=500", count:"410+ items"},
  {id:7, name:"Watches & Accessories", image:"https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500", count:"150+ items"},
  {id:8, name:"Gaming", image:"https://images.unsplash.com/photo-1612287230202-1ff1d85d1bdf?w=500", count:"190+ items"},
];

/* ---------- API HELPER (tries backend, falls back to demo data) ---------- */
async function apiGet(endpoint, fallback) {
  try {
    const res = await fetch(`${API_BASE}${endpoint}`, { method: "GET" });
    if (!res.ok) throw new Error("API error");
    return await res.json();
  } catch (err) {
    console.warn(`Backend not reachable for ${endpoint}, using demo data.`, err.message);
    return fallback;
  }
}
async function apiPost(endpoint, body) {
  try {
    const res = await fetch(`${API_BASE}${endpoint}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    return await res.json();
  } catch (err) {
    console.warn(`Backend not reachable for POST ${endpoint}`, err.message);
    return null;
  }
}
async function apiPut(endpoint, body) {
  try {
    const res = await fetch(`${API_BASE}${endpoint}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    return await res.json();
  } catch (err) { return null; }
}
async function apiDelete(endpoint) {
  try {
    await fetch(`${API_BASE}${endpoint}`, { method: "DELETE" });
    return true;
  } catch (err) { return false; }
}

/* ---------- LOCAL STORAGE STATE (cart, wishlist, auth) ---------- */
function getCart(){ return JSON.parse(localStorage.getItem("novacart_cart") || "[]"); }
function setCart(cart){ localStorage.setItem("novacart_cart", JSON.stringify(cart)); updateCartBadge(); }
function getWishlist(){ return JSON.parse(localStorage.getItem("novacart_wishlist") || "[]"); }
function setWishlist(w){ localStorage.setItem("novacart_wishlist", JSON.stringify(w)); updateWishlistBadge(); }
function getUser(){ return JSON.parse(localStorage.getItem("novacart_user") || "null"); }
function setUser(u){ localStorage.setItem("novacart_user", JSON.stringify(u)); }
function logoutUser(){ localStorage.removeItem("novacart_user"); window.location.href="login.html"; }

async function addToCart(product, qty = 1){
  await apiPost("/cart", {
    userId: 1,
    productId: product.id,
    quantity: qty
  });

  showToast(`${product.name} added to cart`);
  updateCartBadge();
}
function removeFromCart(id){
  setCart(getCart().filter(i => i.id !== id));
}
async function updateCartQty(id, qty){
  await apiPut("/cart", {
    userId: 1,
    productId: id,
    quantity: qty
  });
}
function toggleWishlist(product){
  let w = getWishlist();
  const exists = w.find(i => i.id === product.id);
  if (exists) { w = w.filter(i => i.id !== product.id); showToast("Removed from wishlist"); }
  else { w.push(product); showToast("Added to wishlist"); }
  setWishlist(w);
  return !exists;
}
function isInWishlist(id){ return getWishlist().some(i => i.id === id); }
async function updateCartBadge(){
  const el = document.getElementById("cart-count");
  if (!el) return;

  const cart = await apiGet("/cart/1", []);
  el.textContent = cart.reduce((sum, item) => sum + item.quantity, 0);
}
function updateWishlistBadge(){
  const el = document.getElementById("wishlist-count");
  if (el) el.textContent = getWishlist().length;
}

/* ---------- TOAST ---------- */
function showToast(msg){
  let toast = document.getElementById("toast");
  if (!toast){
    toast = document.createElement("div");
    toast.id = "toast";
    toast.className = "toast";
    document.body.appendChild(toast);
  }
  toast.textContent = msg;
  toast.classList.add("show");
  clearTimeout(window._toastTimer);
  window._toastTimer = setTimeout(()=> toast.classList.remove("show"), 2500);
}

/* ---------- STAR RATING RENDER ---------- */
function renderStars(rating){
  const full = Math.floor(rating);
  const half = rating % 1 >= 0.5;
  let html = "★".repeat(full);
  if (half) html += "½";
  html += "☆".repeat(5 - full - (half?1:0));
  return html;
}

/* ---------- PRODUCT CARD TEMPLATE ---------- */
function productCardHTML(p){
  const wished = isInWishlist(p.id);
  return `
  <div class="product-card fade-up">
    <div class="product-img">
      ${p.badge ? `<span class="product-badge">${p.badge}</span>` : ""}
      <button class="product-wishlist ${wished ? 'active' : ''}" onclick="event.stopPropagation();handleWishlistClick(${p.id})">
        <i class="fa-${wished ? 'solid' : 'regular'} fa-heart"></i>
      </button>
      <a href="product-details.html?id=${p.id}">
        <img src="${p.image}" alt="${p.name}">
      </a>
    </div>

    <div class="product-info">
      <div class="product-cat">${p.category}</div>
      <a href="product-details.html?id=${p.id}">
        <h4 class="product-name">${p.name}</h4>
      </a>
      <div class="product-rating">${renderStars(p.rating)} <span>(${p.reviews})</span></div>

      <div class="product-price-row">
        <div class="price">
          ₹${Number(p.price).toLocaleString()}
          ${p.oldPrice ? `<span class="old">₹${Number(p.oldPrice).toLocaleString()}</span>` : ""}
        </div>
        <button class="add-cart-btn" onclick="event.stopPropagation();handleAddToCart(${p.id})">Add</button>
      </div>
    </div>
  </div>`;
}
async function handleAddToCart(id){
  const p = await apiGet(`/products/${id}`, null);

  if(p){
    await addToCart(p, 1);
    showToast("Product added to cart");
  }
}
async function handleWishlistClick(id){
  let p = await apiGet(`/products/${id}`, null);

  if (!p) {
    p = DEMO_PRODUCTS.find(x => x.id === id);
  }

  if (p) {
    const product = {
      id: p.id,
      name: p.name,
      category: p.category || p.categoryName,
      price: p.price,
      oldPrice: p.oldPrice,
      rating: p.rating,
      reviews: p.reviews || p.reviewsCount || 0,
      image: p.image || p.imageUrl,
      badge: p.badge
    };

    const added = toggleWishlist(product);
    const btn = event.currentTarget;
    btn.classList.toggle("active", added);
    btn.querySelector("i").className = `fa-${added ? 'solid' : 'regular'} fa-heart`;
  }
}
/* ---------- MOBILE NAV DRAWER ---------- */
function setupMobileNav(){
  const toggle = document.querySelector(".menu-toggle");
  const drawer = document.querySelector(".mobile-drawer");
  const overlay = document.querySelector(".drawer-overlay");
  if (!toggle || !drawer) return;
  toggle.addEventListener("click", ()=>{ drawer.classList.add("open"); overlay.classList.add("active"); });
  overlay.addEventListener("click", ()=>{ drawer.classList.remove("open"); overlay.classList.remove("active"); });
}

/* ---------- SCROLL ANIMATIONS ---------- */
function setupScrollAnimations(){
  const els = document.querySelectorAll(".fade-up");
  const obs = new IntersectionObserver((entries)=>{
    entries.forEach(e=>{ if (e.isIntersecting) e.target.classList.add("visible"); });
  }, {threshold:0.1});
  els.forEach(el=>obs.observe(el));
}

/* ---------- NAVBAR AUTH STATE ---------- */
function setupAuthNav() {
    const loginLink = document.getElementById("nav-login");
    if (!loginLink) return;

    const user = getUser();
    const span = loginLink.querySelector("span");

    if (user) {
        loginLink.href = "login.html";
        loginLink.title = `Hi, ${user.name}`;

        // icon
        loginLink.querySelector("i").className = "fa-solid fa-user";

        // span இருந்தா மட்டும் update பண்ணு
        if (span) {
            span.textContent = "Profile";
        }
    } else {
        loginLink.href = "login.html";
        loginLink.querySelector("i").className = "fa-regular fa-user";

        if (span) {
            span.textContent = "Login";
        }
    }
}
/* ---------- SEARCH ---------- */
function setupSearch(){
  const inputs = document.querySelectorAll(".search-box input");
  inputs.forEach(input=>{
    input.addEventListener("keydown", (e)=>{
      if (e.key === "Enter"){
        const q = e.target.value.trim();
        if (q) window.location.href = `products.html?search=${encodeURIComponent(q)}`;
      }
    });
  });
}

/* ---------- INIT (runs on every page) ---------- */
document.addEventListener("DOMContentLoaded", ()=>{
  updateCartBadge();
  updateWishlistBadge();
  setupMobileNav();
  setupScrollAnimations();
  setupAuthNav();
  setupSearch();
});

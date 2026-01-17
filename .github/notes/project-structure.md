# Project Structure


``` yaml
verdedemas/
├── src/
│   ├── main/
│   │   ├── java/com/verdedemas/
│   │   │   ├── VerdeDeMasApplication.java
│   │   │   ├── config/
│   │   │   │   ├── WebConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── WebSocketConfig.java
│   │   │   │   └── JwtConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── CartController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── AddressController.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── ProductService.java
│   │   │   │   ├── CartService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── AddressService.java
│   │   │   │   └── EmailService.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── CartRepository.java
│   │   │   │   ├── CartItemRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── OrderItemRepository.java
│   │   │   │   ├── AddressRepository.java
│   │   │   │   └── ReviewRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── Cart.java
│   │   │   │   ├── CartItem.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── Address.java
│   │   │   │   ├── Review.java
│   │   │   │   └── BaseEntity.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── CreateProductRequest.java
│   │   │   │   │   ├── CreateOrderRequest.java
│   │   │   │   │   └── AddAddressRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── LoginResponse.java
│   │   │   │       ├── ProductResponse.java
│   │   │   │       ├── OrderResponse.java
│   │   │   │       ├── CartResponse.java
│   │   │   │       └── ErrorResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── InvalidCredentialsException.java
│   │   │   │   └── InsufficientStockException.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── SecurityUtil.java
│   │   │   ├── websocket/
│   │   │   │   ├── OrderTrackingController.java
│   │   │   │   ├── NotificationService.java
│   │   │   │   └── OrderStatusMessage.java
│   │   │   └── util/
│   │   │       ├── Constants.java
│   │   │       └── DateUtil.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__init_schema.sql
│   │               └── V2__add_sample_categories.sql
│   └── test/
│       └── java/com/verdedemas/
│           └── service/
├── pom.xml
├── .gitignore
├── .gitattributes
└── README.md
```
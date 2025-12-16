# Fertilizer Shop Application

A comprehensive Spring Boot-based fertilizer e-commerce application with admin panel and customer portal.

## Features

### Admin Panel
- Admin authentication and role-based access
- Product management (CRUD operations)
- Customer management
- Order management with status tracking
- Admin dashboard with statistics

### Customer Portal
- User registration and authentication
- Product browsing with search and filter
- Shopping cart functionality
- Order placement and tracking
- Profile management

## Tech Stack

### Backend
- Spring Boot 3
- Java 17
- Spring Web (MVC)
- Spring Data JPA
- Spring Security
- Spring Validation
- Maven

### Frontend
- Thymeleaf templates
- Bootstrap 5 (CDN)
- HTML5, CSS3

### Database
- MySQL (recommended)
- H2 (for development/demo)

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── fertilizer/
│   │           └── shop/
│   │               ├── FertilizerShopApplication.java
│   │               ├── config/
│   │               │   └── SecurityConfig.java
│   │               ├── controller/
│   │               │   ├── AdminController.java
│   │               │   ├── CustomerController.java
│   │               │   ├── AuthController.java
│   │               │   └── CartController.java
│   │               ├── model/
│   │               │   ├── User.java
│   │               │   ├── Product.java
│   │               │   ├── Order.java
│   │               │   ├── OrderItem.java
│   │               │   └── dto/
│   │               │       ├── UserDto.java
│   │               │       ├── ProductDto.java
│   │               │       └── OrderDto.java
│   │               ├── repository/
│   │               │   ├── UserRepository.java
│   │               │   ├── ProductRepository.java
│   │               │   ├── OrderRepository.java
│   │               │   └── OrderItemRepository.java
│   │               └── service/
│   │                   ├── UserService.java
│   │                   ├── ProductService.java
│   │                   ├── OrderService.java
│   │                   └── CartService.java
│   └── resources/
│       ├── templates/
│       │   ├── admin/
│       │   │   ├── admin-dashboard.html
│       │   │   ├── admin-login.html
│       │   │   ├── admin-products.html
│       │   │   ├── admin-product-form.html
│       │   │   ├── admin-customers.html
│       │   │   └── admin-orders.html
│       │   ├── customer/
│       │   │   ├── index.html
│       │   │   ├── login.html
│       │   │   ├── register.html
│       │   │   ├── product-list.html
│       │   │   ├── product-detail.html
│       │   │   ├── cart.html
│       │   │   ├── checkout.html
│       │   │   ├── customer-orders.html
│       │   │   └── profile.html
│       │   └── layout/
│       │       └── base.html
│       ├── static/
│       │   ├── css/
│       │   │   └── style.css
│       │   ├── js/
│       │   │   └── app.js
│       │   └── images/
│       └── application.properties
```

## Database Design

### Entities Relationship

```
User (1) ---- (*) Order (1) ---- (*) OrderItem (*) ---- (1) Product
 |                                          
 |--- role: ADMIN/CUSTOMER                   
 |--- email (unique)                         
 |--- name, phone, address                   
                                            
Product                                       
 |--- name, description                      
 |--- price, stock                           
 |--- category, brand, weight                
 |--- imageUrl, isActive                     
                                            
Order                                         
 |--- totalAmount, status                    
 |--- shippingAddress                        
 |--- createdAt, updatedAt                   
                                            
OrderItem                                     
 |--- quantity, priceAtPurchase              
```

## Quick Start

1. Clone the repository
2. Configure database in `application.properties`
3. Run the application: `mvn spring-boot:run`
4. Access the application at `http://localhost:8080`

## Default Admin User

- Email: admin@fertilizer.com
- Password: admin123

## Database Configuration

### MySQL Setup
1. Install MySQL server
2. Create database: `CREATE DATABASE fertilizer_shop;`
3. Update `application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/fertilizer_shop
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### H2 Database (Development)
For quick testing, use the H2 in-memory database by uncommenting these lines in `application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

## Running the Application

### Using Maven
```bash
mvn spring-boot:run
```

### Using Executable JAR
```bash
mvn clean package
java -jar target/fertilizer-shop-1.0.0.jar
```

## Features Overview

### Admin Panel Features
- **Dashboard**: View statistics including total products, customers, and orders
- **Product Management**: Add, edit, delete, and manage fertilizer products
- **Customer Management**: View all registered customers and their details
- **Order Management**: View all orders, update order status (Pending → Confirmed → Shipped → Delivered)
- **Inventory Control**: Manage stock levels for all products

### Customer Portal Features
- **Product Browsing**: Browse all available fertilizers with search and filter options
- **Product Categories**: NPK, Organic, Bio, Micro-nutrients, and Specialty fertilizers
- **Shopping Cart**: Add products to cart, update quantities, remove items
- **Order Placement**: Place orders with shipping address and contact information
- **Order Tracking**: View order history and track current order status
- **Profile Management**: View and update personal information

## Security Features

- **Role-Based Access Control**: Separate roles for ADMIN and CUSTOMER
- **Password Hashing**: BCrypt encryption for secure password storage
- **Spring Security**: Comprehensive security configuration with form-based login
- **CSRF Protection**: Built-in CSRF token protection for forms

## Technology Stack Details

### Backend Technologies
- **Spring Boot 3**: Framework for rapid application development
- **Java 17**: Modern Java features and performance improvements
- **Spring Data JPA**: Database abstraction and ORM functionality
- **Spring Security**: Authentication and authorization framework
- **Spring Validation**: Bean validation with annotations
- **MySQL/H2**: Relational database support

### Frontend Technologies
- **Thymeleaf**: Server-side template engine
- **Bootstrap 5**: Responsive UI framework
- **Font Awesome**: Icon library
- **Vanilla JavaScript**: Client-side interactivity

## Database Schema

The application uses the following main entities:

### Users Table
- id (Primary Key)
- name, email, password, phone, address
- role (ADMIN/CUSTOMER)
- created_at

### Products Table
- id (Primary Key)
- name, description, image_url, price, stock
- category, brand, weight
- is_active, created_at

### Orders Table
- id (Primary Key)
- customer_id (Foreign Key to Users)
- total_amount, status, shipping_address
- created_at, updated_at

### Order Items Table
- id (Primary Key)
- order_id (Foreign Key to Orders)
- product_id (Foreign Key to Products)
- quantity, price_at_purchase

## API Endpoints

### Public Endpoints
- `GET /` - Home page with product listing
- `GET /products` - Product listing page
- `GET /auth/login` - Customer login page
- `GET /auth/register` - User registration page
- `GET /auth/access-denied` - Access denied page

### Customer Endpoints
- `GET /cart` - View shopping cart
- `POST /cart/add` - Add product to cart
- `POST /cart/update` - Update cart item quantity
- `GET /cart/remove/{productId}` - Remove item from cart
- `GET /cart/checkout` - Checkout page
- `POST /cart/checkout` - Process order
- `GET /customer/dashboard` - Customer dashboard
- `GET /customer/profile` - View/edit profile
- `POST /customer/profile` - Update profile
- `GET /customer/orders` - View order history
- `GET /customer/orders/{orderId}` - View order details
- `POST /customer/orders/{orderId}/cancel` - Cancel order

### Admin Endpoints
- `GET /admin/dashboard` - Admin dashboard with statistics
- `GET /admin/products` - Manage products (list)
- `GET /admin/products/add` - Add new product form
- `GET /admin/products/edit/{id}` - Edit product form
- `POST /admin/products/save` - Save product (create/update)
- `GET /admin/products/delete/{id}` - Soft delete product
- `GET /admin/customers` - View all customers
- `GET /admin/customers/{customerId}/orders` - View customer's orders
- `GET /admin/orders` - Manage all orders
- `GET /admin/orders/{orderId}` - View order details
- `POST /admin/orders/{orderId}/status` - Update order status

## Testing the Application

### Test Data
The application includes sample data in `data.sql`:
- 1 admin user (admin@fertilizer.com)
- 3 sample customers
- 15 fertilizer products across different categories
- 5 sample orders with various statuses

### Testing Scenarios

#### As Admin
1. Login with admin@fertilizer.com / admin123
2. View dashboard statistics
3. Add/edit/delete products
4. View all customers and their orders
5. Update order statuses
6. Monitor inventory levels

#### As Customer
1. Register a new account or login with test accounts
2. Browse products by category
3. Search for specific products
4. Add items to cart
5. Proceed to checkout
6. View order history
7. Track order status

## Customization Options

### Adding New Product Categories
1. Update the `category` field in products table
2. Modify category filter logic in `ProductService`

### Customizing Order Statuses
1. Update `Order.Status` enum in the Order entity
2. Update status handling logic in controllers and templates

### Adding Payment Integration
The current application has a basic checkout process. To add payment integration:
1. Add payment gateway dependencies (Stripe, PayPal, etc.)
2. Create payment service and repository
3. Integrate payment processing in the checkout flow

## Security Considerations

- **Password Policy**: Enforce strong password requirements
- **Input Validation**: Validate all user inputs to prevent injection attacks
- **CSRF Protection**: Ensure all forms include CSRF tokens
- **Session Management**: Configure session timeout and management
- **HTTPS**: Use HTTPS in production environment

## Deployment

### Prerequisites
- Java 17 or higher
- MySQL database (or H2 for testing)
- Maven 3.6 or higher

### Production Deployment
1. Create production database
2. Update `application.properties` for production settings
3. Build JAR: `mvn clean package -DskipTests`
4. Run application: `java -jar fertilizer-shop-1.0.0.jar`

### Environment Variables
Consider using environment variables for sensitive configuration:
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/fertilizer_prod
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=prod_password
```

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check database server is running
   - Verify connection URL, username, and password
   - Ensure database exists

2. **Application Won't Start**
   - Check Java version (must be 17+)
   - Verify Maven dependencies are downloaded
   - Check for port conflicts (default: 8080)

3. **Login Issues**
   - Ensure user exists in database
   - Check password is correctly hashed
   - Verify role assignments

### Logging
Enable debug logging by adding to `application.properties`:
```properties
logging.level.com.fertilizer=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Email: support@fertilizer.com
- Documentation: [Project Wiki](https://github.com/yourusername/fertilizer-shop/wiki)

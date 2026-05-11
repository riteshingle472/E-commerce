🛒 Ecommerce — Multi-Role Spring Boot Platform

This project is a full-featured e-commerce backend application developed using Spring Boot and Java 21. It is designed around three different user roles — Customer, Seller, and Admin — where each role has its own responsibilities and permissions inside the platform.

The main purpose of this project is to build a real-world backend system that follows clean architecture, secure authentication practices, role-based authorization, and scalable API design.

The application includes important production-level features such as JWT authentication, OTP-based password recovery, Razorpay payment integration, product and inventory management, order handling, and secure REST APIs.

🚀 What Users Can Do
👤 Customer Module

Customers can create accounts, login securely using JWT authentication, browse available products, search products, add items to their cart or wishlist, and place orders online.

Customers can track their order history, give product ratings and reviews, and reset forgotten passwords using OTP verification through email.

🏪 Seller Module

Sellers can register themselves on the platform and manage their own products.

They can add new products, update product information, remove products, manage inventory stock, and update order statuses. Newly added products go through an approval process handled by the Admin before becoming visible to customers.

🔐 Admin Module

The Admin manages the overall platform operations.

Admins can approve or reject seller accounts, approve or reject products submitted by sellers, monitor inventory activity, and handle pending approval requests to maintain platform quality and security.

🧰 Technologies Used

The backend is built using:

Spring Boot 3
Java 21
MySQL Database
Spring Data JPA & Hibernate
Spring Security
JWT Authentication
Razorpay Payment Gateway
Spring Mail (SMTP)
Maven Build Tool
BCrypt Password Encryption

The project follows a RESTful API architecture and is structured in a modular way for better scalability and maintainability.

📁 Project Structure

The project is organized into multiple layers such as controllers, services, repositories, DTOs, security configuration, and utility classes to keep the code clean and manageable.

ecommerce/
├── Configuration/
├── Controller/
├── DTO/
├── Entity/
├── Repository/
├── Security/
├── Service/
├── Specification/
└── Utils/

Each layer has a separate responsibility, which makes the project easier to scale and maintain.

⚙️ Prerequisites

Before running the project, make sure the following tools are installed on your system:

Java 21+
Maven
MySQL
Git
IntelliJ IDEA or VS Code

You will also need:

A Razorpay account for payment integration
An SMTP email service for OTP functionality
🔧 Environment Variables

Sensitive information such as database credentials, JWT secrets, email passwords, and Razorpay keys are stored using environment variables instead of hardcoding them directly into the project.

Example:

SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=

MAIL_USERNAME=
MAIL_PASSWORD=

RAZORPAY_KEY_ID=
RAZORPAY_KEY_SECRET=

This approach improves security and allows other developers to run the project without exposing private credentials.

▶️ Running the Project

First, clone the repository:

git clone https://github.com/your-username/E-commerce.git

Move into the project folder:

cd E-commerce

Build the project using Maven:

mvn clean install

Run the Spring Boot application:

mvn spring-boot:run

The server will start on:

http://localhost:8080
🐳 Docker Support

The project also supports Docker, making deployment easier and more portable.

To build the Docker image:

docker build -t ecommerce-app .

To run the container:

docker run -p 8080:8080 ecommerce-app

You can also use Docker Compose for easier container management.

🔐 Authentication Flow

The authentication system works using JWT tokens.

Users register themselves
Users log in using email and password
Backend generates a JWT token
The token is sent in API headers for protected requests

For password recovery:

User requests password reset
Backend sends OTP to email
OTP verification is completed
User can set a new password securely

💳 Payment Integration

The project uses Razorpay for online payments.
When a customer places an order:
Backend creates a Razorpay order

🛡️ Security Features

Several security practices are implemented in the project, including:

BCrypt password hashing
JWT-based authentication
Stateless session management
Role-based access control
Secure API endpoints
OTP expiration handling
Razorpay signature verification

These features help make the application closer to a production-level backend system.

🗃️ Database Design

The application uses MySQL with Hibernate ORM for database management.

Main database tables include:

users
products
orders
cart
wishlist
reviews
otp_tokens

The database structure is designed to maintain proper relationships between users, products, orders, and payments.

👨‍💻 About the Project

This project was built to improve backend engineering skills and understand how real-world e-commerce systems are designed using Spring Boot.

It focuses on writing clean code, implementing secure authentication, handling payments, designing scalable APIs, and following industry-standard backend development practices.

Author

Ritesh Ingle
Backend Developer focused on Spring Boot, Microservices, REST APIs, Security, and Production-Level Backend Engineering.

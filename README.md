<img width="1280" height="486" alt="image" src="https://github.com/user-attachments/assets/8a9b6c41-1d9f-4793-8a2d-d055a0181160" />

While you are doing online shopping, you don't even think how this entire system works! We've developed Backend technology for people got interested in it after reading the first sentence😊.

Let's explore online shopping with the help of books together! Here you will find API for managing possible activity on online bookstore website.

## 📁 Stack
- **Backend**: Java, Spring Boot, Hibernate, Maven
- **Database**: MySQL
- **Migrations**: Liquibase
- **Security:** JWT
- **Deployment**: AWS

## 💡 Features
- ACID, CRUD and SOLID adherence
- Role allocation (ADMIN as manager & USER as a shopper) 🔐
- Soft deletion in almost all entities for data consistency
- Docker and Swagger usage
- Testing ![Line Coverage](https://img.shields.io/badge/Coverage-95%25-brightgreen)
  
<img width="500" height="267" alt="Знімок екрана 2026-04-18 115638" src="https://github.com/user-attachments/assets/0377e6b3-afc2-4970-8934-5ca24f1544fd" />

- CI/CD checkstyle pipeline

<img width="500" height="86" alt="image" src="https://github.com/user-attachments/assets/f03e700b-7e27-4a7c-953b-c416577b4475" />

## ⚡ Program opportunities
#### Things Shoppers Can Do
1. Join and sign in:
   - join the store
   - sign in to look at books and buy them
2. Look at and search for books:
   - look at all the books
   - look closely at one book
   - find a book by typing its name
3. Look at bookshelf sections:
   - see all bookshelf sections
   - see all books in one section
4. Use the basket:
   - put a book in the basket
   - look inside the basket
   - take a book out of the basket
5. Buying books:
   - buy all the books in the basket
   - look at past receipts
6. Look at receipts:
   - see all books on one receipt
   - look closely at one book on a receipt

#### Things Managers Can Do
1. Arrange books:
   - add a new book to the store
   - change details of a book
   - remove a book from the store
2. Organize bookshelf sections:
   - make a new bookshelf section
   - change details of a section
   - remove a section
3. Look at and change receipts:
   - change the status of a receipt

## 📌 API endpoints
#### 🔑 Authentication Controller 🔑
- `POST` */api/auth/register* — User registration
- `POST` */api/auth/login* — User authentication

#### 📖 Book Controller 📖
- `POST` */api/books* — Create a new book (for ADMIN only)
- `GET` */api/books/{id}* — Retrieve book information (for USER only)
- `GET` */api/books* — Retrieve books (for USER only)
- `GET` */api/books/search* — Search books (for USER only)
- `PUT` */api/books/{id}* — Update book (for ADMIN only)
- `DELETE` */api/books/{id}* — Delete book (for ADMIN only)

#### ⭐ Category Controller ⭐
- `POST` */api/categories* — Create a new category (for ADMIN only)
- `GET` */api/categories/{id}* — Retrieve category information (for USER only)
- `GET` */api/categories* — Retrieve categories (for USER only)
- `GET` */api/categories/{id}/books* — Retrieve books in category (for USER only)
- `PUT` */api/categories/{id}* — Update category (for ADMIN only)
- `DELETE` */api/categories/{id}* — Delete category (for ADMIN only)

#### 🛒 Shopping Cart Controller (for USER only) 🛒
- `POST` */api/cart* — Add book to shopping cart
- `GET` */api/cart* — Retrieve my shopping cart information
- `PUT` */api/cart/items/{cartItemId}* — Update book quantity in shopping cart
- `DELETE` */api/cart/items/{cartItemId}* — Remove book from shopping cart

#### 🧾 Order Controller 🧾
- `POST` */api/orders* — Create a new order (for USER only)
- `GET` */api/orders* — Retrieve orders (for USER only)
- `GET` */api/orders/{id}/items* — Retrieve items in order (for USER only)
- `GET` */api/orders/{id}/items/{itemId}* — Retrieve orders (for USER only)
- `PATCH` */api/orders/{id}* — Update order status (for ADMIN only)

## 📦 Setup
#### Docker configuration
1. Copy `.env.sample` file to new `.env` file
2. Fill your `.env` file with required environment variables
3. Run Docker application

#### 🔎 Build your project with `mvn clean package` and use `mvn clean verify` for CI check

## 🧠 Development сhallenges
- **Security setup**: do not forget to use your `.env` variables appropriately in order to avoid critical situations and information leakage
- **Migrations**: do not change root files while working on code, the best practice is to add new files with changing migration rules such as `ALTER table`
- **Tests integration**: it requires you to be attentive while changing something in code since you can ruin both unit and integrative tests
- **AWS deployment**: consider your repository, IAM user, RDS database, ECR, EC2 instance and security variables are corresponding in order to publish and update project effectively

## 🚀 Demo & Documentation API
The application has been deployed through AWS.

Swagger documentation is available at http://3.82.41.71/api/swagger-ui/index.html

## 🎥 Full Demo Video

See here 

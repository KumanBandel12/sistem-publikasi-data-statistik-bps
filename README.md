# 📊 Sistem Publikasi Data Statistik BPS

REST API untuk Manajemen Publikasi Data Statistik Badan Pusat Statistik (BPS)

---

## 📋 Deskripsi Project
Sistem Publikasi Data Statistik BPS adalah aplikasi web service berbasis **REST API** yang dibangun untuk mengelola publikasi data statistik yang dihasilkan oleh **Badan Pusat Statistik (BPS)**.  
Sistem ini memungkinkan admin BPS untuk mengunggah, mengelola, dan mendistribusikan publikasi statistik, sementara pengguna umum dapat mengakses, mencari, dan mengunduh publikasi yang tersedia.

### ✨ Fitur Utama
- ✅ **Manajemen User** – Register, login, profile management dengan JWT authentication
- ✅ **Manajemen Kategori** – CRUD kategori publikasi *(Admin only)*
- ✅ **Manajemen Publikasi** – Upload, update, delete publikasi dengan file PDF/Excel
- ✅ **Pencarian & Filter** – Search by keyword, filter by category, year
- ✅ **Download Tracking** – Monitoring views dan downloads publikasi
- ✅ **Role-Based Access Control** – Akses berbeda untuk USER dan ADMIN
- ✅ **File Management** – Upload dan download file publikasi secara aman
- ✅ **API Documentation** – Swagger UI untuk dokumentasi interaktif

---

## 🛠️ Tech Stack

**Backend Framework:**
- Java 17+
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- JWT (JSON Web Token)
- Hibernate

**Database:**
- MySQL 8.0

**Documentation:**
- SpringDoc OpenAPI 3 (Swagger)

**Build Tool:**
- Maven

**Additional Libraries:**
- Lombok
- Validation API
- JJWT

---

## 📂 Struktur Project

```
publikasi-statistik/
├── src/
│   ├── main/
│   │   ├── java/com/bps/publikasistatistik/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── OpenAPIConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── PublicationController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── ApiResponse.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── CategoryRequest.java
│   │   │   │   ├── CategoryResponse.java
│   │   │   │   └── ... (11 files total)
│   │   │   ├── entity/              # JPA Entities
│   │   │   │   ├── Category.java
│   │   │   │   ├── Publication.java
│   │   │   │   └── User.java
│   │   │   ├── repository/          # JPA Repositories
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── PublicationRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/            # Security components
│   │   │   │   ├── CustomUserDetails.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── JwtUtil.java
│   │   │   ├── service/             # Business logic
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── CategoryService.java
│   │   │   │   ├── FileStorageService.java
│   │   │   │   ├── PublicationService.java
│   │   │   │   └── UserService.java
│   │   │   └── PublikasiStatistikApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── uploads/                         # File storage directory
│   └── publications/
├── pom.xml
├── API_DOCUMENTATION.md
├── BPS-Publikasi-Statistik-API.postman_collection.json
├── BPS-API-Local.postman_environment.json
└── README.md
```

---

## 🚀 Setup & Installation

### Prasyarat
- ✅ Java JDK 17+
- ✅ MySQL 8.0
- ✅ Maven 3.6+
- ✅ IDE (IntelliJ / Eclipse / VS Code)
- ✅ Postman

### Langkah Instalasi
1. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd publikasi-statistik
   ```

2. **Setup Database**
   ```sql
   CREATE DATABASE bps_publikasi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

   CREATE USER 'bps_user'@'localhost' IDENTIFIED BY 'bps_password';
   GRANT ALL PRIVILEGES ON bps_publikasi.* TO 'bps_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Konfigurasi `application.properties`**
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/bps_publikasi?useSSL=false&serverTimezone=UTC
   #sesuaikan dengan username dan password database Anda
   spring.datasource.username=bps_user 
   spring.datasource.password=bps_password

   # JWT Secret (ganti dengan secret Anda sendiri)
   app.jwt.secret=bpsPublikasiStatistikSecretKeyYangSangatPanjangDanAman2024
   app.jwt.expiration=86400000
   ```

4. **Build Project**
   ```bash
   mvn clean install
   ```

5. **Run Application**
   ```bash
   mvn spring-boot:run
   ```
   Atau jalankan dari IDE dengan run PublikasiStatistikApplication.java

6. **Aplikasi Berjalan di:**
   ```bash
   http://localhost:8080
   ```
   
7. **Akses Swagger UI:**
   ```bash
   http://localhost:8080/swagger-ui/index.html
   ```

---

## 📊 Database Schema

### Tables:
1. **user**
   ```sql
   id              BIGINT PRIMARY KEY AUTO_INCREMENT
   username        VARCHAR(50) UNIQUE NOT NULL
   email           VARCHAR(100) UNIQUE NOT NULL
   password        VARCHAR(255) NOT NULL
   role            ENUM('USER', 'ADMIN') NOT NULL
   created_at      DATETIME NOT NULL
   updated_at      DATETIME
   ```
   
2. **categories**
   ```sql
   id              BIGINT PRIMARY KEY AUTO_INCREMENT
   name            VARCHAR(100) UNIQUE NOT NULL
   description     TEXT
   created_at      DATETIME NOT NULL
   ```

3. **publications**
   ```sql
   id              BIGINT PRIMARY KEY AUTO_INCREMENT
   title           VARCHAR(255) NOT NULL
   description     TEXT
   file_name       VARCHAR(255) NOT NULL
   file_path       VARCHAR(500) NOT NULL
   file_size       BIGINT
   year            INT NOT NULL
   author          VARCHAR(100)
   views           INT DEFAULT 0
   downloads       INT DEFAULT 0
   category_id     BIGINT NOT NULL (FK -> categories.id)
   uploaded_by     BIGINT NOT NULL (FK -> users.id)
   created_at      DATETIME NOT NULL
   updated_at      DATETIME
   ```

### Relationships:
- publications.category_id → categories.id (Many-to-One)
- publications.uploaded_by → users.id (Many-to-One)

---

## 🔑 Initial Setup Data
1. **Insert Kategori**
   ```sql
   INSERT INTO categories (name, description, created_at) VALUES
   ('Statistik Ekonomi', 'Data dan publikasi terkait ekonomi Indonesia', NOW()),
   ('Statistik Sosial', 'Data dan publikasi terkait sosial dan kesejahteraan', NOW()),
   ('Statistik Kependudukan', 'Data demografi dan kependudukan', NOW()),
   ('Statistik Pertanian', 'Data pertanian dan perkebunan', NOW());
   ```

2. **Buat Admin User**
   
   **Via API (Register lalu update role di database):**
   ```sql
   # 1. Register via API
   POST http://localhost:8080/api/auth/register
   Body: {
   "username": "admin_bps",
   "email": "admin@bps.go.id",
   "password": "admin123"
   }
   
   # 2. Update role di database
   UPDATE users SET role = 'ADMIN' WHERE email = 'admin@bps.go.id';
   ```

---

## 📡 API Endpoints

| Method | Endpoint | Description | Auth Required | Admin Only |
|:------:|:----------|:-------------|:---------------:|:-------------:|
| **POST** | `/auth/register` | Register user baru | ❌ | ❌ |
| **POST** | `/auth/login` | Login dan dapat token | ❌ | ❌ |
| **GET** | `/profile` | Get user profile | ✅ | ❌ |
| **PUT** | `/profile` | Update profile | ✅ | ❌ |
| **PUT** | `/profile/password` | Change password | ✅ | ❌ |
| **DELETE** | `/profile` | Delete account | ✅ | ❌ |
| **GET** | `/categories` | Get all categories | ✅ | ❌ |
| **GET** | `/categories/{id}` | Get category by ID | ✅ | ❌ |
| **POST** | `/categories` | Create category | ✅ | ✅ |
| **PUT** | `/categories/{id}` | Update category | ✅ | ✅ |
| **DELETE** | `/categories/{id}` | Delete category | ✅ | ✅ |
| **GET** | `/publications` | Get all publications | ✅ | ❌ |
| **GET** | `/publications/latest` | Get latest 10 | ✅ | ❌ |
| **GET** | `/publications/most-downloaded` | Get top downloads | ✅ | ❌ |
| **GET** | `/publications/{id}` | Get publication by ID | ✅ | ❌ |
| **POST** | `/publications` | Upload publication | ✅ | ✅ |
| **PUT** | `/publications/{id}` | Update publication | ✅ | ✅ |
| **DELETE** | `/publications/{id}` | Delete publication | ✅ | ✅ |
| **GET** | `/publications/{id}/download` | Download file | ✅ | ❌ |

📖 **Dokumentasi Lengkap:** Lihat file `API_DOCUMENTATION.md`

---

## 🔒 Security & Authentication

### JWT Token Authentication:
- Token expire: 24 jam (86400000 ms)
- Algorithm: HS256
- Header format: ```Authorization: Bearer <token>```

### Role-Based Access:

- **USER**: Read-only access, dapat download
- **ADMIN**: Full access, dapat upload & manage

### Password Security:

- Hash algorithm: BCrypt
- Minimum length: **8 characters**

---

## 📁 File Upload Configuration

### Supported File Types:

- ✅ PDF (```.pdf```)
- ✅ Excel (```.xls```, ```.xlsx```)

### Max File Size:

- **10 MB**

### Storage Location:

- ```uploads/publications/```
- File naming: UUID format (e.g., ```a1b2c3d4-e5f6-7890.pdf```)

---

## 🐛 Troubleshooting

### Error: "Could not create upload directory"
**Solusi:**
   ```bash
   bashmkdir -p uploads/publications
   chmod -R 777 uploads  # Mac/Linux
   ```

### Error: "Access denied for user"
**Solusi:**
- Cek username & password di ```application.properties```
- Pastikan user sudah dibuat di MySQL

### Error: "Table doesn't exist"
**Solusi:**
- Set ```spring.jpa.hibernate.ddl-auto=update``` di application.properties
- Restart aplikasi untuk auto-create tables

### : 401 Unauthorized
**Solusi:**
- Pastikan token belum expire (24 jam)
- Login ulang untuk mendapat token baru
- Cek format header: ```Authorization: Bearer <token>```

---

## 👥 Author
**Nama:** Mohammad Agam Bonanza  
**NIM:** 222313214  
**Kelas:** 3SI1  
**Institusi:** Politeknik Statistika STIS  
**Mata Kuliah:** Pemrograman Platform Khusus

---

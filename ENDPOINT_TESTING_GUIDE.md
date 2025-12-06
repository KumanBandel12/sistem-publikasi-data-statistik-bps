# 📋 Panduan Testing Lengkap - BPS Publikasi Statistik API

## 🎯 Total Endpoints: **36 Endpoints** ⭐ *UPDATED*

---

## 📊 Checklist Testing

### ✅ **Authentication & Authorization (3 endpoints)**
- [ ] Register User
- [ ] Login User
- [ ] Forgot Password

### ✅ **User Profile Management (7 endpoints)**
- [ ] Get Profile
- [ ] Update Profile
- [ ] Change Password
- [ ] Delete Account
- [ ] Upload Profile Picture
- [ ] Get Profile Picture
- [ ] **Delete Profile Picture** *(NEW)*

### ✅ **Category Management (7 endpoints)**
- [ ] Get All Categories
- [ ] Get Category Tree
- [ ] Get Sub-Categories
- [ ] Get Category by ID
- [ ] Create Category (Admin)
- [ ] Update Category (Admin)
- [ ] Delete Category (Admin)

### ✅ **Publication Management (10 endpoints)** ⭐ *+1 NEW*
- [ ] Get All Publications (with Search & Filter)
- [ ] **Get Search Suggestions (Autocomplete)** ⭐ *NEW*
- [ ] Get Latest Publications
- [ ] Get Most Downloaded
- [ ] Get Publication by ID
- [ ] Upload Publication (Admin)
- [ ] Update Publication (Admin)
- [ ] Delete Publication (Admin)
- [ ] Download Publication File
- [ ] Get Publication Cover Image

### ✅ **Notification System (7 endpoints)** ⭐ *+1 NEW*
- [ ] Get All Notifications
- [ ] **Get Unread Notifications** ⭐ *NEW*
- [ ] Get Unread Count
- [ ] Mark as Read
- [ ] Mark All as Read
- [ ] Delete Notification
- [ ] Clear All Read Notifications

### ✅ **Search & Search History (3 endpoints)** ⭐ *UPDATED*
- [ ] **Get Search Suggestions** ⭐ *(moved from Publications)*
- [ ] Search Publications (Auto-save to History)
- [ ] Get Search History
- [ ] Clear Search History

---

## 🚀 Setup & Prerequisites

### 1. **Start Application**
```bash
# Jalankan aplikasi Spring Boot
mvnw spring-boot:run
```

### 2. **Base URL**
```
http://localhost:8080/api
```

### 3. **Swagger UI**
```
http://localhost:8080/swagger-ui/index.html
```

### 4. **Database**
- MySQL harus running di `localhost:3306`
- Database: `bps_publikasi`
- Auto-create schema dengan `ddl-auto=update`

---

## 🔐 1. AUTHENTICATION & AUTHORIZATION (3 endpoints)

### 1.1 Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "Password123!",
  "fullName": "John Doe",
  "gender": "L",
  "placeOfBirth": "Jakarta",
  "dateOfBirth": "1990-01-15",
  "phoneNumber": "081234567890",
  "address": "Jl. Sudirman No. 1"
}
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

**Notes:**
- Password harus min 8 karakter
- **Wajib mengandung:**
  - Minimal 1 huruf besar (A-Z)
  - Minimal 1 huruf kecil (a-z)
  - Minimal 1 angka (0-9)
- **Karakter yang diizinkan:**
  - Huruf (A-Z, a-z)
  - Angka (0-9)
  - Simbol umum: `@$!%*?&.,;:#_-+=(){}[]<>|/\~`'"^` (opsional)
- Phone number otomatis dinormalisasi ke format `628XXXXXXXXX`
- Default role adalah `USER`

---

### 1.2 Login User
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "Password123!"
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

**⚠️ PENTING:** Simpan `token` untuk digunakan di semua request berikutnya!

---

### 1.3 Forgot Password
```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "john@example.com",
  "placeOfBirth": "Jakarta",
  "dateOfBirth": "1990-01-15",
  "newPassword": "NewPassword123!"
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Password reset successfully"
}
```

**Notes:**
- Validasi dengan 3 security questions: email, tempat lahir, tanggal lahir
- Tempat lahir tidak case-sensitive
- **New password requirements:**
  - Minimal 8 karakter
  - Minimal 1 huruf besar (A-Z)
  - Minimal 1 huruf kecil (a-z)
  - Minimal 1 angka (0-9)
  - Hanya boleh mengandung huruf, angka, dan simbol umum
  - Simbol opsional
- Setelah berhasil, user dapat login dengan password baru
- Akan mengirim notifikasi security alert ke user

---

## 👤 2. USER PROFILE MANAGEMENT (8 endpoints)

**⚠️ Semua endpoint profile memerlukan Bearer Token!**

### Header untuk semua request:
```http
Authorization: Bearer {token_dari_login}
```

---

### 2.1 Get Profile
```http
GET /api/profile
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "data": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "gender": "L",
    "placeOfBirth": "Jakarta",
    "dateOfBirth": "1990-01-15",
    "phoneNumber": "628123456789",
    "address": "Jl. Sudirman No. 1",
    "profilePicture": "profile_12345.jpg",
    "role": "USER",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

---

### 2.2 Update Profile
```http
PUT /api/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "johndoe_updated",
  "email": "john.new@example.com",
  "fullName": "John Doe Updated",
  "gender": "L",
  "placeOfBirth": "Jakarta Selatan",
  "dateOfBirth": "1990-01-15",
  "phoneNumber": "082345678901",
  "address": "Jl. Thamrin No. 2"
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "username": "johndoe_updated",
    "email": "john.new@example.com",
    "fullName": "John Doe Updated",
    ...
  }
}
```

**Notes:**
- Semua field opsional (update field yang diinginkan saja)
- Username dan email harus unik
- Phone number otomatis dinormalisasi
- Akan mengirim notifikasi "Profile Updated"

---

### 2.3 Change Password
```http
PUT /api/profile/password
Authorization: Bearer {token}
Content-Type: application/json

{
  "oldPassword": "Password123!",
  "newPassword": "NewPassword456!",
  "confirmPassword": "NewPassword456!"
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Password changed successfully"
}
```

**Notes:**
- Old password harus benar
- New password harus berbeda dari old password
- **Password requirements:**
  - Minimal 8 karakter
  - Minimal 1 huruf besar (A-Z)
  - Minimal 1 huruf kecil (a-z)
  - Minimal 1 angka (0-9)
  - Hanya boleh mengandung huruf, angka, dan simbol umum
  - Simbol opsional
- Akan mengirim notifikasi "Password Changed"

---

### 2.4 Delete Account
```http
DELETE /api/profile
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Account deleted successfully"
}
```

**Notes:**
- Menghapus akun permanen
- Profile picture (jika ada) akan dihapus otomatis
- Semua notifikasi user juga terhapus (cascade)

---

### 2.5 Upload Profile Picture
```http
POST /api/profile/picture
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [pilih file gambar JPG/PNG]
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile picture uploaded successfully",
  "data": {
    "id": 1,
    "username": "johndoe",
    "profilePicture": "profile_1234567890.jpg",
    ...
  }
}
```

**Notes:**
- Hanya menerima JPG, JPEG, PNG
- Max size: 10MB
- Gambar lama otomatis terhapus jika ada

---

### 2.6 Get Profile Picture
```http
GET /api/profile/picture
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
- Content-Type: `image/jpeg` atau `image/png`
- Body: Binary image data
- Header: `Content-Disposition: inline; filename="profile_xxx.jpg"`

**Notes:**
- Mengembalikan gambar langsung (bukan JSON)
- Bisa ditampilkan di browser atau `<img>` tag
- Return 404 jika tidak ada gambar

---

### 2.7 **Delete Profile Picture** ⭐ *NEW*
```http
DELETE /api/profile/picture
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile picture deleted successfully",
  "data": {
    "id": 1,
    "username": "johndoe",
    "profilePicture": null,
    ...
  }
}
```

**Notes:**
- Menghapus file gambar dari storage
- Set `profilePicture` field menjadi `null` di database
- Return error jika tidak ada gambar untuk dihapus

---

## 📂 3. CATEGORY MANAGEMENT (7 endpoints)

### 3.1 Get All Categories
```http
GET /api/categories
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Categories retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Ekonomi",
      "description": "Data ekonomi dan bisnis",
      "publicationCount": 15,
      "level": 0,
      "parentId": null,
      "hasSubCategories": true,
      "displayOrder": 1
    },
    {
      "id": 2,
      "name": "Inflasi",
      "description": "Data inflasi dan harga",
      "publicationCount": 5,
      "level": 1,
      "parentId": 1,
      "parentName": "Ekonomi",
      "hasSubCategories": false,
      "displayOrder": 1
    }
  ]
}
```

---

### 3.2 Get Category Tree
```http
GET /api/categories/tree
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Category tree retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Ekonomi",
      "description": "Data ekonomi",
      "level": 0,
      "publicationCount": 15,
      "subCategories": [
        {
          "id": 2,
          "name": "Inflasi",
          "description": "Data inflasi",
          "level": 1,
          "parentId": 1,
          "publicationCount": 5,
          "subCategories": []
        },
        {
          "id": 3,
          "name": "Perdagangan",
          "level": 1,
          "parentId": 1,
          "publicationCount": 10,
          "subCategories": []
        }
      ]
    }
  ]
}
```

**Notes:**
- Menampilkan struktur hierarki lengkap
- Root categories di level pertama
- Sub-categories nested di dalam parent
- Max 2 levels (root + sub)

---

### 3.3 Get Sub-Categories
```http
GET /api/categories/{parentId}/subcategories
Authorization: Bearer {token}

# Contoh:
GET /api/categories/1/subcategories
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Sub-categories retrieved successfully",
  "data": [
    {
      "id": 2,
      "name": "Inflasi",
      "description": "Data inflasi",
      "level": 1,
      "parentId": 1,
      "parentName": "Ekonomi",
      "publicationCount": 5
    },
    {
      "id": 3,
      "name": "Perdagangan",
      "level": 1,
      "parentId": 1,
      "parentName": "Ekonomi",
      "publicationCount": 10
    }
  ]
}
```

---

### 3.4 Get Category by ID
```http
GET /api/categories/{id}
Authorization: Bearer {token}

# Contoh:
GET /api/categories/1
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Category retrieved successfully",
  "data": {
    "id": 1,
    "name": "Ekonomi",
    "description": "Data ekonomi dan bisnis",
    "level": 0,
    "publicationCount": 15,
    "hasSubCategories": true,
    "displayOrder": 1
  }
}
```

---

### 3.5 Create Category (Admin Only) 🔒
```http
POST /api/categories
Authorization: Bearer {admin_token}
Content-Type: application/json

# Root Category:
{
  "name": "Ekonomi",
  "description": "Data ekonomi dan bisnis",
  "displayOrder": 1
}

# Sub-Category:
{
  "name": "Inflasi",
  "description": "Data inflasi dan harga",
  "parentId": 1,
  "displayOrder": 1
}
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "message": "Category created successfully",
  "data": {
    "id": 1,
    "name": "Ekonomi",
    "description": "Data ekonomi dan bisnis",
    "level": 0,
    "publicationCount": 0
  }
}
```

**Notes:**
- **Hanya ADMIN** yang bisa create
- Untuk root category: jangan kirim `parentId`
- Untuk sub-category: kirim `parentId` dari parent
- Max 2 level hierarchy (tidak bisa buat sub-sub-category)
- `displayOrder` opsional (default: 0)

---

### 3.6 Update Category (Admin Only) 🔒
```http
PUT /api/categories/{id}
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "Ekonomi Updated",
  "description": "Deskripsi baru",
  "displayOrder": 2
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Category updated successfully",
  "data": {
    "id": 1,
    "name": "Ekonomi Updated",
    "description": "Deskripsi baru",
    "displayOrder": 2
  }
}
```

**Notes:**
- **Hanya ADMIN** yang bisa update
- Tidak bisa mengubah `parentId` (tidak bisa move category)
- Untuk memindahkan category, hapus dan buat ulang

---

### 3.7 Delete Category (Admin Only) 🔒
```http
DELETE /api/categories/{id}
Authorization: Bearer {admin_token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Category deleted successfully"
}
```

**Notes:**
- **Hanya ADMIN** yang bisa delete
- Tidak bisa hapus category yang masih punya publikasi
- Tidak bisa hapus parent category yang masih punya sub-categories
- Hapus semua sub-categories terlebih dahulu

---

## 📚 4. PUBLICATION MANAGEMENT (10 endpoints)

### 4.1 Get All Publications (with Search & Filter)
```http
GET /api/publications
Authorization: Bearer {token}

# Tanpa filter (semua publikasi):
GET /api/publications

# Search by keyword:
GET /api/publications?search=inflasi

# Filter by category:
GET /api/publications?categoryId=1

# Filter by year:
GET /api/publications?year=2024

# Sort:
GET /api/publications?sort=latest
GET /api/publications?sort=oldest
GET /api/publications?sort=most-downloaded
GET /api/publications?sort=most-viewed

# Kombinasi filter:
GET /api/publications?search=ekonomi&categoryId=1&year=2024&sort=latest
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Publications retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Laporan Inflasi Januari 2024",
      "description": "Analisis inflasi bulan Januari",
      "author": "BPS",
      "publishDate": "2024-01-15",
      "fileUrl": "/api/publications/1/download",
      "coverImageUrl": "/api/publications/1/cover",
      "categoryId": 2,
      "categoryName": "Inflasi",
      "viewCount": 150,
      "downloadCount": 75,
      "uploadedBy": "admin",
      "createdAt": "2024-01-15T10:00:00"
    }
  ]
}
```

**Query Parameters:**
- `search`: Cari di title, description, author
- `categoryId`: Filter by category (include sub-categories)
- `year`: Filter by publish year
- `sort`: `latest`, `oldest`, `most-downloaded`, `most-viewed`

---

### 4.2 Get Latest Publications
```http
GET /api/publications/latest
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Latest publications retrieved successfully",
  "data": [
    // Top 10 publikasi terbaru berdasarkan createdAt
  ]
}
```

---

### 4.3 Get Most Downloaded
```http
GET /api/publications/most-downloaded
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Most downloaded publications retrieved successfully",
  "data": [
    // Top 10 publikasi paling banyak didownload
  ]
}
```

---

### 4.4 Get Publication by ID
```http
GET /api/publications/{id}
Authorization: Bearer {token}

# Contoh:
GET /api/publications/1
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Publication retrieved successfully",
  "data": {
    "id": 1,
    "title": "Laporan Inflasi Januari 2024",
    "description": "Analisis lengkap inflasi bulan Januari 2024",
    "author": "BPS Jakarta",
    "publishDate": "2024-01-15",
    "fileUrl": "/api/publications/1/download",
    "coverImageUrl": "/api/publications/1/cover",
    "categoryId": 2,
    "categoryName": "Inflasi",
    "viewCount": 151,
    "downloadCount": 75,
    "uploadedBy": "admin",
    "createdAt": "2024-01-15T10:00:00"
  }
}
```

**Notes:**
- **Otomatis increment `viewCount` setiap kali diakses**
- Digunakan untuk detail page publikasi

---

### 4.5 Upload Publication (Admin Only) 🔒
```http
POST /api/publications
Authorization: Bearer {admin_token}
Content-Type: multipart/form-data

title: Laporan Inflasi Januari 2024
description: Analisis inflasi bulan Januari
author: BPS Jakarta
publishDate: 2024-01-15
categoryId: 2
file: [pilih file PDF]
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "message": "Publication uploaded successfully",
  "data": {
    "id": 1,
    "title": "Laporan Inflasi Januari 2024",
    "fileUrl": "/api/publications/1/download",
    "coverImageUrl": "/api/publications/1/cover",
    ...
  }
}
```

**Notes:**
- **Hanya ADMIN** yang bisa upload
- File harus PDF
- Max size: 10MB
- Cover image otomatis di-generate dari halaman pertama PDF
- Akan mengirim notifikasi "NEW_PUBLICATION" ke semua users

---

### 4.6 Update Publication (Admin Only) 🔒
```http
PUT /api/publications/{id}
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "title": "Laporan Inflasi Januari 2024 (Revised)",
  "description": "Deskripsi baru",
  "author": "BPS Jakarta",
  "publishDate": "2024-01-15",
  "categoryId": 2
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Publication updated successfully",
  "data": {
    "id": 1,
    "title": "Laporan Inflasi Januari 2024 (Revised)",
    ...
  }
}
```

**Notes:**
- **Hanya ADMIN** yang bisa update
- Tidak mengubah file PDF (untuk ganti file, hapus dan upload ulang)
- View count dan download count tetap dipertahankan

---

### 4.7 Delete Publication (Admin Only) 🔒
```http
DELETE /api/publications/{id}
Authorization: Bearer {admin_token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Publication deleted successfully"
}
```

**Notes:**
- **Hanya ADMIN** yang bisa delete
- File PDF dan cover image otomatis terhapus dari storage

---

### 4.8 Download Publication File
```http
GET /api/publications/{id}/download
Authorization: Bearer {token}

# Contoh:
GET /api/publications/1/download
```

**Expected Response (200 OK):**
- Content-Type: `application/pdf`
- Header: `Content-Disposition: attachment; filename="laporan_inflasi.pdf"`
- Body: Binary PDF data

**Notes:**
- **Otomatis increment `downloadCount` setiap kali didownload**
- Browser akan otomatis download file
- Milestone detection: akan notifikasi admin saat mencapai 100, 500, 1000 downloads

---

### 4.9 Get Publication Cover Image
```http
GET /api/publications/{id}/cover
Authorization: Bearer {token}

# Contoh:
GET /api/publications/1/cover
```

**Expected Response (200 OK):**
- Content-Type: `image/jpeg`
- Header: `Content-Disposition: inline; filename="cover_xxx.jpg"`
- Body: Binary image data

**Notes:**
- Cover otomatis di-generate saat upload PDF
- Diambil dari halaman pertama PDF menggunakan Apache PDFBox
- Return 404 jika cover tidak tersedia

---

## 🔔 5. NOTIFICATION SYSTEM (6 endpoints)

### 5.1 Get All Notifications
```http
GET /api/notifications
Authorization: Bearer {token}

# With pagination:
GET /api/notifications?page=0&size=10
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Notifications retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "type": "NEW_PUBLICATION",
        "title": "Publikasi Baru",
        "message": "Publikasi baru telah ditambahkan: Laporan Inflasi Januari 2024",
        "read": false,
        "relatedEntityType": "PUBLICATION",
        "relatedEntityId": 1,
        "createdAt": "2024-01-15T10:00:00"
      },
      {
        "id": 2,
        "type": "PROFILE_UPDATED",
        "title": "Profil Diperbarui",
        "message": "Profil Anda telah berhasil diperbarui",
        "read": true,
        "relatedEntityType": null,
        "relatedEntityId": null,
        "createdAt": "2024-01-14T15:30:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 25,
    "totalPages": 3
  }
}
```

**Notification Types:**
1. `NEW_PUBLICATION` - Publikasi baru ditambahkan
2. `PROFILE_UPDATED` - Profil berhasil diupdate
3. `PASSWORD_CHANGED` - Password berhasil diubah
4. `ADMIN_NEW_USER` - User baru register (untuk admin)
5. `ADMIN_MILESTONE` - Milestone downloads tercapai (untuk admin)

---

### 5.2 Get Unread Notifications
```http
GET /api/notifications/unread
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Unread notifications retrieved successfully",
  "data": [
    // Array of unread notifications only
  ]
}
```

---

### 5.3 Get Unread Count
```http
GET /api/notifications/unread/count
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Unread count retrieved successfully",
  "data": 5
}
```

**Notes:**
- Return integer count
- Berguna untuk badge notification di UI

---

### 5.4 Mark as Read
```http
PUT /api/notifications/{id}/read
Authorization: Bearer {token}

# Contoh:
PUT /api/notifications/1/read
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Notification marked as read",
  "data": {
    "id": 1,
    "type": "NEW_PUBLICATION",
    "read": true,
    ...
  }
}
```

---

### 5.5 Mark All as Read
```http
PUT /api/notifications/read-all
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "All notifications marked as read"
}
```

**Notes:**
- Mark semua notifikasi user menjadi read
- Berguna untuk "Clear all" button

---

### 5.6 Delete Notification
```http
DELETE /api/notifications/{id}
Authorization: Bearer {token}

# Contoh:
DELETE /api/notifications/1
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Notification deleted successfully"
}
```

**Notes:**
- User hanya bisa delete notifikasi miliknya sendiri
- Notifikasi otomatis terhapus setelah 7 hari (cleanup job)

---

## 🔍 6. SEARCH & SEARCH HISTORY (3 endpoints)

### 6.1 Get Search Suggestions (Autocomplete)
```http
GET /api/publications/suggestions?keyword=Statistik
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Suggestions retrieved",
  "data": [
    "Statistik Kesejahteraan Indonesia",
    "Statistik Pendidikan Indonesia",
    "Statistik Kesehatan Indonesia",
    "Statistik Pemuda Indonesia",
    "Statistik Indonesia 2025"
  ]
}
```

**Notes:**
- Return: Array of String (judul publikasi saja)
- Maksimal 5 suggestions
- Case-insensitive search
- Muncul saat user mengetik di search bar
- Digunakan untuk autocomplete/dropdown suggestion

**Testing Scenarios:**
```bash
# Test 1: Keyword pendek
GET /api/publications/suggestions?keyword=Stat

# Test 2: Keyword spesifik
GET /api/publications/suggestions?keyword=Inflasi

# Test 3: Keyword dengan spasi
GET /api/publications/suggestions?keyword=Statistik%20Indonesia

# Test 4: Keyword tidak ada
GET /api/publications/suggestions?keyword=XYZABC123
# Expected: Empty array []
```

---

### 6.2 Search Publications (with Auto-Save to History)
```http
GET /api/publications?search=Statistik Indonesia
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Publications retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Statistik Indonesia 2025",
      "description": "...",
      ...
    }
  ]
}
```

**Notes:**
- **AUTO-SAVE:** Keyword otomatis tersimpan ke search history
- **UPSERT Logic:** 
  - Jika keyword sudah pernah dicari → Update timestamp (naik ke atas)
  - Jika keyword baru → Insert record baru
- Kombinasi dengan filter & sort tetap berjalan

**Testing Scenarios:**
```bash
# Test 1: Search saja
GET /api/publications?search=Ekonomi

# Test 2: Search + Filter Category
GET /api/publications?search=Inflasi&categoryId=2

# Test 3: Search + Filter Year
GET /api/publications?search=Statistik&year=2024

# Test 4: Search + Sort
GET /api/publications?search=Indonesia&sort=latest

# Test 5: Kombinasi lengkap
GET /api/publications?search=Ekonomi&categoryId=1&year=2024&sort=most-downloaded
```

---

### 6.3 Get My Search History
```http
GET /api/search/history
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Search history retrieved successfully",
  "data": [
    {
      "id": 5,
      "keyword": "Statistik Indonesia",
      "searchedAt": "2024-11-30T15:45:00"
    },
    {
      "id": 3,
      "keyword": "Ekonomi",
      "searchedAt": "2024-11-30T14:30:00"
    },
    {
      "id": 1,
      "keyword": "Inflasi",
      "searchedAt": "2024-11-30T10:15:00"
    }
  ]
}
```

**Notes:**
- Sorted by **newest first** (searchedAt DESC)
- Maksimal **10 item** terakhir per user
- Tidak ada duplikat keyword per user
- Keyword yang sama akan naik ke paling atas saat dicari lagi

---

### 6.4 Clear All Search History
```http
DELETE /api/search/history
Authorization: Bearer {token}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Search history cleared successfully"
}
```

**Notes:**
- Menghapus semua search history user
- User lain tidak terpengaruh

---

## 📱 Testing Flow: Search & History (Complete Scenario)

### **Scenario 1: First Time Search**

**Step 1: User pertama kali buka halaman search**
```bash
GET /api/search/history
```
**Expected:** Empty array `[]` (belum ada history)

---

**Step 2: User mengetik "Stat" di search bar**
```bash
GET /api/publications/suggestions?keyword=Stat
```
**Expected:** 
```json
["Statistik Indonesia 2025", "Statistik Pemuda Indonesia", ...]
```

---

**Step 3: User klik suggestion "Statistik Indonesia 2025" atau tekan Enter**
```bash
GET /api/publications?search=Statistik Indonesia 2025
```
**Expected:** 
- Dapat hasil publikasi
- Keyword **otomatis tersimpan** ke history

---

**Step 4: Verify history tersimpan**
```bash
GET /api/search/history
```
**Expected:**
```json
[
  {
    "id": 1,
    "keyword": "Statistik Indonesia 2025",
    "searchedAt": "2024-11-30T15:00:00"
  }
]
```

---

### **Scenario 2: Search Keyword yang Sama (Test Upsert Logic)**

**Step 1: User search keyword baru**
```bash
GET /api/publications?search=Ekonomi
GET /api/publications?search=Inflasi
```

**Step 2: Cek history**
```bash
GET /api/search/history
```
**Expected (urutan terbaru di atas):**
```json
[
  {"id": 3, "keyword": "Inflasi", "searchedAt": "15:05:00"},
  {"id": 2, "keyword": "Ekonomi", "searchedAt": "15:02:00"},
  {"id": 1, "keyword": "Statistik Indonesia 2025", "searchedAt": "15:00:00"}
]
```

---

**Step 3: User search "Ekonomi" lagi (keyword yang sudah ada)**
```bash
GET /api/publications?search=Ekonomi
```

---

**Step 4: Verify upsert logic → Keyword "Ekonomi" naik ke atas**
```bash
GET /api/search/history
```
**Expected:**
```json
[
  {"id": 2, "keyword": "Ekonomi", "searchedAt": "15:10:00"},  ← NAIK KE ATAS!
  {"id": 3, "keyword": "Inflasi", "searchedAt": "15:05:00"},
  {"id": 1, "keyword": "Statistik Indonesia 2025", "searchedAt": "15:00:00"}
]
```
**✅ PASSED:** Tidak ada duplikat, timestamp diupdate, keyword naik ke paling atas

---

### **Scenario 3: Test History Limit (Max 10 Items)**

**Step 1: Search 15 keyword berbeda**
```bash
GET /api/publications?search=Keyword1
GET /api/publications?search=Keyword2
GET /api/publications?search=Keyword3
...
GET /api/publications?search=Keyword15
```

---

**Step 2: Verify hanya 10 terakhir yang tersimpan**
```bash
GET /api/search/history
```
**Expected:** Hanya 10 item (Keyword6 sampai Keyword15)
```json
[
  {"keyword": "Keyword15", ...},
  {"keyword": "Keyword14", ...},
  ...
  {"keyword": "Keyword6", ...}
]
```
**✅ PASSED:** Keyword1-5 otomatis terhapus (hanya 10 terbaru)

---

### **Scenario 4: Test Search + Filter + Sort (Kombinasi)**

**Step 1: Search dengan filter & sort**
```bash
GET /api/publications?search=Statistik&categoryId=1&year=2024&sort=latest
```
**Expected:** 
- ✅ Hasil publikasi filtered & sorted
- ✅ Keyword "Statistik" tersimpan ke history

---

**Step 2: Verify history**
```bash
GET /api/search/history
```
**Expected:** Keyword "Statistik" ada di history

**✅ PASSED:** Search + filter + sort tetap compatible

---

### **Scenario 5: Test Clear History**

**Step 1: Clear all history**
```bash
DELETE /api/search/history
```
**Expected:** `{ "success": true, "message": "Search history cleared successfully" }`

---

**Step 2: Verify history kosong**
```bash
GET /api/search/history
```
**Expected:** Empty array `[]`

**✅ PASSED:** History terhapus semua

---

### **Scenario 6: Test Suggestion Limit (Max 5)**

**Step 1: Search keyword umum yang banyak match**
```bash
GET /api/publications/suggestions?keyword=Stat
```

**Expected:** Maksimal 5 suggestions muncul
```json
[
  "Statistik Indonesia 2025",
  "Statistik Kesejahteraan Indonesia",
  "Statistik Pendidikan Indonesia",
  "Statistik Kesehatan Indonesia",
  "Statistik Pemuda Indonesia"
]
```
**✅ PASSED:** Hanya 5 suggestions (bukan 10, 20, atau semua)

---

### **Scenario 7: Multiple Users (History Isolation)**

**Step 1: User A login dan search**
```bash
# User A login
POST /api/auth/login (email: userA@example.com)

# User A search
GET /api/publications?search=Ekonomi
```

---

**Step 2: User B login dan search**
```bash
# User B login
POST /api/auth/login (email: userB@example.com)

# User B search
GET /api/publications?search=Inflasi
```

---

**Step 3: Verify isolation**
```bash
# User A check history
GET /api/search/history (with User A token)
Expected: [{"keyword": "Ekonomi", ...}]

# User B check history
GET /api/search/history (with User B token)
Expected: [{"keyword": "Inflasi", ...}]
```
**✅ PASSED:** History terpisah per user, tidak tercampur

---

## 📊 Search & History Testing Checklist

### **Suggestions Endpoint:**
- [ ] Test suggestion dengan keyword pendek (1-2 huruf)
- [ ] Test suggestion dengan keyword spesifik
- [ ] Test suggestion dengan keyword tidak ada (empty array)
- [ ] Verify maksimal 5 suggestions
- [ ] Test case-insensitive (stat = Stat = STAT)

### **Search with Auto-Save:**
- [ ] Test search simple (hanya search parameter)
- [ ] Test search + filter category
- [ ] Test search + filter year
- [ ] Test search + sort
- [ ] Test kombinasi lengkap (search + category + year + sort)
- [ ] Verify keyword tersimpan ke history

### **Upsert Logic:**
- [ ] Search keyword baru → Verify insert ke history
- [ ] Search keyword yang sama → Verify timestamp update (naik ke atas)
- [ ] Verify tidak ada duplikat keyword per user

### **History Limit:**
- [ ] Search >10 keyword berbeda
- [ ] Verify hanya 10 terakhir yang tersimpan

### **Clear History:**
- [ ] Clear all history
- [ ] Verify history kosong

### **Isolation:**
- [ ] Multiple users dengan history berbeda
- [ ] Verify history tidak tercampur

---

## 🛠️ Testing Tools & Examples

### **1. Postman Collection**
Import dan test langsung:
```json
{
  "name": "Search & History Tests",
  "requests": [
    {
      "name": "Get Suggestions",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/publications/suggestions?keyword=Stat",
        "header": [{"key": "Authorization", "value": "Bearer {{token}}"}]
      }
    },
    {
      "name": "Search with Auto-Save",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/publications?search=Ekonomi",
        "header": [{"key": "Authorization", "value": "Bearer {{token}}"}]
      }
    },
    {
      "name": "Get Search History",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/search/history",
        "header": [{"key": "Authorization", "value": "Bearer {{token}}"}]
      }
    }
  ]
}
```

---

### **2. cURL Examples**

**Get Suggestions:**
```bash
curl -X GET "http://localhost:8080/api/publications/suggestions?keyword=Statistik" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Search with Auto-Save:**
```bash
curl -X GET "http://localhost:8080/api/publications?search=Ekonomi&categoryId=1&year=2024" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Get History:**
```bash
curl -X GET "http://localhost:8080/api/search/history" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Clear History:**
```bash
curl -X DELETE "http://localhost:8080/api/search/history" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### **3. Swagger UI**
Buka: `http://localhost:8080/swagger-ui/index.html`

**Testing Steps:**
1. Klik "Authorize" → Masukkan token
2. Test `/api/publications/suggestions` dengan parameter keyword
3. Test `/api/publications` dengan parameter search
4. Test `/api/search/history` (GET & DELETE)

---

## 📝 Testing Workflow

### **Step 1: Persiapan Database**
1. Pastikan MySQL running
2. Database `bps_publikasi` akan dibuat otomatis
3. Table akan dibuat otomatis saat aplikasi start

### **Step 2: Create Admin User**
Jalankan SQL manual atau register user pertama dan update rolenya:
```sql
-- Setelah register user pertama, update menjadi admin:
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@bps.go.id';
```

### **Step 3: Test Authentication**
1. ✅ Register user baru (USER)
2. ✅ Login dengan user tersebut (simpan token)
3. ✅ Login dengan admin (simpan admin token)

### **Step 4: Test Categories** (sebagai Admin)
1. ✅ Create root category: "Ekonomi"
2. ✅ Create sub-category: "Inflasi" (parentId: Ekonomi)
3. ✅ Get category tree
4. ✅ Get sub-categories
5. ✅ Update category
6. ✅ Try delete (akan error jika ada publikasi)

### **Step 5: Test Publications** (sebagai Admin)
1. ✅ Upload publication dengan PDF
2. ✅ Get publication by ID (cek cover auto-generated)
3. ✅ Search publications
4. ✅ Filter by category
5. ✅ Download publication file
6. ✅ Get cover image
7. ✅ Update publication
8. ✅ Delete publication

### **Step 6: Test Profile** (sebagai User)
1. ✅ Get profile
2. ✅ Update profile (fullName, phone, etc)
3. ✅ Upload profile picture
4. ✅ Get profile picture
5. ✅ **Delete profile picture** *(NEW)*
6. ✅ Change password
7. ✅ Try forgot password

### **Step 7: Test Notifications**
1. ✅ Check notifications after upload publication (user dapat notif NEW_PUBLICATION)
2. ✅ Check notifications after update profile (dapat notif PROFILE_UPDATED)
3. ✅ Check notifications after change password (dapat notif PASSWORD_CHANGED)
4. ✅ Download publication 100x (admin dapat notif ADMIN_MILESTONE)
5. ✅ Get unread count
6. ✅ Mark as read
7. ✅ Mark all as read
8. ✅ Delete notification
9. ✅ Clear all read notifications

### **Step 8: Test Search History**
1. ✅ Search publications dengan keyword (otomatis tersimpan)
2. ✅ Get search history
3. ✅ Manually save search keyword
### **Step 8: Test Search & Search History** ⭐ *NEW*
1. ✅ Get suggestions (autocomplete)
2. ✅ Search publications (auto-save to history)
3. ✅ Verify history saved
4. ✅ Test upsert logic (search same keyword → timestamp update)
5. ✅ Test history limit (max 10 items)
6. ✅ Test search + filter + sort combination
7. ✅ Clear search history

---

## 🛠️ Testing Tools

### **1. Postman**
- Import collection: `BPS-Publikasi-Statistik-API.postman_collection.json`
- Import environment: `BPS-API-Local.postman_environment.json`
- Set `{{baseUrl}}` = `http://localhost:8080/api`
- Set `{{token}}` setelah login

### **2. Swagger UI**
- Buka: `http://localhost:8080/swagger-ui/index.html`
- Klik "Authorize" button
- Masukkan token: `Bearer {token}`
- Test langsung dari browser

### **3. cURL**
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Get profile
curl -X GET http://localhost:8080/api/profile \
  -H "Authorization: Bearer {token}"
```

---

## ⚠️ Common Issues & Solutions

### **1. 401 Unauthorized**
- Token expired (expired after 24 hours)
- Token salah atau tidak valid
- **Solution:** Login ulang untuk dapat token baru

### **2. 403 Forbidden**
- Endpoint butuh ADMIN role tapi user role = USER
- **Solution:** Pastikan login dengan admin account

### **3. 404 Not Found**
- Resource tidak ada (category, publication, notification)
- **Solution:** Cek ID yang digunakan

### **4. 400 Bad Request**
- Validation error (password kurang dari 8 karakter, dll)
- **Solution:** Cek error message di response

### **5. File Upload Error**
- File size > 10MB
- File type bukan PDF (untuk publication)
- File type bukan JPG/PNG (untuk profile picture)
- **Solution:** Gunakan file yang sesuai requirements

### **6. Database Error**
- MySQL tidak running
- Database belum dibuat
- **Solution:** Pastikan MySQL running dan accessible

---

## 📊 Summary: 36 Endpoints ⭐ *UPDATED*

| Module | Endpoint Count | Auth Required | Admin Only |
|--------|---------------|---------------|------------|
| Authentication | 3 | ❌ (Public) | ❌ |
| User Profile | 7 | ✅ | ❌ |
| Categories | 7 | ✅ | 3 endpoints |
| Publications | 10 | ✅ | 3 endpoints |
| Notifications | 7 | ✅ | ❌ |
| Search & History | 3 | ✅ | ❌ |
| **TOTAL** | **36** | **33** | **6** |

### **Admin-Only Endpoints (6):**
1. `POST /api/categories` - Create Category
2. `PUT /api/categories/{id}` - Update Category
3. `DELETE /api/categories/{id}` - Delete Category
4. `POST /api/publications` - Upload Publication
5. `PUT /api/publications/{id}` - Update Publication
6. `DELETE /api/publications/{id}` - Delete Publication

### **New Endpoints (2):** ⭐
1. `GET /api/publications/suggestions` - Get search suggestions (autocomplete)
2. `GET /api/notifications/unread` - Get unread notifications only

---

## ✅ Final Checklist

Sebelum deploy ke production, pastikan:

- [ ] Semua 37 endpoints sudah ditest
- [ ] Authentication & authorization berfungsi
- [ ] File upload/download berfungsi (PDF & images)
- [ ] Cover auto-generation berfungsi
- [ ] Notification system berfungsi
- [ ] Phone number validation berfungsi
- [ ] Forgot password berfungsi
- [ ] Search & filter berfungsi
- [ ] **Search suggestions (autocomplete) berfungsi** ⭐
- [ ] **Search history auto-save berfungsi** ⭐
- [ ] **Search history upsert logic berfungsi** ⭐
- [ ] **Search history limit (max 10) berfungsi** ⭐
- [ ] Pagination berfungsi (notifications)
- [ ] Cascade delete berfungsi (user, category, publication)
- [ ] Role-based access control (ADMIN vs USER) berfungsi
- [ ] Error handling lengkap
- [ ] Database cleanup job running (notification auto-delete after 7 days)

---

## 📞 Support

Jika ada issue atau pertanyaan:
1. Cek Swagger UI untuk dokumentasi interaktif
2. Cek log aplikasi untuk error details
3. Cek database untuk verify data changes

**Happy Testing! 🚀**

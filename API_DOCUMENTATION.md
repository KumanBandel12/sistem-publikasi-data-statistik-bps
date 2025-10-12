# 📚 Dokumentasi API - BPS Publikasi Statistik

## Gambaran Umum

Dokumen ini menyediakan dokumentasi lengkap untuk API Sistem Publikasi Data Statistik BPS. API ini memungkinkan pengguna untuk mengakses dan mengelola publikasi statistik, kategori, dan profil pengguna melalui antarmuka RESTful.

**Base URL:** `http://localhost:8080/api`

---

## Autentikasi

API ini mengimplementasikan autentikasi berbasis JWT (JSON Web Token) untuk mengamankan endpoint dan mengelola sesi pengguna.

### Format Header Autentikasi

```
Authorization: Bearer <your-jwt-token>
```

### Tingkat Akses Endpoint

**Endpoint Publik** (Tidak memerlukan autentikasi):
- `POST /auth/register` - Pendaftaran pengguna
- `POST /auth/login` - Autentikasi pengguna

**Endpoint Terproteksi:**  
Semua endpoint lainnya memerlukan token JWT yang valid di header Authorization.

---

## 📋 Daftar Isi

1. [Autentikasi](#1-autentikasi)
2. [Manajemen Profil Pengguna](#2-manajemen-profil-pengguna)
3. [Manajemen Kategori](#3-manajemen-kategori)
4. [Manajemen Publikasi](#4-manajemen-publikasi)
5. [Respons Error](#respons-error)
6. [Kode Status HTTP](#kode-status-http)
7. [Kontrol Akses Berbasis Role](#kontrol-akses-berbasis-role)
8. [Panduan Pengujian](#panduan-pengujian)
9. [Sumber Daya Tambahan](#sumber-daya-tambahan)

---

## 1. Autentikasi

### 1.1 Registrasi Pengguna

**Endpoint:** `POST /auth/register`

**Deskripsi:** Membuat akun pengguna baru dalam sistem.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@bps.go.id",
  "password": "password123"
}
```

**Aturan Validasi:**
- `username`: Wajib diisi, minimal 3 karakter, maksimal 50 karakter
- `email`: Wajib diisi, harus berformat email yang valid
- `password`: Wajib diisi, minimal 8 karakter

**Respons Sukses (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@bps.go.id",
    "role": "USER",
    "createdAt": "2024-10-11T10:30:00"
  }
}
```

---

### 1.2 Login

**Endpoint:** `POST /auth/login`

**Deskripsi:** Melakukan autentikasi kredensial pengguna dan mengembalikan token akses JWT.

**Request Body:**
```json
{
  "email": "john@bps.go.id",
  "password": "password123"
}
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "user": {
      "id": 1,
      "username": "john_doe",
      "email": "john@bps.go.id",
      "role": "USER",
      "createdAt": "2024-10-11T10:30:00"
    }
  }
}
```

**Respons Error (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

---

## 2. Manajemen Profil Pengguna

### 2.1 Dapatkan Profil

**Endpoint:** `GET /profile`

**Deskripsi:** Mengambil informasi profil pengguna yang sedang terautentikasi.

**Headers:**
```
Authorization: Bearer <token>
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@bps.go.id",
    "role": "USER",
    "createdAt": "2024-10-11T10:30:00"
  }
}
```

---

### 2.2 Perbarui Profil

**Endpoint:** `PUT /profile`

**Deskripsi:** Memperbarui informasi profil pengguna yang sedang terautentikasi.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "username": "john_updated",
  "email": "john.new@bps.go.id"
}
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "username": "john_updated",
    "email": "john.new@bps.go.id",
    "role": "USER",
    "createdAt": "2024-10-11T10:30:00"
  }
}
```

---

### 2.3 Ubah Password

**Endpoint:** `PUT /profile/password`

**Deskripsi:** Memperbarui password pengguna yang sedang terautentikasi.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "oldPassword": "password123",
  "newPassword": "newpassword456"
}
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Password changed successfully",
  "data": null
}
```

---

### 2.4 Hapus Akun

**Endpoint:** `DELETE /profile`

**Deskripsi:** Menghapus akun pengguna yang sedang terautentikasi secara permanen.

**Headers:**
```
Authorization: Bearer <token>
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Account deleted successfully",
  "data": null
}
```

---

## 3. Manajemen Kategori

### 3.1 Dapatkan Semua Kategori

**Endpoint:** `GET /categories`

**Deskripsi:** Mengambil daftar semua kategori publikasi yang tersedia.

**Headers:**
```
Authorization: Bearer <token>
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Categories retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Statistik Ekonomi",
      "description": "Data dan publikasi terkait ekonomi Indonesia",
      "publicationCount": 5,
      "createdAt": "2024-10-11T10:00:00"
    },
    {
      "id": 2,
      "name": "Statistik Sosial",
      "description": "Data dan publikasi terkait sosial",
      "publicationCount": 3,
      "createdAt": "2024-10-11T10:00:00"
    }
  ]
}
```

---

### 3.2 Dapatkan Kategori Berdasarkan ID

**Endpoint:** `GET /categories/{id}`

**Deskripsi:** Mengambil informasi detail untuk kategori tertentu.

**Headers:**
```
Authorization: Bearer <token>
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Category retrieved successfully",
  "data": {
    "id": 1,
    "name": "Statistik Ekonomi",
    "description": "Data dan publikasi terkait ekonomi Indonesia",
    "publicationCount": 5,
    "createdAt": "2024-10-11T10:00:00"
  }
}
```

---

### 3.3 Buat Kategori (Khusus Admin)

**Endpoint:** `POST /categories`

**Deskripsi:** Membuat kategori publikasi baru.

**Otorisasi:** Memerlukan role Admin

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Request Body:**
```json
{
  "name": "Statistik Industri",
  "description": "Data industri dan manufaktur"
}
```

**Respons Sukses (201 Created):**
```json
{
  "success": true,
  "message": "Category created successfully",
  "data": {
    "id": 6,
    "name": "Statistik Industri",
    "description": "Data industri dan manufaktur",
    "publicationCount": 0,
    "createdAt": "2024-10-11T15:30:00"
  }
}
```

---

### 3.4 Perbarui Kategori (Khusus Admin)

**Endpoint:** `PUT /categories/{id}`

**Deskripsi:** Memperbarui informasi kategori yang sudah ada.

**Otorisasi:** Memerlukan role Admin

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Request Body:**
```json
{
  "name": "Statistik Industri dan Perdagangan",
  "description": "Data industri, manufaktur, dan perdagangan"
}
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Category updated successfully",
  "data": {
    "id": 6,
    "name": "Statistik Industri dan Perdagangan",
    "description": "Data industri, manufaktur, dan perdagangan",
    "publicationCount": 0,
    "createdAt": "2024-10-11T15:30:00"
  }
}
```

---

### 3.5 Hapus Kategori (Khusus Admin)

**Endpoint:** `DELETE /categories/{id}`

**Deskripsi:** Menghapus kategori dari sistem.

**Otorisasi:** Memerlukan role Admin

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Category deleted successfully",
  "data": null
}
```

**Respons Error (400 Bad Request):**
```json
{
  "success": false,
  "message": "Cannot delete category with existing publications",
  "data": null
}
```

---

## 4. Manajemen Publikasi

### 4.1 Upload Publikasi (Khusus Admin)

**Endpoint:** `POST /publications`

**Deskripsi:** Mengunggah file publikasi baru dengan metadata terkait.

**Otorisasi:** Memerlukan role Admin

**Headers:**
```
Authorization: Bearer <admin-token>
Content-Type: multipart/form-data
```

**Request Body (form-data):**
- `title`: Statistik Ekonomi Indonesia 2024
- `description`: Laporan lengkap ekonomi Indonesia tahun 2024
- `categoryId`: 1
- `year`: 2024
- `author`: Badan Pusat Statistik
- `file`: [File PDF atau Excel]

**Aturan Validasi:**
- Tipe file: PDF (.pdf) atau Excel (.xls, .xlsx)
- Ukuran file maksimal: 10 MB
- Semua field wajib diisi kecuali `description` dan `author`

**Respons Sukses (201 Created):**
```json
{
  "success": true,
  "message": "Publication uploaded successfully",
  "data": {
    "id": 1,
    "title": "Statistik Ekonomi Indonesia 2024",
    "description": "Laporan lengkap ekonomi Indonesia tahun 2024",
    "fileName": "a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf",
    "fileUrl": "/api/publications/1/download",
    "fileSize": 2457600,
    "fileSizeFormatted": "2.34 MB",
    "year": 2024,
    "author": "Badan Pusat Statistik",
    "views": 0,
    "downloads": 0,
    "category": {
      "id": 1,
      "name": "Statistik Ekonomi",
      "description": "Data dan publikasi terkait ekonomi Indonesia",
      "publicationCount": null,
      "createdAt": "2024-10-11T10:00:00"
    },
    "uploadedBy": {
      "id": 2,
      "username": "admin_bps",
      "email": "admin@bps.go.id",
      "role": "ADMIN",
      "createdAt": "2024-10-11T09:00:00"
    },
    "createdAt": "2024-10-11T15:45:00",
    "updatedAt": "2024-10-11T15:45:00"
  }
}
```

---

### 4.2 Dapatkan Semua Publikasi

**Endpoint:** `GET /publications`

**Deskripsi:** Mengambil daftar semua publikasi dengan filter opsional.

**Headers:**
```
Authorization: Bearer <token>
```

**Parameter Query (Opsional):**
- `search` - Pencarian berdasarkan judul atau deskripsi
- `categoryId` - Filter berdasarkan ID kategori
- `year` - Filter berdasarkan tahun publikasi

**Contoh Request:**
```
GET /publications
GET /publications?search=ekonomi
GET /publications?categoryId=1
GET /publications?year=2024
GET /publications?categoryId=1&year=2024
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Publications retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Statistik Ekonomi Indonesia 2024",
      "description": "Laporan lengkap ekonomi Indonesia tahun 2024",
      "fileName": "a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf",
      "fileUrl": "/api/publications/1/download",
      "fileSize": 2457600,
      "fileSizeFormatted": "2.34 MB",
      "year": 2024,
      "author": "Badan Pusat Statistik",
      "views": 15,
      "downloads": 5,
      "category": {...},
      "uploadedBy": {...},
      "createdAt": "2024-10-11T15:45:00",
      "updatedAt": "2024-10-11T15:45:00"
    }
  ]
}
```

---

### 4.3 Dapatkan Publikasi Terbaru

**Endpoint:** `GET /publications/latest`

**Deskripsi:** Mengambil 10 dokumen yang paling baru diterbitkan.

**Headers:**
```
Authorization: Bearer <token>
```

**Respons:** Mengembalikan 10 publikasi terbaru (format sama dengan Get All Publications)

---

### 4.4 Dapatkan Publikasi Paling Banyak Diunduh

**Endpoint:** `GET /publications/most-downloaded`

**Deskripsi:** Mengambil 10 publikasi yang paling sering diunduh.

**Headers:**
```
Authorization: Bearer <token>
```

**Respons:** Mengembalikan 10 publikasi paling banyak diunduh (format sama dengan Get All Publications)

---

### 4.5 Dapatkan Publikasi Berdasarkan ID

**Endpoint:** `GET /publications/{id}`

**Deskripsi:** Mengambil informasi detail untuk publikasi tertentu.

**Headers:**
```
Authorization: Bearer <token>
```

**Respons Sukses (200 OK):** Objek publikasi tunggal (format sama dengan respons upload)

**Catatan:** Pemanggilan endpoint ini secara otomatis menambah counter views publikasi.

---

### 4.6 Perbarui Publikasi (Admin atau Pemilik)

**Endpoint:** `PUT /publications/{id}`

**Deskripsi:** Memperbarui metadata publikasi yang sudah ada.

**Otorisasi:** Role Admin atau pemilik publikasi

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Statistik Ekonomi Indonesia 2024 (Revised)",
  "description": "Updated description",
  "categoryId": 1,
  "year": 2024,
  "author": "BPS"
}
```

**Respons Sukses (200 OK):** Objek publikasi yang telah diperbarui

**Aturan Izin:**
- Pengguna Admin dapat memperbarui publikasi apapun
- Pengguna biasa hanya dapat memperbarui publikasi milik mereka sendiri

---

### 4.7 Hapus Publikasi (Admin atau Pemilik)

**Endpoint:** `DELETE /publications/{id}`

**Deskripsi:** Menghapus publikasi dan file terkait secara permanen.

**Otorisasi:** Role Admin atau pemilik publikasi

**Headers:**
```
Authorization: Bearer <token>
```

**Respons Sukses (200 OK):**
```json
{
  "success": true,
  "message": "Publication deleted successfully",
  "data": null
}
```

**Catatan:** Operasi ini juga menghapus file fisik dari server.

---

### 4.8 Download File Publikasi

**Endpoint:** `GET /publications/{id}/download`

**Deskripsi:** Mengunduh file publikasi.

**Headers:**
```
Authorization: Bearer <token>
```

**Respons:** File binary (PDF atau Excel)

**Header Respons:**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="original-filename.pdf"
```

**Catatan:** Pemanggilan endpoint ini secara otomatis menambah counter downloads publikasi.

---

## 🔒 Respons Error

### 401 Unauthorized
```json
{
  "success": false,
  "message": "Unauthorized: Full authentication is required",
  "path": "/api/publications"
}
```

### 403 Forbidden
```json
{
  "success": false,
  "message": "Access Denied",
  "data": null
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Resource not found with id: 123",
  "data": null
}
```

### 400 Bad Request (Validation Error)
```json
{
  "success": false,
  "message": "Validation failed",
  "data": null
}
```

---

## 📊 Kode Status HTTP

| Kode | Deskripsi |
|------|-----------|
| 200  | OK - Request berhasil diselesaikan |
| 201  | Created - Resource berhasil dibuat |
| 400  | Bad Request - Error validasi atau data tidak valid |
| 401  | Unauthorized - Token autentikasi hilang atau tidak valid |
| 403  | Forbidden - Izin tidak mencukupi untuk mengakses resource |
| 404  | Not Found - Resource yang diminta tidak ditemukan |
| 500  | Internal Server Error - Error server yang tidak terduga |

---

## 🛡️ Kontrol Akses Berbasis Role

### Izin Role USER

**Operasi yang Diizinkan:**
- ✅ Registrasi dan login
- ✅ Mengelola profil sendiri (lihat, perbarui, hapus)
- ✅ Melihat semua kategori
- ✅ Melihat, mencari, dan mengunduh publikasi

**Operasi yang Dibatasi:**
- ❌ Membuat, memperbarui, atau menghapus kategori
- ❌ Mengunggah publikasi baru
- ❌ Memperbarui atau menghapus publikasi yang dibuat oleh pengguna lain

### Izin Role ADMIN

**Operasi yang Diizinkan:**
- ✅ Semua izin role USER
- ✅ Membuat, memperbarui, dan menghapus kategori
- ✅ Mengunggah publikasi baru
- ✅ Memperbarui dan menghapus publikasi apapun (termasuk yang dibuat oleh pengguna lain)

---

## 🧪 Panduan Pengujian

### Menggunakan Postman

1. Import koleksi Postman: `BPS-Publikasi-API.postman_collection.json`
2. Buat environment dengan variabel:
    - `baseUrl` = `http://localhost:8080/api`
3. Setelah login berhasil, simpan token yang dikembalikan dalam variabel environment bernama `authToken`
4. Gunakan `{{authToken}}` di header Authorization untuk semua endpoint terproteksi

### Alur Pengujian

1. Daftarkan akun pengguna baru menggunakan `/auth/register`
2. Login dengan kredensial menggunakan `/auth/login` dan simpan token
3. Uji endpoint manajemen profil
4. Uji endpoint pengambilan kategori
5. Uji fungsi pencarian dan download publikasi
6. Untuk operasi admin, pastikan Anda memiliki hak akses admin

---

## 📚 Sumber Tambahan

### URL Dokumentasi

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **API Docs (JSON):** `http://localhost:8080/v3/api-docs`

---

# BÀI TẬP 09: CẤU HÌNH SPRING SECURITY & HỆ THỐNG QUẢN LÝ SHOP (USER, PRODUCT, OTP, CLOUDINARY)
## Môn học: Lập Trình Web (WEBPR330479) - Trường ĐH Công nghệ Kỹ Thuật TP.HCM (HCM-UTE)
**Giảng viên hướng dẫn:** ThS. Nguyễn Hữu Trung  
**Sinh viên thực hiện:** Huỳnh Cao Trung Đức  
**Mã số sinh viên:** 24133012  
**Mã nguồn GitHub:** [https://github.com/duchuynhct/-24133012-HuynhCaoTrungDuc-BaiTap09](https://github.com/duchuynhct/-24133012-HuynhCaoTrungDuc-BaiTap09)

---

## 1. TỔNG QUAN NỘI DUNG DỰ ÁN

Dự án phát triển hoàn chỉnh ứng dụng quản trị và bảo mật Web theo tài liệu hướng dẫn **"HƯỚNG DẪN SPRING BOOT + SECURITY"**:

### 🎯 1. Phân hệ Xác thực & Phân quyền (Authentication & Authorization)
- **Đăng nhập (Login):** Hỗ trợ đăng nhập linh hoạt bằng **Username** hoặc **Email**, lưu session đăng nhập an toàn, quản lý phiên đồng thời (`maximumSessions(1)`).
- **Đăng ký tài khoản (Register):** Nhập thông tin, mã hóa mật khẩu BCrypt, tạo tài khoản ở trạng thái `enabled = false` và gửi mã OTP 6 số qua email.
- **Xác nhận OTP (Verify OTP):** Kiểm tra mã OTP, giới hạn thời gian 5 phút và tối đa 5 lần nhập sai. Sau khi xác nhận thành công, tài khoản chuyển sang `enabled = true`.
- **Gửi lại mã OTP (Resend OTP):** Hỗ trợ gửi lại mã OTP mới nếu mã cũ hết hạn.
- **Quên mật khẩu (Forgot Password):** Gửi mã OTP xác nhận đặt lại mật khẩu qua email.
- **Đặt lại mật khẩu (Reset Password):** Kiểm tra OTP và cập nhật mật khẩu mới mã hóa an toàn.
- **Đăng xuất (Logout):** Hủy session và xóa cookie `JSESSIONID`.

### 🎯 2. Quản lý Người dùng (User Management - Dành cho ADMIN)
- **CRUD User:** Thêm, xem, sửa thông tin, đổi quyền hạn, kích hoạt/vô hiệu hóa, xóa tài khoản.
- **Tìm kiếm & Phân trang:** Tìm kiếm theo từ khóa (username, email, họ tên) kết hợp phân trang dữ liệu.
- **Bảo mật phân quyền:** Chỉ tài khoản có vai trò `ROLE_ADMIN` mới được phép truy cập đường dẫn `/users/**`.
- **Đếm số sản phẩm:** Thống kê số lượng sản phẩm do từng User đăng bán.

### 🎯 3. Quản lý Sản phẩm (Product Management)
- **CRUD Product:** Thêm sản phẩm mới, cập nhật thông tin, xóa sản phẩm.
- **Upload ảnh lên Cloudinary:** Tải ảnh sản phẩm trực tiếp lên dịch vụ Cloudinary, lưu trữ đường dẫn ảnh `secure_url` và `public_id`, tự động xóa ảnh trên Cloudinary khi xóa sản phẩm.
- **Gắn quyền sở hữu:** Mỗi sản phẩm được gắn với User tạo (`1 user - n products`).
- **Tìm kiếm & Phân trang:** Tìm kiếm sản phẩm theo tên hoặc mô tả kết hợp phân trang danh sách.

### 🎯 4. Bảng điều khiển (Dashboard)
- Hiển thị tổng số User và tổng số Product toàn hệ thống.
- Điều hướng nhanh đến các khu vực quản lý theo quyền hạn.

---

## 2. CÔNG NGHỆ SỬ DỤNG

| Thành phần | Công nghệ / Thư viện |
| :--- | :--- |
| **Backend** | Spring Boot 4.1.1 / Spring Framework 7 |
| **Security** | Spring Security 7.1.x / 6.x (SecurityFilterChain, Session Management, CSRF) |
| **Java** | JDK 26 (LTS Compatible) |
| **Database** | Microsoft SQL Server (database: `webst9`) |
| **ORM** | Spring Data JPA / Hibernate ORM |
| **Mapper** | MapStruct 1.6.3 (UserMapper, ProductMapper) |
| **Email** | Spring Boot Starter Mail (JavaMailSender) |
| **Image Hosting** | Cloudinary HTTP5 (2.0.0) |
| **Validation** | Jakarta Validation (`@NotBlank`, `@Email`, `@Size`, `@DecimalMin`,...) |
| **Template Engine** | Thymeleaf, Thymeleaf Extras Spring Security 6, Thymeleaf Layout Dialect |
| **Build Tool** | Apache Maven 3.9.x |

---

## 3. CẤU TRÚC THƯ MỤC DỰ ÁN

```text
-24133012-HuynhCaoTrungDuc-BaiTap09/
├── .gitignore
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── vn/
        │       └── iotstar/
        │           ├── Springboot19Application.java    # Khởi động app & CommandLineRunner nạp seed data
        │           ├── config/
        │           │   ├── CloudinaryConfig.java       # Cấu hình Bean Cloudinary
        │           │   ├── EncodingConfig.java         # Bộ lọc UTF-8
        │           │   └── SecurityConfig.java         # Cấu hình Spring Security (phân quyền URL & Session)
        │           ├── controller/
        │           │   ├── AuthController.java         # Login, Register, OTP, Forgot/Reset Password
        │           │   ├── ErrorController.java        # Xử lý trang báo lỗi
        │           │   ├── HomeController.java         # Dashboard thống kê User & Product
        │           │   ├── ProductController.java      # CRUD Product, Upload Cloudinary, Phân trang
        │           │   └── UserController.java         # CRUD User, Tìm kiếm, Phân trang
        │           ├── dto/
        │           │   ├── ForgotPasswordDTO.java      # Form quên mật khẩu
        │           │   ├── LoginDTO.java               # Form đăng nhập
        │           │   ├── ProductDTO.java             # DTO sản phẩm kèm MultipartFile
        │           │   ├── RegisterDTO.java            # Form đăng ký
        │           │   ├── ResetPasswordDTO.java       # Form đặt lại mật khẩu
        │           │   ├── UserDTO.java                # DTO người dùng
        │           │   └── VerifyOtpDTO.java           # Form xác thực mã OTP
        │           ├── entity/
        │           │   ├── OtpToken.java               # Bảng otp_tokens
        │           │   ├── Product.java                # Bảng products
        │           │   ├── Role.java                   # Bảng roles
        │           │   └── User.java                   # Bảng users
        │           ├── mapper/
        │           │   ├── ProductMapper.java          # Ánh xạ Product <-> ProductDTO
        │           │   └── UserMapper.java             # Ánh xạ User <-> UserDTO
        │           ├── repository/
        │           │   ├── OtpTokenRepository.java     # Thao tác bảng otp_tokens
        │           │   ├── ProductRepository.java      # Tìm kiếm, phân trang, đếm products
        │           │   ├── RoleRepository.java         # Tìm kiếm role
        │           │   └── UserRepository.java         # Tìm kiếm, phân trang users
        │           ├── security/
        │           │   ├── CustomUserDetails.java      # Triển khai UserDetails mở rộng
        │           │   └── CustomUserDetailsService.java # Load user qua findByUsernameOrEmail
        │           └── service/
        │               ├── AuthService.java            # Interface xác thực & OTP
        │               ├── CloudinaryService.java       # Interface upload/xóa ảnh Cloudinary
        │               ├── CloudinaryUploadResult.java # Record kết quả upload
        │               ├── EmailService.java            # Interface gửi email OTP
        │               ├── OtpService.java              # Interface quản lý mã OTP
        │               ├── ProductService.java          # Interface nghiệp vụ sản phẩm
        │               ├── UserService.java             # Interface nghiệp vụ người dùng
        │               └── impl/
        │                   ├── AuthServiceImpl.java
        │                   ├── CloudinaryServiceImpl.java
        │                   ├── EmailServiceImpl.java
        │                   ├── OtpServiceImpl.java
        │                   ├── ProductServiceImpl.java
        │                   └── UserServiceImpl.java
        └── resources/
            ├── application.properties                  # Cấu hình CSDL SQL Server, Mail, Cloudinary
            ├── static/
            │   ├── css/
            │   │   └── app.css                         # Toàn bộ CSS giao diện theo mẫu bài giảng
            │   └── images/
            │       ├── admin.png
            │       ├── user.png
            │       └── avatar-default.png
            └── templates/
                ├── auth/
                │   ├── forgot-password.html
                │   ├── login.html
                │   ├── register.html
                │   ├── reset-password.html
                │   └── verify-otp.html
                ├── fragments/
                │   ├── footer.html
                │   └── header.html
                ├── layouts/
                │   └── layout.html
                ├── products/
                │   ├── form.html
                │   └── list.html
                ├── users/
                │   ├── form.html
                │   └── list.html
                ├── error.html
                └── home.html                           # Dashboard
```

---

## 4. TÀI KHOẢN THỬ NGHIỆM (SEED DATA)

Khi ứng dụng khởi chạy lần đầu, `CommandLineRunner` trong `Springboot19Application` sẽ tự động tạo dữ liệu mẫu trong CSDL SQL Server `webst9`:

| Username | Email | Mật khẩu | Quyền (Role) | Họ và tên | Trạng thái |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `admin` | `admin@hcmute.edu.vn` | `123456` | `ROLE_ADMIN` | System Administrator | ACTIVE |
| `user01` | `user01@gmail.com` | `123456` | `ROLE_USER` | Huỳnh Cao Trung Đức | ACTIVE |

*Đồng thời tự động tạo 2 sản phẩm mẫu cho `user01`:*
- **Điện thoại Oppo A95** (Giá: 6,565,656.00 VNĐ)
- **Điện thoại Oppo A6** (Giá: 689,990.00 VNĐ)

---

## 5. HƯỚNG DẪN KHỞI CHẠY & KIỂM THỬ

### Bước 1: Chuẩn bị CSDL
Đảm bảo dịch vụ SQL Server đang chạy (`localhost:1433`). Tạo database:
```sql
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'webst9')
CREATE DATABASE webst9;
```

### Bước 2: Chạy ứng dụng
Mở terminal tại thư mục dự án và chạy:
```powershell
$env:JAVA_HOME = "C:\Java\jdk-26.0.2.1"
mvn clean package -DskipTests
java -jar target/springboot1-9-1.0.jar
```
*(Ứng dụng khởi chạy trên cổng **8081**)*

### Bước 3: Kiểm thử các chức năng trên trình duyệt
1. **Đăng nhập:** Truy cập `http://localhost:8081/login`
   - Đăng nhập với `admin` / `123456` $\rightarrow$ Truy cập được cả `/products` và `/users`.
   - Đăng nhập với `user01` / `123456` $\rightarrow$ Truy cập được `/products`, bị chặn khi vào `/users`.
2. **Đăng ký & Xác nhận OTP:** Truy cập `http://localhost:8081/register`
   - Điền thông tin đăng ký $\rightarrow$ Hệ thống sinh mã OTP 6 số và gửi mail (mã OTP đồng thời hiển thị tại console server để tiện kiểm thử).
   - Nhập OTP tại `/verify-otp` $\rightarrow$ Kích hoạt tài khoản thành công.
3. **Quên mật khẩu:** Truy cập `http://localhost:8081/forgot-password`
   - Nhập email $\rightarrow$ Nhận OTP $\rightarrow$ Nhập mật khẩu mới tại `/reset-password`.
4. **Quản lý Users (Admin):** `http://localhost:8081/users`
   - Tìm kiếm người dùng theo từ khóa, phân trang, thêm mới, chỉnh sửa và xóa người dùng.
5. **Quản lý Products:** `http://localhost:8081/products`
   - Tìm kiếm sản phẩm, phân trang, thêm sản phẩm mới kèm tải ảnh Cloudinary, chỉnh sửa và xóa.

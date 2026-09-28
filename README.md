# BÀI TẬP 09: CẤU HÌNH SPRING SECURITY 6 & CUSTOM LOGIN
## Môn học: Lập Trình Web (WEBPR330479) - Trường ĐH Sư Phạm Kỹ Thuật TP.HCM (HCMUTE)
**Giảng viên hướng dẫn:** ThS. Nguyễn Hữu Trung  
**Sinh viên thực hiện:** Huỳnh Cao Trung Đức  
**Mã số sinh viên:** 24133012  
**Mã nguồn GitHub:** [https://github.com/duchuynhct/-24133012-HuynhCaoTrungDuc-BaiTap09](https://github.com/duchuynhct/-24133012-HuynhCaoTrungDuc-BaiTap09)

---

## 1. TỔNG QUAN NỘI DUNG BÀI LÀM

Dự án tích hợp hoàn chỉnh cả 2 ví dụ trong tài liệu giảng dạy vào **1 Project Spring Boot duy nhất**:

### 🎯 Ví dụ 1: Custom Login (Layout KHÔNG dùng Dialect)
- Cho bảng `User`, `Role`.
- Chức năng đăng nhập bằng `username` hoặc `email` đều được.
- Thông tin người dùng (`fullName`, `role`) hiển thị trên thanh `header.html`.
- Sử dụng **MapStruct** (`UserMapper`) để ánh xạ Entity $\leftrightarrow$ DTO.
- Giao diện Thymeleaf thuần, ghép template bằng cơ chế `th:replace` chuẩn (không dùng thư viện Dialect).
- Đường dẫn truy cập kiểm thử: `http://localhost:8081/vidu1`

### 🎯 Ví dụ 2: Custom Login (Thymeleaf Layout Dialect + Avatar)
- Mở rộng chức năng đăng nhập linh hoạt bằng **Username HOẶC Email**.
- Hiển thị đầy đủ **Avatar tròn (`images`), Họ và tên (`fullName`), Username, Email, Quyền (`role`)** trên thanh `header.html`.
- Sử dụng **Thymeleaf Layout Dialect** (`nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect` với cú pháp `xmlns:layout`, `layout:decorate="~{layouts/layout}"`, `<main layout:fragment="content">`).
- Đối tượng `CustomUserDetails` lưu giữ trực tiếp thông tin vào Security Context.
- Đường dẫn truy cập kiểm thử: `http://localhost:8081/`

---

## 2. CÔNG NGHỆ & MÔI TRƯỜNG SỬ DỤNG

| Công nghệ | Phiên bản / Chi tiết |
| :--- | :--- |
| **Java SDK** | Java 21 / 25 / 26 (LTS) |
| **Spring Boot** | 4.1.1 / 3.x (Spring Security 6/7, Spring Data JPA, Spring Web, Validation) |
| **Database** | Microsoft SQL Server (database: `webst9`) |
| **ORM / DDL** | Hibernate ORM 7.x (`ddl-auto: update`) |
| **Mapper** | MapStruct 1.6.3 |
| **Template Engine** | Thymeleaf, Thymeleaf Extras Spring Security 6, Thymeleaf Layout Dialect 4.0.1 |
| **Build Tool** | Apache Maven 3.9.x |

---

## 3. CẤU TRÚC MÃ NGUỒN DỰ ÁN

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
        │           ├── Springboot19Application.java    # Khởi động app & CommandLineRunner nạp dữ liệu mẫu
        │           ├── config/
        │           │   ├── EncodingConfig.java         # Bộ lọc UTF-8
        │           │   └── SecurityConfig.java         # Cấu hình Spring Security 6 (SecurityFilterChain)
        │           ├── controller/
        │           │   ├── AuthController.java         # Điều hướng /login
        │           │   └── HomeController.java         # Điều hướng / (Ví dụ 2) và /vidu1 (Ví dụ 1)
        │           ├── dto/
        │           │   ├── LoginDTO.java               # DTO nhận form đăng nhập
        │           │   └── UserDTO.java                # DTO trả về thông tin User
        │           ├── entity/
        │           │   ├── Role.java                   # Entity roles (id, name)
        │           │   └── User.java                   # Entity users (id, username, email, password,...)
        │           ├── mapper/
        │           │   └── UserMapper.java             # MapStruct interface ánh xạ User <-> UserDTO
        │           ├── repository/
        │           │   ├── RoleRepository.java         # JpaRepository Role
        │           │   └── UserRepository.java         # JpaRepository User (findByUsernameOrEmail)
        │           └── security/
        │               ├── CustomUserDetails.java      # UserDetails mở rộng (fullName, images, email,...)
        │               └── CustomUserDetailsService.java # Tải User qua findByUsernameOrEmail
        └── resources/
            ├── application.properties                  # Cấu hình SQL Server & port 8081
            ├── static/
            │   ├── css/
            │   │   └── app.css                         # CSS giao diện hiện đại theo bài giảng
            │   └── images/
            │       ├── admin.png                       # Avatar tài khoản admin
            │       ├── user.png                        # Avatar tài khoản user01
            │       └── avatar-default.png              # Avatar mặc định
            └── templates/
                ├── auth/
                │   └── login.html                      # Form đăng nhập chung
                ├── layouts/
                │   └── layout.html                     # Layout template Ví dụ 2 (Layout Dialect)
                ├── fragments/
                │   └── header.html                     # Topbar Ví dụ 2 (Avatar + Fullname + Email + Role)
                ├── home.html                           # Trang chủ Ví dụ 2 (layout:decorate)
                └── vidu1/                              # Phân vùng độc lập cho Ví dụ 1
                    ├── layouts/
                    │   └── layout_vidu1.html           # Layout Ví dụ 1 (th:fragment="head")
                    ├── fragments/
                    │   └── header_vidu1.html           # Header Ví dụ 1 (th:replace chuẩn)
                    └── home_vidu1.html                 # Trang chủ Ví dụ 1
```

---

## 4. TÀI KHOẢN THỬ NGHIỆM (SEED DATA)

Khi ứng dụng khởi động lần đầu, `CommandLineRunner` trong `Springboot19Application` sẽ tự động tạo các quyền và 2 tài khoản mẫu trong CSDL SQL Server `webst9`:

| Username | Email | Mật khẩu | Quyền (Role) | Họ và tên | Ảnh đại diện (Avatar) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `user01` | `user01@gmail.com` | `123456` | `ROLE_USER` | Huỳnh Cao Trung Đức | `/images/user.png` |
| `admin` | `admin@hcmute.edu.vn` | `123456` | `ROLE_ADMIN` | System Administrator | `/images/admin.png` |

> **Lưu ý:** Bạn có thể đăng nhập bằng **Username** (ví dụ: `user01`) hoặc **Email** (ví dụ: `user01@gmail.com`) với mật khẩu `123456` đều thành công.

---

## 5. HƯỚNG DẪN CÀI ĐẶT & CHẠY ỨNG DỤNG

### Bước 1: Chuẩn bị CSDL SQL Server
Đảm bảo dịch vụ SQL Server đang chạy (`localhost:1433`). Mở SQL Server Management Studio (SSMS) hoặc chạy lệnh sau để tạo database:
```sql
CREATE DATABASE webst9;
```

Cấu hình kết nối trong `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=webst9;encrypt=true;trustServerCertificate=true;sendStringParametersAsUnicode=true;
spring.datasource.username=sa
spring.datasource.password=123456
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### Bước 2: Biên dịch và chạy ứng dụng
Mở terminal tại thư mục dự án và chạy:
```bash
mvn clean spring-boot:run
```
Hoặc đóng gói file `.jar` và chạy:
```bash
mvn clean package -DskipTests
java -jar target/springboot1-9-1.0.jar
```

### Bước 3: Trải nghiệm ứng dụng trên trình duyệt
1. **Trang đăng nhập:** `http://localhost:8081/login`
   - Đăng nhập thử với `user01` / `123456` hoặc `user01@gmail.com` / `123456`.
   - Đăng nhập thử với tài khoản sai mật khẩu $\rightarrow$ Hiển thị thông báo đỏ: *"Username/email hoặc password không đúng"*.
2. **Trang chủ Ví dụ 2 (Thymeleaf Layout Dialect + Avatar):** `http://localhost:8081/`
   - Quan sát trên thanh Topbar hiển thị: **Avatar tròn**, Full Name (*Nguyễn Hữu Trung*), Username (*user01*), Email, Badge Role (*ROLE_USER*) và nút *Đăng xuất*.
3. **Trang chủ Ví dụ 1 (Thymeleaf không Dialect):** `http://localhost:8081/vidu1`
   - Hiển thị theo đúng chuẩn `th:replace` truyền thống không dùng Layout Dialect.
4. **Đăng xuất:** Bấm nút **"Đăng xuất"** trên Header $\rightarrow$ Điều hướng về `/login?logout=true` kèm thông báo *"Bạn đã đăng xuất thành công"*.

---

## 6. KẾT QUẢ KIỂM THỬ

- [x] Tự động sinh bảng `roles`, `users` trên SQL Server với các khóa chính, khóa ngoại, unique constraint.
- [x] Tự động nạp tài khoản mẫu đã mã hóa BCrypt.
- [x] Xác thực người dùng bằng **Username** hoặc **Email** linh hoạt.
- [x] MapStruct chuyển đổi DTO sang Entity và ngược lại chính xác.
- [x] Cơ chế phân quyền Spring Security 6 hoạt động ổn định.
- [x] Giao diện Ví dụ 1 (Không Dialect) hiển thị chuẩn xác.
- [x] Giao diện Ví dụ 2 (Layout Dialect) hiển thị đầy đủ Avatar, Fullname, Email, Role.
- [x] Chức năng Logout xóa session và cookie `JSESSIONID` an toàn.

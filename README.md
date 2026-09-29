| Đường dẫn (URL) | Chức năng | Mô tả chi tiết |
| :--- | :--- | :--- |
| **`http://localhost:8080/auth/signup`** | **Đăng ký** | Nhập Họ tên, Email, Mật khẩu để tạo tài khoản mới. Khi thành công sẽ chuyển sang trang Đăng nhập. |
| **`http://localhost:8080/auth/login`** | **Đăng nhập** | Nhập Email và Mật khẩu. Khi thành công, hệ thống lưu JWT Token vào `localStorage` và chuyển đến `/user/me`. |
| **`http://localhost:8080/user/me`** | **Hồ sơ cá nhân** | Tự động đọc Token để gọi API `/users/me`. Hiển thị Avatar, ID, Email, Họ tên, Ngày tạo, Token và nút Sao chép. |
| **`http://localhost:8080/user`** | **Danh sách người dùng** | Sử dụng Token để gọi API `/users`. Hiển thị bảng toàn bộ người dùng đã đăng ký trong hệ thống. |

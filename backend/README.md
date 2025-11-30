# Backend - Product Module

Hướng dẫn chạy test, sinh báo cáo coverage (JaCoCo) và cách chụp hình evidence cho bài nộp.

## Yêu cầu môi trường
- JDK 21
- Windows PowerShell (mặc định của repo)

## Chạy test
```powershell
# Từ thư mục backend
.\mvnw.cmd test
```
Kết quả mong đợi: tất cả test xanh.

## Sinh báo cáo Coverage (JaCoCo)
```powershell
# Từ thư mục backend
.\mvnw.cmd clean verify
```
- Báo cáo HTML sẽ nằm tại: `target\site\jacoco\index.html`
- Mặc định build sẽ FAIL nếu coverage < 80% (instructions) hoặc < 70% (branches).

## Mở nhanh báo cáo trên Windows
```powershell
# Mở thư mục chứa report
explorer "$(Resolve-Path .\target\site\jacoco)"
# Hoặc mở trực tiếp file index.html bằng trình duyệt mặc định
Start-Process "$(Resolve-Path .\target\site\jacoco\index.html)"
```

## Cách chụp ảnh màn hình (evidence)
1. Mở `target\site\jacoco\index.html` như hướng dẫn trên.
2. Chụp màn hình phần tổng quan (`Coverage Summary`) hiển thị tỉ lệ %.
3. Lưu ảnh vào thư mục `backend\docs\` (tự tạo nếu chưa có). Đặt tên gợi nhớ, ví dụ: `jacoco-summary.png`.

## Coverage hiện tại (tham chiếu nhanh)
Đọc từ `target/site/jacoco/jacoco.csv` sau lần chạy gần nhất:
- `ProductService`: INSTRUCTION covered 183, missed 0; BRANCH covered 23, missed 3; LINE covered 28, missed 0.
- `ProductController`: INSTRUCTION covered 55, missed 0; LINE covered 9, missed 0.
- `GlobalExceptionHandler`: INSTRUCTION covered 71, missed 15; BRANCH covered 1, missed 1; LINE covered 14, missed 3.
- `Product` entity: INSTRUCTION covered 21, missed 0; LINE covered 8, missed 0.

Lưu ý: số liệu có thể thay đổi theo lần chạy; hãy mở báo cáo HTML để xem tổng quan chính xác.

## API Endpoints (tóm tắt)
- `POST /api/products` – tạo mới (201). Validate dữ liệu, chặn tên trùng, trần giá.
- `GET /api/products/{id}` – lấy chi tiết (200). 404 nếu không tồn tại.
- `GET /api/products?page=&size=&search=` – trả về `PagedResponse<T>` gồm `data`, `page`, `size`, `totalElements`, `totalPages`.
- `PUT /api/products/{id}` – cập nhật (200). Validate + tên trùng.
- `DELETE /api/products/{id}` – xóa (204). 404 nếu không tồn tại.

## Troubleshooting nhanh
- Không thấy báo cáo: đảm bảo chạy `clean verify` (không chỉ `jacoco:report`).
- Coverage rule làm fail build: mở `pom.xml` và điều chỉnh ngưỡng trong plugin `jacoco-maven-plugin`.

# Yêu cầu để làm phần quản lý mảng

Mình phụ trách bốn lớp: `QuanLyVe`, `QuanLyHanhKhach`, `QuanLyDaiLy`, `QuanLyChuyenBay`. Để viết các hàm tìm, sửa, xóa và thống kê, cần chốt tên thuộc tính và phương thức của các lớp dữ liệu.

## 1. Mã định danh

- Mỗi vé, hành khách, đại lý, chuyến bay có một mã kiểu `String`, không rỗng và không trùng trong danh sách cùng loại.
- Đề xuất định dạng: `VE0001`, `HK0001`, `DL0001`, `CB0001`. Có thể dùng định dạng khác nếu cả nhóm thống nhất. Không cần viết sẵn các mã cụ thể trong code.
- Khi tìm kiếm, mã nhập từ bàn phím sẽ được so sánh với mã lưu trong đối tượng. Nhóm chốt việc có phân biệt chữ hoa/thường hay không; đề xuất **không phân biệt**.

## 2. Phương thức cần có

| Lớp | Phương thức | Mục đích |
| --- | --- | --- |
| `VeMayBay` | `String getMaVe()` | Tìm, kiểm tra trùng, sửa, xóa vé theo mã |
| `VeMayBay` | `HanhKhach getHanhKhach()` | Tìm/thống kê vé theo hành khách |
| `VeMayBay` | `ChuyenBay getChuyenBay()` | Tìm/thống kê vé theo chuyến bay |
| `HanhKhach` | `String getMaHanhKhach()` | Tìm, kiểm tra trùng, sửa, xóa hành khách |
| `HanhKhach` | `String getHoTen()` | Tìm gần đúng theo tên |
| `DaiLy` | `String getMaDaiLy()` | Tìm, kiểm tra trùng, sửa, xóa đại lý |
| `DaiLy` | `String getTenDaiLy()` | Tìm gần đúng theo tên |
| `ChuyenBay` | `String getMaChuyenBay()` | Tìm, kiểm tra trùng, sửa, xóa chuyến bay |

Các lớp trên cần constructor để tạo đối tượng có mã và `toString()` để in danh sách. `VePhoThong` và `VeThuongGia` nhận mã vé qua constructor rồi chuyển lên lớp cha `VeMayBay`. Nếu đã dùng tên phương thức khác, gửi mình **tên và kiểu trả về chính xác**.

## 3. Quy tắc cần chốt trước khi viết hàm sửa/xóa

- Thuộc tính nào được sửa? Cần tên setter tương ứng. Đề xuất không sửa mã định danh sau khi đã tạo.
- Khi xóa hành khách hoặc chuyến bay đã có vé, chương trình từ chối xóa hay xóa cả vé liên quan?
- `DaiLy.java` chưa có trong bộ khung. Người phụ trách lớp này cần tạo lớp và hai getter ở bảng trên.

## 4. Ranh giới công việc

- Phần của mình: quản lý mảng đối tượng, `soLuong`, thêm, xóa bằng dồn mảng, tìm chính xác/gần đúng, thống kê. Không dùng `ArrayList`, `Vector`, `LinkedList`.
- Phần nhập liệu/menu: nhập thông tin, tạo từng đối tượng, gọi hàm `them...()` trong lớp quản lý. Nếu nhập `n` đối tượng thì lặp `n` lần.
- Phần đọc/ghi file: khi đọc, tạo đối tượng rồi gọi hàm `them...()`; khi ghi, lấy số lượng và từng đối tượng từ lớp quản lý. Mình sẽ cung cấp hàm lấy đối tượng theo vị trí.
- Tên lớp xử lý file trong phân công là `XuLyFile`, còn bộ khung hiện là `LuuDuLieu`. Cần chọn một tên trước khi ghép code.

Chốt các tên phương thức ở mục 2 trước khi viết phần tìm kiếm để tránh sửa lại code khi ghép nhóm.

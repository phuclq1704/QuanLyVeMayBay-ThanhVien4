import java.util.Scanner;

/** Quản lý danh sách đơn đặt vé bằng mảng đối tượng DonDatVe[]. */
public class QuanLyDonDatVe {
    private DonDatVe[] danhSachDon;
    private int soLuongDon;
    private int soDonTiepTheo;
    private int soVeTiepTheo;
    private String thongBaoGanNhat;
    private static final Scanner INPUT = new Scanner(System.in);

    public QuanLyDonDatVe() {
        danhSachDon = new DonDatVe[100];
        soLuongDon = 0;
        soDonTiepTheo = 1;
        soVeTiepTheo = 1;
        thongBaoGanNhat = "";
    }

    /** Nhập n đơn từ bàn phím; mỗi đơn tự tạo chuyến bay và hồ sơ hành khách. */
    public void nhapDanhSach(int n) {
        if (n < 0) {
            System.out.println("Số đơn cần nhập không được âm.");
            return;
        }
        for (int i = 0; i < n; i++) {
            System.out.println("\nNhập đơn thứ " + (i + 1) + "/" + n + ":");
            String maHanhKhach = nhapChuoi("Mã hành khách: ");
            String hoTen = nhapChuoi("Họ tên: ");
            String dienThoai = nhapChuoi("Số điện thoại: ");
            String hoChieu = nhapChuoi("Số hộ chiếu: ");
            String maChuyenBay = nhapChuoi("Mã chuyến bay: ");
            String diemDi = nhapChuoi("Điểm đi: ");
            String diemDen = nhapChuoi("Điểm đến: ");
            String ngayDi = nhapChuoi("Ngày đi: ");
            String ngayDen = nhapChuoi("Ngày đến: ");
            Integer tongGhe = nhapSoNguyen("Tổng số ghế: ");
            Integer loaiVe = nhapSoNguyen("Loại vé (1. Phổ thông, 2. Thương gia): ");
            Double giaGoc = nhapSoThuc("Giá gốc (VND): ");
            Double phuPhi = nhapSoThuc("Phụ phí (VND): ");
            if (maHanhKhach == null || hoTen == null || dienThoai == null || hoChieu == null
                    || maChuyenBay == null || diemDi == null || diemDen == null || ngayDi == null
                    || ngayDen == null || tongGhe == null || loaiVe == null
                    || giaGoc == null || phuPhi == null) {
                return;
            }

            HanhKhach hanhKhach = new HanhKhach(true, maHanhKhach, hoTen, dienThoai, hoChieu);
            ChuyenBay chuyenBay = new ChuyenBay(true, maChuyenBay, diemDi, diemDen,
                    ngayDi, ngayDen, tongGhe, tongGhe);
            IThanhToan thanhToan = nhapThanhToan();
            if (thanhToan == null) {
                return;
            }
            String tenPhuongThuc = thanhToan instanceof ThanhToanThe ? "Thẻ" : "Tiền mặt";
            DonDatVe don = datVeTuDong(hanhKhach, chuyenBay, loaiVe,
                    giaGoc, phuPhi, thanhToan, tenPhuongThuc);
            if (don == null) {
                System.out.println(thongBaoGanNhat);
            } else {
                System.out.println("Đã tạo đơn " + don.getMaDonDat() + ".");
            }
        }
    }

    /** Tạo đơn với mã đơn và mã vé tự sinh sau khi thanh toán thành công. */
    public DonDatVe datVeTuDong(HanhKhach hanhKhach, ChuyenBay chuyenBay,
                                int loaiVe, double giaGoc, double phuPhi,
                                IThanhToan thanhToan, String tenPhuongThuc) {
        String maDonDat = taoMaDonTiepTheo();
        String maVe = taoMaVeTiepTheo();
        return datVe(maDonDat, maVe, hanhKhach, chuyenBay, loaiVe,
                giaGoc, phuPhi, thanhToan, tenPhuongThuc);
    }

    /** Đặt vé; ghế chỉ được giữ nếu thanh toán thành công. */
    public DonDatVe datVe(String maDonDat, String maVe, HanhKhach hanhKhach,
                          ChuyenBay chuyenBay, int loaiVe, double giaGoc,
                          double phuPhi, IThanhToan thanhToan,
                          String tenPhuongThuc) {
        thongBaoGanNhat = "";
        if (maDonDat == null || maDonDat.trim().isEmpty()
                || maVe == null || maVe.trim().isEmpty()) {
            return loi("Mã đơn và mã vé không được để trống.");
        }
        if (hanhKhach == null || chuyenBay == null || thanhToan == null) {
            return loi("Cần có hành khách, chuyến bay và phương thức thanh toán.");
        }
        if (!hanhKhach.isStatus()) {
            return loi("Hành khách đã ngừng hoạt động.");
        }
        if (loaiVe != 1 && loaiVe != 2) {
            return loi("Loại vé không hợp lệ.");
        }
        if (!Double.isFinite(giaGoc) || !Double.isFinite(phuPhi)
                || giaGoc <= 0 || phuPhi < 0) {
            return loi("Giá vé phải lớn hơn 0 và phụ phí không được âm.");
        }
        if (timDonTheoMa(maDonDat) != null || daCoMaVe(maVe)) {
            return loi("Mã đơn hoặc mã vé đã được sử dụng.");
        }

        String maDonSach = maDonDat.trim();
        String maVeSach = maVe.trim();
        VeMayBay ve = loaiVe == 1
                ? new VePhoThong(maVeSach, giaGoc, phuPhi)
                : new VeThuongGia(maVeSach, giaGoc, phuPhi);
        double tongTien = ve.tinhGiaVe();
        if (!Double.isFinite(tongTien) || tongTien <= 0) {
            return loi("Tổng tiền vé không hợp lệ.");
        }
        if (!chuyenBay.DatGhe()) {
            return loi("Chuyến bay đã hết ghế.");
        }

        boolean daThanhToan;
        try {
            daThanhToan = thanhToan.thanhToan(tongTien);
        } catch (RuntimeException e) {
            chuyenBay.HuyGhe();
            return loi("Không thể thực hiện thanh toán: " + e.getMessage());
        }
        if (!daThanhToan) {
            chuyenBay.HuyGhe();
            return loi("Thanh toán không thành công; ghế đã được trả lại.");
        }

        DonDatVe don = new DonDatVe(maDonSach, hanhKhach, hanhKhach, null,
                chuyenBay, ve, thanhToan, ngayHomNay());
        don.xacNhanDatVe();
        if (!luuDonNoiBo(don)) {
            chuyenBay.HuyGhe();
            return loi("Không thể lưu đơn; ghế đã được trả lại.");
        }
        thongBaoGanNhat = "Đặt vé thành công.";
        return don;
    }

    /** Thêm một đơn đã tạo sẵn theo chữ ký trong sơ đồ lớp. */
    public void themDon(DonDatVe don) {
        if (!luuDonNoiBo(don)) {
            thongBaoGanNhat = "Không thêm được đơn: dữ liệu không hợp lệ hoặc mã đã tồn tại.";
        }
    }

    /** Sửa tên, điện thoại hoặc hộ chiếu của hành khách trong đơn đã chọn. */
    public boolean suaDon(String maDon) {
        DonDatVe don = timDonTheoMa(maDon);
        if (don == null || don.isDaHuy() || don.getHanhKhach() == null) {
            return false;
        }
        HanhKhach cu = don.getHanhKhach();
        System.out.println("Nhấn Enter nếu muốn giữ nguyên thông tin hiện tại.");
        String hoTen = nhapChuoi("Họ tên mới [" + cu.getHoTen() + "]: ");
        String dienThoai = nhapChuoi("Số điện thoại mới [" + cu.getSoDienThoai() + "]: ");
        String hoChieu = nhapChuoi("Số hộ chiếu mới [" + cu.getHochieu() + "]: ");
        if (hoTen == null || dienThoai == null || hoChieu == null) {
            return false;
        }
        HanhKhach moi = new HanhKhach(cu.isStatus(), cu.getMaSo(),
                hoTen.isEmpty() ? cu.getHoTen() : hoTen,
                dienThoai.isEmpty() ? cu.getSoDienThoai() : dienThoai,
                hoChieu.isEmpty() ? cu.getHochieu() : hoChieu);
        don.setHanhKhach(moi);
        return true;
    }

    /** Xóa hẳn đơn khỏi mảng; đơn còn hiệu lực sẽ trả ghế trước khi xóa. */
    public boolean xoaDon(String maDon) {
        int viTri = timViTriDonTheoMa(maDon);
        if (viTri < 0) {
            return false;
        }
        DonDatVe don = danhSachDon[viTri];
        if (!don.isDaHuy() && don.getChuyenBay() != null
                && !don.getChuyenBay().HuyGhe()) {
            return false;
        }
        for (int i = viTri; i < soLuongDon - 1; i++) {
            danhSachDon[i] = danhSachDon[i + 1];
        }
        danhSachDon[--soLuongDon] = null;
        return true;
    }

    /** Hủy đơn nhưng vẫn lưu lại để tra cứu lịch sử. */
    public boolean huyDonTheoMa(String maDonDat) {
        DonDatVe don = timDonTheoMa(maDonDat);
        if (don == null || don.isDaHuy() || don.getChuyenBay() == null) {
            return false;
        }
        if (!don.getChuyenBay().HuyGhe()) {
            return false;
        }
        don.setDaHuy(true);
        return true;
    }

    public DonDatVe timDonTheoMa(String maDonDat) {
        int viTri = timViTriDonTheoMa(maDonDat);
        return viTri < 0 ? null : danhSachDon[viTri];
    }

    public int demDonTheoMaChuyenBay(String maChuyenBay) {
        return demDonTheoMaChuyenBay(maChuyenBay, false);
    }

    public int demDonConHieuLucTheoMaChuyenBay(String maChuyenBay) {
        return demDonTheoMaChuyenBay(maChuyenBay, true);
    }

    private int demDonTheoMaChuyenBay(String maChuyenBay, boolean chiConHieuLuc) {
        if (maChuyenBay == null || maChuyenBay.trim().isEmpty()) {
            return 0;
        }
        int dem = 0;
        for (int i = 0; i < soLuongDon; i++) {
            DonDatVe don = danhSachDon[i];
            ChuyenBay chuyenBay = don.getChuyenBay();
            String maCuaChuyenBay = chuyenBay == null ? null : chuyenBay.getSoHieuCHuyenBay();
            if (maCuaChuyenBay != null
                    && (!chiConHieuLuc || !don.isDaHuy())
                    && maCuaChuyenBay.equalsIgnoreCase(maChuyenBay.trim())) {
                dem++;
            }
        }
        return dem;
    }

    /** Hàm thống kê doanh thu theo đúng tên trong sơ đồ lớp. */
    public double thongKeDoanhThu() {
        double doanhThu = 0;
        for (int i = 0; i < soLuongDon; i++) {
            if (!danhSachDon[i].isDaHuy()) {
                doanhThu += danhSachDon[i].getSoTienThanhToan();
            }
        }
        return doanhThu;
    }

    /** Giữ tên hàm cũ để giao diện hiện tại tiếp tục sử dụng được. */
    public double tinhDoanhThu() {
        return thongKeDoanhThu();
    }

    public void inBaoCaoDoanhThu() {
        int soDonHieuLuc = 0;
        for (int i = 0; i < soLuongDon; i++) {
            if (!danhSachDon[i].isDaHuy()) {
                soDonHieuLuc++;
            }
        }
        System.out.println("\n===== BÁO CÁO DOANH THU =====");
        System.out.println("Số đơn còn hiệu lực: " + soDonHieuLuc);
        System.out.printf("Tổng doanh thu: %,.0f VND%n", thongKeDoanhThu());

        String[] maChuyenBay = new String[soLuongDon];
        double[] doanhThuTheoChuyen = new double[soLuongDon];
        int soChuyen = 0;
        for (int i = 0; i < soLuongDon; i++) {
            DonDatVe don = danhSachDon[i];
            if (don.isDaHuy() || don.getChuyenBay() == null) {
                continue;
            }
            String ma = don.getChuyenBay().getSoHieuCHuyenBay();
            if (ma == null || ma.trim().isEmpty()) {
                ma = "(không có mã)";
            }
            int viTri = 0;
            while (viTri < soChuyen && !maChuyenBay[viTri].equalsIgnoreCase(ma)) {
                viTri++;
            }
            if (viTri == soChuyen) {
                maChuyenBay[soChuyen] = ma;
                soChuyen++;
            }
            doanhThuTheoChuyen[viTri] += don.getSoTienThanhToan();
        }
        for (int i = 0; i < soChuyen; i++) {
            System.out.printf("- %s: %,.0f VND%n", maChuyenBay[i], doanhThuTheoChuyen[i]);
        }
        System.out.println("=============================\n");
    }

    public void xuatDanhSach() {
        if (soLuongDon == 0) {
            System.out.println("Danh sách đơn đặt vé trống.");
            return;
        }
        for (int i = 0; i < soLuongDon; i++) {
            System.out.print((i + 1) + ". ");
            danhSachDon[i].hienThiThongTin();
        }
    }

    public DonDatVe[] getDanhSach() {
        DonDatVe[] ketQua = new DonDatVe[soLuongDon];
        System.arraycopy(danhSachDon, 0, ketQua, 0, soLuongDon);
        return ketQua;
    }

    public int getSoLuong() {
        return soLuongDon;
    }

    public String getThongBaoGanNhat() {
        return thongBaoGanNhat;
    }

    private boolean luuDonNoiBo(DonDatVe don) {
        if (don == null || don.getMaDonDat() == null || don.getMaDonDat().trim().isEmpty()
                || don.getVe() == null || don.getVe().getMaVe() == null
                || don.getVe().getMaVe().trim().isEmpty() || don.getHanhKhach() == null
                || don.getChuyenBay() == null) {
            return false;
        }
        String maHanhKhach = don.getHanhKhach().getMaSo();
        String maChuyenBay = don.getChuyenBay().getSoHieuCHuyenBay();
        double soTien = don.getSoTienThanhToan();
        if (maHanhKhach == null || maHanhKhach.trim().isEmpty()
                || maChuyenBay == null || maChuyenBay.trim().isEmpty()
                || !Double.isFinite(soTien) || soTien <= 0
                || timDonTheoMa(don.getMaDonDat()) != null
                || daCoMaVe(don.getVe().getMaVe())) {
            return false;
        }
        if (soLuongDon == danhSachDon.length) {
            DonDatVe[] danhSachMoi = new DonDatVe[danhSachDon.length * 2];
            System.arraycopy(danhSachDon, 0, danhSachMoi, 0, soLuongDon);
            danhSachDon = danhSachMoi;
        }
        danhSachDon[soLuongDon++] = don;
        return true;
    }

    private int timViTriDonTheoMa(String maDonDat) {
        if (maDonDat == null || maDonDat.trim().isEmpty()) {
            return -1;
        }
        for (int i = 0; i < soLuongDon; i++) {
            if (danhSachDon[i].getMaDonDat().equalsIgnoreCase(maDonDat.trim())) {
                return i;
            }
        }
        return -1;
    }

    private boolean daCoMaVe(String maVe) {
        if (maVe == null || maVe.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < soLuongDon; i++) {
            VeMayBay ve = danhSachDon[i].getVe();
            if (ve != null && ve.getMaVe() != null && ve.getMaVe().equalsIgnoreCase(maVe.trim())) {
                return true;
            }
        }
        return false;
    }

    private String taoMaDonTiepTheo() {
        String ma;
        do {
            ma = String.format("DD%04d", soDonTiepTheo++);
        } while (timDonTheoMa(ma) != null);
        return ma;
    }

    private String taoMaVeTiepTheo() {
        String ma;
        do {
            ma = String.format("VE%04d", soVeTiepTheo++);
        } while (daCoMaVe(ma));
        return ma;
    }

    private IThanhToan nhapThanhToan() {
        Integer loai = nhapSoNguyen("Thanh toán (1. Tiền mặt, 2. Thẻ): ");
        if (loai == null) {
            return null;
        }
        if (loai == 1) {
            Double tien = nhapSoThuc("Số tiền khách đưa (VND): ");
            return tien == null ? null : new ThanhToanTienMat(tien);
        }
        if (loai == 2) {
            String soThe = nhapChuoi("Số thẻ: ");
            return soThe == null ? null : new ThanhToanThe(soThe);
        }
        System.out.println("Phương thức thanh toán không hợp lệ.");
        return null;
    }

    private static String nhapChuoi(String loiNhac) {
        System.out.print(loiNhac);
        return INPUT.hasNextLine() ? INPUT.nextLine().trim() : null;
    }

    private static Integer nhapSoNguyen(String loiNhac) {
        while (INPUT.hasNextLine()) {
            String text = nhapChuoi(loiNhac);
            if (text == null) {
                return null;
            }
            try {
                return Integer.valueOf(text);
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số nguyên hợp lệ.");
            }
        }
        return null;
    }

    private static Double nhapSoThuc(String loiNhac) {
        while (INPUT.hasNextLine()) {
            String text = nhapChuoi(loiNhac);
            if (text == null) {
                return null;
            }
            try {
                double value = Double.parseDouble(text.replace(',', '.'));
                if (Double.isFinite(value) && value >= 0) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // In hướng dẫn chung phía dưới.
            }
            System.out.println("Vui lòng nhập số không âm hợp lệ.");
        }
        return null;
    }

    private static NgayThang ngayHomNay() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return new NgayThang(today.getDayOfMonth(), today.getMonthValue(), today.getYear());
    }

    private DonDatVe loi(String thongBao) {
        thongBaoGanNhat = thongBao;
        return null;
    }
}

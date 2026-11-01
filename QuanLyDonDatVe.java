/** Quản lý các đơn đặt vé, tình trạng ghế và báo cáo doanh thu. */
public class QuanLyDonDatVe {
    private DonDatVe[] danhSach;
    private int soLuong;
    private int soDonTiepTheo;
    private int soVeTiepTheo;
    private String thongBaoGanNhat;

    public QuanLyDonDatVe() {
        danhSach = new DonDatVe[100];
        soLuong = 0;
        soDonTiepTheo = 1;
        soVeTiepTheo = 1;
        thongBaoGanNhat = "";
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

    /**
     * Tạo đơn đặt vé. loaiVe nhận 1 cho phổ thông, 2 cho thương gia.
     * Ghế chỉ được giữ nếu giao dịch thanh toán thành công.
     */
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

        DonDatVe don = new DonDatVe(maDonSach, hanhKhach, chuyenBay, ve,
                tenPhuongThuc == null ? "Không xác định" : tenPhuongThuc.trim());
        if (!themDon(don)) {
            chuyenBay.HuyGhe();
            return loi("Không thể lưu đơn; ghế đã được trả lại.");
        }
        thongBaoGanNhat = "Đặt vé thành công.";
        return don;
    }

    /** Thêm đơn có sẵn, dùng khi nạp dữ liệu hoặc ghép với các lớp khác. */
    public boolean themDon(DonDatVe don) {
        String maHanhKhach = don == null || don.getHanhKhach() == null
                ? null : don.getHanhKhach().getMaSo();
        String maChuyenBay = don == null || don.getChuyenBay() == null
                ? null : don.getChuyenBay().getSoHieuCHuyenBay();
        double soTien = don == null ? Double.NaN : don.getSoTienThanhToan();
        if (don == null || don.getMaDonDat() == null
                || don.getMaDonDat().trim().isEmpty() || don.getVe() == null
                || don.getVe().getMaVe() == null || don.getVe().getMaVe().trim().isEmpty()
                || maHanhKhach == null || maHanhKhach.trim().isEmpty()
                || maChuyenBay == null || maChuyenBay.trim().isEmpty()
                || !Double.isFinite(soTien) || soTien <= 0) {
            return false;
        }
        if (timDonTheoMa(don.getMaDonDat()) != null || daCoMaVe(don.getVe().getMaVe())) {
            return false;
        }
        if (soLuong == danhSach.length) {
            DonDatVe[] danhSachMoi = new DonDatVe[danhSach.length * 2];
            System.arraycopy(danhSach, 0, danhSachMoi, 0, soLuong);
            danhSach = danhSachMoi;
        }
        danhSach[soLuong++] = don;
        return true;
    }

    public DonDatVe timDonTheoMa(String maDonDat) {
        if (maDonDat == null || maDonDat.trim().isEmpty()) {
            return null;
        }
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaDonDat().equalsIgnoreCase(maDonDat.trim())) {
                return danhSach[i];
            }
        }
        return null;
    }

    /** Hủy đơn và trả ghế về chuyến bay; đơn vẫn được giữ để tra cứu lịch sử. */
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
        for (int i = 0; i < soLuong; i++) {
            DonDatVe don = danhSach[i];
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

    public double tinhDoanhThu() {
        double doanhThu = 0;
        for (int i = 0; i < soLuong; i++) {
            if (!danhSach[i].isDaHuy()) {
                doanhThu += danhSach[i].getSoTienThanhToan();
            }
        }
        return doanhThu;
    }

    public void inBaoCaoDoanhThu() {
        int soDonHieuLuc = 0;
        for (int i = 0; i < soLuong; i++) {
            if (!danhSach[i].isDaHuy()) {
                soDonHieuLuc++;
            }
        }
        System.out.println("\n===== BÁO CÁO DOANH THU =====");
        System.out.println("Số đơn còn hiệu lực: " + soDonHieuLuc);
        System.out.printf("Tổng doanh thu: %,.0f VND%n", tinhDoanhThu());

        String[] maChuyenBay = new String[soLuong];
        double[] doanhThuTheoChuyen = new double[soLuong];
        int soChuyen = 0;
        for (int i = 0; i < soLuong; i++) {
            DonDatVe don = danhSach[i];
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
        if (soChuyen > 0) {
            System.out.println("Doanh thu theo chuyến bay:");
            for (int i = 0; i < soChuyen; i++) {
                System.out.printf("- %s: %,.0f VND%n", maChuyenBay[i], doanhThuTheoChuyen[i]);
            }
        }
        System.out.println("=============================\n");
    }

    public void xuatDanhSach() {
        if (soLuong == 0) {
            System.out.println("Danh sách đơn đặt vé trống.");
            return;
        }
        for (int i = 0; i < soLuong; i++) {
            System.out.print((i + 1) + ". ");
            danhSach[i].hienThiThongTin();
        }
    }

    public DonDatVe[] getDanhSach() {
        DonDatVe[] ketQua = new DonDatVe[soLuong];
        System.arraycopy(danhSach, 0, ketQua, 0, soLuong);
        return ketQua;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public String getThongBaoGanNhat() {
        return thongBaoGanNhat;
    }

    private DonDatVe loi(String thongBao) {
        thongBaoGanNhat = thongBao;
        return null;
    }

    private boolean daCoMaVe(String maVe) {
        if (maVe == null || maVe.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < soLuong; i++) {
            VeMayBay ve = danhSach[i].getVe();
            if (ve != null && ve.getMaVe().equalsIgnoreCase(maVe.trim())) {
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
}

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Đơn đặt vé; các thuộc tính chính bám theo sơ đồ lớp của đồ án. */
public class DonDatVe {
    private String maDonDat;
    private Nguoi nguoiDat;
    private HanhKhach hanhKhach;
    private NhanVien nhanVienLap;
    private VeMayBay ve;
    private IThanhToan phuongThuc;
    private NgayThang ngayDat;

    // Giữ liên kết này vì VeMayBay.java hiện tại của nhóm chưa có thuộc tính ChuyenBay.
    private ChuyenBay chuyenBay;
    private boolean daHuy;
    private boolean daXacNhan;

    public DonDatVe() {
        this("", null, null, null, null, null, null, ngayHomNay());
    }

    /** Constructor theo các thuộc tính chính trong sơ đồ UML. */
    public DonDatVe(String maDonDat, Nguoi nguoiDat, HanhKhach hanhKhach,
                    NhanVien nhanVienLap, VeMayBay ve, IThanhToan phuongThuc,
                    NgayThang ngayDat) {
        this(maDonDat, nguoiDat, hanhKhach, nhanVienLap, null, ve, phuongThuc, ngayDat);
    }

    /** Constructor mở rộng để đơn giữ được chuyến bay với model hiện tại của nhóm. */
    public DonDatVe(String maDonDat, Nguoi nguoiDat, HanhKhach hanhKhach,
                    NhanVien nhanVienLap, ChuyenBay chuyenBay, VeMayBay ve,
                    IThanhToan phuongThuc, NgayThang ngayDat) {
        this.maDonDat = maDonDat;
        this.nguoiDat = nguoiDat == null ? hanhKhach : nguoiDat;
        this.hanhKhach = hanhKhach;
        this.nhanVienLap = nhanVienLap;
        this.chuyenBay = chuyenBay;
        this.ve = ve;
        this.phuongThuc = phuongThuc;
        this.ngayDat = ngayDat == null ? ngayHomNay() : new NgayThang(ngayDat);
        this.daHuy = false;
        this.daXacNhan = false;
    }

    /** Constructor tương thích với cách tạo đơn trước đây. */
    public DonDatVe(String maDonDat, HanhKhach hanhKhach, ChuyenBay chuyenBay,
                    VeMayBay ve, String tenPhuongThuc) {
        this(maDonDat, hanhKhach, hanhKhach, null, chuyenBay, ve,
                taoPhuongThucMau(tenPhuongThuc, ve), ngayHomNay());
    }

    public String getMaDonDat() {
        return maDonDat;
    }

    public void setMaDonDat(String maDonDat) {
        this.maDonDat = maDonDat;
    }

    public Nguoi getNguoiDat() {
        return nguoiDat;
    }

    public void setNguoiDat(Nguoi nguoiDat) {
        this.nguoiDat = nguoiDat;
        if (nguoiDat instanceof HanhKhach) {
            this.hanhKhach = (HanhKhach) nguoiDat;
        }
    }

    public HanhKhach getHanhKhach() {
        return hanhKhach;
    }

    public void setHanhKhach(HanhKhach hanhKhach) {
        boolean nguoiDatLaHanhKhach = nguoiDat == null || nguoiDat == this.hanhKhach;
        this.hanhKhach = hanhKhach;
        if (nguoiDatLaHanhKhach) {
            this.nguoiDat = hanhKhach;
        }
    }

    public NhanVien getNhanVienLap() {
        return nhanVienLap;
    }

    public void setNhanVienLap(NhanVien nhanVienLap) {
        this.nhanVienLap = nhanVienLap;
    }

    public VeMayBay getVe() {
        return ve;
    }

    public void setVe(VeMayBay ve) {
        this.ve = ve;
    }

    public IThanhToan getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(IThanhToan phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public NgayThang getNgayDat() {
        return ngayDat == null ? null : new NgayThang(ngayDat);
    }

    public void setNgayDat(NgayThang ngayDat) {
        this.ngayDat = ngayDat == null ? null : new NgayThang(ngayDat);
    }

    /** Liên kết bổ sung để tương thích với VeMayBay hiện có trong project. */
    public ChuyenBay getChuyenBay() {
        return chuyenBay;
    }

    public void setChuyenBay(ChuyenBay chuyenBay) {
        this.chuyenBay = chuyenBay;
    }

    public boolean isDaHuy() {
        return daHuy;
    }

    public void setDaHuy(boolean daHuy) {
        this.daHuy = daHuy;
    }

    public boolean isDaXacNhan() {
        return daXacNhan;
    }

    /** Xác nhận đơn sau khi đã giữ ghế và thanh toán thành công. */
    public void xacNhanDatVe() {
        daXacNhan = true;
    }

    public double getSoTienThanhToan() {
        return ve == null ? 0 : ve.tinhGiaVe();
    }

    /** Getter tương thích với giao diện đang hiển thị cả ngày và giờ. */
    public LocalDateTime getThoiGianDat() {
        if (ngayDat == null || !ngayDat.KTngay()) {
            return null;
        }
        return LocalDate.of(ngayDat.getNam(), ngayDat.getThang(), ngayDat.getNgay()).atStartOfDay();
    }

    /** Setter tương thích; NgayThang trong sơ đồ chỉ lưu phần ngày. */
    public void setThoiGianDat(LocalDateTime thoiGianDat) {
        if (thoiGianDat == null) {
            ngayDat = null;
        } else {
            ngayDat = new NgayThang(thoiGianDat.getDayOfMonth(),
                    thoiGianDat.getMonthValue(), thoiGianDat.getYear());
        }
    }

    /** Tên phương thức thanh toán để giao diện/báo cáo có thể hiển thị. */
    public String getPhuongThucThanhToan() {
        if (phuongThuc instanceof ThanhToanTienMat) {
            return "Tiền mặt";
        }
        if (phuongThuc instanceof ThanhToanThe) {
            return "Thẻ";
        }
        return phuongThuc == null ? "Chưa thanh toán" : "Phương thức khác";
    }

    /** Tương thích với dữ liệu cũ; phương thức mới được lưu dưới dạng IThanhToan. */
    public void setPhuongThucThanhToan(String tenPhuongThuc) {
        this.phuongThuc = taoPhuongThucMau(tenPhuongThuc, ve);
    }

    public void hienThiThongTin() {
        String maKhach = hanhKhach == null ? "(không có)" : hanhKhach.getMaSo();
        String tenKhach = hanhKhach == null ? "(không có)" : hanhKhach.getHoTen();
        String maChuyen = chuyenBay == null ? "(không có)" : chuyenBay.getSoHieuCHuyenBay();
        String maVe = ve == null ? "(không có)" : ve.getMaVe();
        String ngay = ngayDat == null ? "(không có)" : ngayDat.toString();
        System.out.printf("Đơn %s | Hành khách: %s - %s | Chuyến bay: %s | Vé: %s | Giá: %,.0f VND | Thanh toán: %s | Ngày đặt: %s | Trạng thái: %s%n",
                maDonDat, maKhach, tenKhach, maChuyen, maVe, getSoTienThanhToan(),
                getPhuongThucThanhToan(), ngay, daHuy ? "Đã hủy" : "Còn hiệu lực");
    }

    private static NgayThang ngayHomNay() {
        LocalDate today = LocalDate.now();
        return new NgayThang(today.getDayOfMonth(), today.getMonthValue(), today.getYear());
    }

    private static IThanhToan taoPhuongThucMau(String tenPhuongThuc, VeMayBay ve) {
        double soTien = ve == null ? 0 : ve.tinhGiaVe();
        if (tenPhuongThuc != null && tenPhuongThuc.toLowerCase().contains("thẻ")) {
            return new ThanhToanThe("THONG-TIN-CU");
        }
        if (tenPhuongThuc != null && tenPhuongThuc.toLowerCase().contains("tiền mặt")) {
            return new ThanhToanTienMat(soTien);
        }
        return null;
    }
}

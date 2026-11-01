import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Thông tin một giao dịch đặt vé và các đối tượng liên quan. */
public class DonDatVe {
    private String maDonDat;
    private HanhKhach hanhKhach;
    private Nguoi nguoiDat;
    private NhanVien nhanVienLap;
    private ChuyenBay chuyenBay;
    private VeMayBay ve;
    private LocalDateTime thoiGianDat;
    private String phuongThucThanhToan;
    private boolean daHuy;

    public DonDatVe() {
        this("", null, null, null, "");
    }

    public DonDatVe(String maDonDat, HanhKhach hanhKhach, ChuyenBay chuyenBay,
                    VeMayBay ve, String phuongThucThanhToan) {
        this.maDonDat = maDonDat;
        this.hanhKhach = hanhKhach;
        this.nguoiDat = hanhKhach;
        this.chuyenBay = chuyenBay;
        this.ve = ve;
        this.thoiGianDat = LocalDateTime.now();
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.daHuy = false;
    }

    public String getMaDonDat() {
        return maDonDat;
    }

    public void setMaDonDat(String maDonDat) {
        this.maDonDat = maDonDat;
    }

    public HanhKhach getHanhKhach() {
        return hanhKhach;
    }

    public void setHanhKhach(HanhKhach hanhKhach) {
        this.hanhKhach = hanhKhach;
        if (nguoiDat == null) {
            this.nguoiDat = hanhKhach;
        }
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

    public NhanVien getNhanVienLap() {
        return nhanVienLap;
    }

    public void setNhanVienLap(NhanVien nhanVienLap) {
        this.nhanVienLap = nhanVienLap;
    }

    public ChuyenBay getChuyenBay() {
        return chuyenBay;
    }

    public void setChuyenBay(ChuyenBay chuyenBay) {
        this.chuyenBay = chuyenBay;
    }

    public VeMayBay getVe() {
        return ve;
    }

    public void setVe(VeMayBay ve) {
        this.ve = ve;
    }

    public LocalDateTime getThoiGianDat() {
        return thoiGianDat;
    }

    public void setThoiGianDat(LocalDateTime thoiGianDat) {
        this.thoiGianDat = thoiGianDat;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public boolean isDaHuy() {
        return daHuy;
    }

    public void setDaHuy(boolean daHuy) {
        this.daHuy = daHuy;
    }

    public double getSoTienThanhToan() {
        return ve == null ? 0 : ve.tinhGiaVe();
    }

    public void hienThiThongTin() {
        String maKhach = hanhKhach == null ? "(không có)" : hanhKhach.getMaSo();
        String tenKhach = hanhKhach == null ? "(không có)" : hanhKhach.getHoTen();
        String maChuyenBay = chuyenBay == null ? "(không có)" : chuyenBay.getSoHieuCHuyenBay();
        String maVe = ve == null ? "(không có)" : ve.getMaVe();
        String thoiGian = thoiGianDat == null ? "(không có)"
                : thoiGianDat.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        System.out.printf("Đơn %s | Hành khách: %s - %s | Chuyến bay: %s | Vé: %s | Giá: %,.0f VND | Thanh toán: %s | Ngày đặt: %s | Trạng thái: %s%n",
                maDonDat, maKhach, tenKhach, maChuyenBay, maVe, getSoTienThanhToan(),
                phuongThucThanhToan, thoiGian, daHuy ? "Đã hủy" : "Còn hiệu lực");
    }
}

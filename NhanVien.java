public class NhanVien extends Nguoi {
    private static final long serialVersionUID = 1L;
    private String maNhanVien;
    private String MK;
    public NhanVien() {
        super(); 
        this.maNhanVien = "";
        this.MK = "";
    }
    public NhanVien(boolean status, String maSo, String hoTen, String soDienThoai, String maNhanVien, String MK) {
        super(status, maSo, hoTen, soDienThoai);
        this.maNhanVien = maNhanVien;
        this.MK = MK;
    }
    public NhanVien(String maSo, String maNhanVien, String hoTen,
                    String soDienThoai, String matKhau) {
        this(true, maSo, hoTen, soDienThoai, maNhanVien, matKhau);
    }
    @Override
    public void hienThiThongTin() {
        System.out.println(" | Trạng thái: " + (status ? "Còn hoạt động" : "Đã xóa")
                + " |[Nhân viên] Mã số: " + maSo + " | Họ tên: " + hoTen
                + " | SĐT: " + soDienThoai + " | Mã nhân viên: " + maNhanVien + " | Mã khóa: " + MK);
    }
    public String getMaNhanVien() {
        return maNhanVien;
    }
    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }
    public String getMK() {
        return MK;
    }
    public void setMK(String MK) {
        this.MK = MK;
    }
}

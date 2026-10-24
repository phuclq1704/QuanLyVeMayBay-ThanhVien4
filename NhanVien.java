public class NhanVien extends Nguoi {
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

public class DaiLy extends Nguoi {
    private String loaiDaiLy;
    private Double duNo;
    public DaiLy() {
        super(); 
        this.loaiDaiLy = "";
        this.duNo = 0.0;
    }
    public DaiLy(boolean status, String maSo, String hoTen, String soDienThoai, String loaiDaiLy, Double duNo) {
        super(status, maSo, hoTen, soDienThoai);
        this.loaiDaiLy = loaiDaiLy;
        this.duNo = duNo;
    }
      /**
     *  soTien > 0 : đại lý lấy vé / phát sinh nợ  -> duNo tăng
     *  soTien < 0 : đại lý thanh toán công nợ     -> duNo giảm (không được trả vượt dư nợ)
     */
    public void capNhatCongNo(boolean status,Double soTien) {
        if (!status)
            return;
        if (soTien > 0) {
            duNo += soTien;
        } else if (soTien < 0) {
            if (duNo + soTien >= 0) {
                duNo += soTien;
            } else {
                System.out.println("Số tiền thanh toán vượt dư nợ. Không thể cập nhật công nợ.");
            }
        }
    }
    @Override
    public void hienThiThongTin() {
        System.out.println(" | Trạng thái: " + (status ? "Còn hoạt động" : "Đã xóa")
                + " |[Đại Lý] Mã số: " + maSo + " | Họ tên: " + hoTen
                + " | SĐT: " + soDienThoai + " | Loại đại lý: " + loaiDaiLy + " | Dư nợ: " + duNo);
    }
    public String getLoaiDaiLy() {
        return loaiDaiLy;
    }
    public void setLoaiDaiLy(String loaiDaiLy) {
        this.loaiDaiLy = loaiDaiLy;
    }
    public Double getDuNo() {
        return duNo;
    }
    public void setDuNo(Double duNo) {
        this.duNo = duNo;
    }
}

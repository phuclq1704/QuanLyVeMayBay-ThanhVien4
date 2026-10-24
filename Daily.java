public class Daily extends Nguoi {
    private String loaiDaily;
    private Double duNo;
    public Daily() {
        super(); 
        this.loaiDaily = "";
        this.duNo = 0.0;
    }
    public Daily(boolean status, String maSo, String hoTen, String soDienThoai, String loaiDaily, Double duNo) {
        super(status, maSo, hoTen, soDienThoai);
        this.loaiDaily = loaiDaily;
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
                + " |[Đại lý] Mã số: " + maSo + " | Họ tên: " + hoTen
                + " | SĐT: " + soDienThoai + " | Loại đại lý: " + loaiDaily + " | Dư nợ: " + duNo);
    }
    public String getLoaiDaily() {
        return loaiDaily;
    }
    public void setLoaiDaily(String loaiDaily) {
        this.loaiDaily = loaiDaily;
    }
    public Double getDuNo() {
        return duNo;
    }
    public void setDuNo(Double duNo) {
        this.duNo = duNo;
    }
}

public class HanhKhach extends Nguoi {
    private String Hochieu;
    public HanhKhach() {
        super(); 
        this.Hochieu = ""; 
        }
    public HanhKhach(boolean status, String maSo, String hoTen, String soDienThoai, String Hochieu) {
        super(status, maSo, hoTen, soDienThoai);
        this.Hochieu = Hochieu;
    }
    @Override
    public void hienThiThongTin() {
        System.out.println(" | Trạng thái: " + (status ? "Còn hoạt động" : "Đã xóa")
                + " |[Hành khách] Mã số: " + maSo + " | Họ tên: " + hoTen
                + " | SĐT: " + soDienThoai + " | Số hộ chiếu: " + Hochieu);
    }
    public String getHochieu() {
        return Hochieu;
    }
    public void setHochieu(String hochieu) {
        Hochieu = hochieu;
    }
}


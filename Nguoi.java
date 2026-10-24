public abstract class Nguoi {
    protected boolean status = true;
    protected String maSo;
    protected String hoTen;
    protected String soDienThoai;
    public Nguoi() {
        this.status = true;
        this.maSo = "";
        this.hoTen = "";
        this.soDienThoai = "";
    }
    public Nguoi(boolean status, String maSo, String hoTen, String soDienThoai) {
        this.status = status;
        this.maSo = maSo;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }
    public abstract void hienThiThongTin();
    
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getMaSo() {
        return maSo;
    }
    public void setMaSo(String maSo) {
        this.maSo = maSo;
    }
    public String getHoTen() {
        return hoTen;
    }
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    public String getSoDienThoai() {
        return soDienThoai;
    }
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}
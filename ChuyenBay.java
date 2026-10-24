public class ChuyenBay {
    private boolean status = true;  
    private String soHieuCHuyenBay;
    private String DiemDi, DiemDen;
    private String NgayDi, NgayDen;
    private int tongSoGhe, soGheTrong;

    public ChuyenBay() {
        this.soHieuCHuyenBay = "";
        this.DiemDi = "";
        this.DiemDen = "";
        this.NgayDi = "";
        this.NgayDen = "";
        this.tongSoGhe = 0;
        this.soGheTrong = 0;
    }

    public ChuyenBay(boolean status, String soHieuCHuyenBay, String DiemDi, String DiemDen, String NgayDi, String NgayDen, int tongSoGhe, int soGheTrong) {
        this.status = status;
        this.soHieuCHuyenBay = soHieuCHuyenBay;
        this.DiemDi = DiemDi;
        this.DiemDen = DiemDen;
        this.NgayDi = NgayDi;
        this.NgayDen = NgayDen;
        this.tongSoGhe = tongSoGhe;
        this.soGheTrong = soGheTrong;
    }

    public boolean DatGhe () {
        if (soGheTrong <= 0) {
            return false;
        }
        soGheTrong--;
        status = soGheTrong > 0;
        return true;
    }
    public boolean HuyGhe () {
        if (soGheTrong >= tongSoGhe) {
            return false;
        }
        soGheTrong++;
        status = true;
        return true;
    }
    public void hienThiThongTin() {
        System.out.println(" | Trạng thái: " + (status ? "Còn Ghế" : "Hết Ghế")
                + " |[Chuyến bay] Số hiệu: " + soHieuCHuyenBay + " | Điểm đi: " + DiemDi
                + " | Điểm đến: " + DiemDen + " | Ngày đi: " + NgayDi
                + " | Ngày đến: " + NgayDen + " | Tổng số ghế: " + tongSoGhe
                + " | Số ghế trống: " + soGheTrong);
    }
    
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getSoHieuCHuyenBay() {
        return soHieuCHuyenBay;
    }
    public void setSoHieuCHuyenBay(String soHieuCHuyenBay) {
        this.soHieuCHuyenBay = soHieuCHuyenBay;
    }
    public String getDiemDi() {
        return DiemDi;
    }
    public void setDiemDi(String DiemDi) {
        this.DiemDi = DiemDi;
    }
    public String getDiemDen() {
        return DiemDen;
    }
    public void setDiemDen(String DiemDen) {
        this.DiemDen = DiemDen;
    }
    public String getNgayDi() {
        return NgayDi;
    }
    public void setNgayDi(String NgayDi) {
        this.NgayDi = NgayDi;
    }
    public String getNgayDen() {
        return NgayDen;
    }
    public void setNgayDen(String NgayDen) {
        this.NgayDen = NgayDen;
    }
    public int getTongSoGhe() {
        return tongSoGhe;
    }
    public void setTongSoGhe(int tongSoGhe) {
        this.tongSoGhe = tongSoGhe;
    }
    public int getSoGheTrong() {
        return soGheTrong;
    }
    public void setSoGheTrong(int soGheTrong) {
        this.soGheTrong = soGheTrong;
    }
}

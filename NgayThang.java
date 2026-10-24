public class NgayThang {
    private int ngay, thang, nam;
    private boolean status = true;

    public NgayThang() {
        ngay = 19;
        thang = 10;
        nam = 2026;
    }

    public NgayThang(int ngay, int thang, int nam) {
        this.ngay = ngay;
        this.thang = thang;
        this.nam = nam;
    }

    // tránh 2 biến trỏ cùng 1 ngày
    public NgayThang(NgayThang k) {
        this.ngay = k.ngay;
        this.thang = k.thang;
        this.nam = k.nam;
        this.status = k.status;
    }

    public boolean KTngay() {
        if (nam < 0 || thang < 1 || thang > 12 || ngay < 1 || ngay > 31) {
            return false;
        }
        if (thang == 2) {
            if ((nam % 4 == 0 && nam % 100 != 0) || (nam % 400 == 0)) {
                return ngay <= 29;
            } else {
                return ngay <= 28;
            }
        }
        if (thang == 4 || thang == 6 || thang == 9 || thang == 11) {
            return ngay <= 30;
        }
        return true;
    }

    // Trả về chuỗi để class khác ghép vào câu / ghi file
    public String chuyenThanhChuoi() {
        return ngay + "/" + thang + "/" + nam;
    }

    @Override
    public String toString() {
        return chuyenThanhChuoi();
    }

    public void hienThi() {
        if (KTngay()) {
            System.out.println(this);
        } else {
            System.out.println("Ngày tháng không hợp lệ.");
        }
    }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
    public int getNgay() { return ngay; }
    public void setNgay(int ngay) { this.ngay = ngay; }
    public int getThang() { return thang; }
    public void setThang(int thang) { this.thang = thang; }
    public int getNam() { return nam; }
    public void setNam(int nam) { this.nam = nam; }
}
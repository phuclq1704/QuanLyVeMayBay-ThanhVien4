import java.io.Serializable;

public abstract class VeMayBay implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final double THUE_VAT = 0.08;
    private String maVe;
    private double giaGoc;
    private ChuyenBay chuyenBay;
    private HanhKhach hanhKhach;
    private boolean duocDoiTra = true;
    private int tongSoVe = 1;

    public VeMayBay(String maVe, double giaGoc) {
        this.maVe = maVe;
        this.giaGoc = giaGoc;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public String getMaVeHeThong() {
        return maVe;
    }

    public void setMaVeHeThong(String maVe) {
        this.maVe = maVe;
    }

    public double getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(double giaGoc) {
        this.giaGoc = giaGoc;
    }

    public ChuyenBay getChuyenBay() {
        return chuyenBay;
    }

    public void setChuyenBay(ChuyenBay chuyenBay) {
        this.chuyenBay = chuyenBay;
    }

    public HanhKhach getHanhKhach() {
        return hanhKhach;
    }

    public void setHanhKhach(HanhKhach hanhKhach) {
        this.hanhKhach = hanhKhach;
    }

    public boolean isDuocDoiTra() {
        return duocDoiTra;
    }

    public void setDuocDoiTra(boolean duocDoiTra) {
        this.duocDoiTra = duocDoiTra;
    }

    public int getTongSoVe() {
        return tongSoVe;
    }

    public void setTongSoVe(int tongSoVe) {
        this.tongSoVe = tongSoVe;
    }

    public abstract double tinhGiaVe();
}

public abstract class VeMayBay {
    private String maVe;
    private double giaGoc;

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

    public double getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(double giaGoc) {
        this.giaGoc = giaGoc;
    }

    public abstract double tinhGiaVe();
}
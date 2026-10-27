public class VeThuongGia extends VeMayBay {
    private double phiPhongCho;

    public VeThuongGia(String maVe, double giaGoc,
                       double phiPhongCho) {
        super(maVe, giaGoc);
        this.phiPhongCho = phiPhongCho;
    }

    public double getPhiPhongCho() {
        return phiPhongCho;
    }

    public void setPhiPhongCho(double phiPhongCho) {
        this.phiPhongCho = phiPhongCho;
    }

    @Override
    public double tinhGiaVe() {
        return (getGiaGoc() + phiPhongCho) * 1.08;
    }
}
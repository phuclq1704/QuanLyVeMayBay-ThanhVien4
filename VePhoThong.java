public class VePhoThong extends VeMayBay {
    private double phiHanhLy;

    public VePhoThong(String maVe, double giaGoc,
                      double phiHanhLy) {
        super(maVe, giaGoc);
        this.phiHanhLy = phiHanhLy;
    }

    public double getPhiHanhLy() {
        return phiHanhLy;
    }

    public void setPhiHanhLy(double phiHanhLy) {
        this.phiHanhLy = phiHanhLy;
    }

    @Override
    public double tinhGiaVe() {
        return (getGiaGoc() + phiHanhLy) * 1.08;
    }
}
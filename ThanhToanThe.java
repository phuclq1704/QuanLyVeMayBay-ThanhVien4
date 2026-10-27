public class ThanhToanThe implements IThanhToan {
    private String soThe;

    public ThanhToanThe(String soThe) {
        this.soThe = soThe;
    }

    public String getSoThe() {
        return soThe;
    }

    public void setSoThe(String soThe) {
        this.soThe = soThe;
    }

    @Override
    public boolean thanhToan(double soTien) {
        if (!soThe.isEmpty() && soTien > 0) {
            System.out.println("Thanh toan the thanh cong!");
            return true;
        } else {
            System.out.println("Thong tin khong hop le!");
            return false;
        }
    }
}
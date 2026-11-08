public class ThanhToanTienMat implements IThanhToan {
    private static final long serialVersionUID = 1L;
    private double tienKhachDua;

    public ThanhToanTienMat(double tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    public double getTienKhachDua() {
        return tienKhachDua;
    }

    public void setTienKhachDua(double tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    @Override
    public boolean thanhToan(double soTien) {
        if (tienKhachDua >= soTien) {
            System.out.println("Thanh toan thanh cong!");
            System.out.println("Tien thua: "
                    + (tienKhachDua - soTien));
            return true;
        } else {
            System.out.println("Khong du tien!");
            return false;
        }
    }
}

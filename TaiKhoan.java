/** Tài khoản mẫu dùng để đăng nhập và phân quyền trong đồ án. */
public final class TaiKhoan {
    private final String tenDangNhap;
    private final String matKhau;
    private final VaiTro vaiTro;

    public TaiKhoan(String tenDangNhap, String matKhau, VaiTro vaiTro) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (matKhau == null || vaiTro == null) {
            throw new IllegalArgumentException("Mật khẩu và vai trò là bắt buộc.");
        }
        this.tenDangNhap = tenDangNhap.trim();
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public boolean khopThongTin(String ten, char[] matKhauNhap) {
        return ten != null
                && matKhauNhap != null
                && tenDangNhap.equalsIgnoreCase(ten.trim())
                && matKhau.equals(new String(matKhauNhap));
    }
}

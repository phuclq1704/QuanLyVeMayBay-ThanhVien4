/** Vai trò đăng nhập và quyền truy cập các màn hình của SkyDesk. */
public enum VaiTro {
    CLIENT("Khách hàng", "Xem chuyến bay, tạo đơn đặt vé"),
    STAFF("Nhân viên bán vé", "Tra cứu và xử lý đặt vé"),
    MANAGER("Quản lý", "Toàn quyền quản lý hệ thống"),
    ADMIN("Quản trị viên", "Toàn quyền quản lý hệ thống");

    private final String tenHienThi;
    private final String moTaQuyen;

    VaiTro(String tenHienThi, String moTaQuyen) {
        this.tenHienThi = tenHienThi;
        this.moTaQuyen = moTaQuyen;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    public String getMoTaQuyen() {
        return moTaQuyen;
    }

    public boolean coToanQuyen() {
        return this == MANAGER || this == ADMIN;
    }

    public boolean duocMoTrang(String khoaTrang) {
        if (coToanQuyen()) return true;
        if (khoaTrang == null) return false;

        switch (this) {
            case CLIENT:
                return "home".equals(khoaTrang)
                        || "flights".equals(khoaTrang)
                        || "booking".equals(khoaTrang);
            case STAFF:
                return "home".equals(khoaTrang)
                        || "flights".equals(khoaTrang)
                        || "passengers".equals(khoaTrang)
                        || "tickets".equals(khoaTrang)
                        || "booking".equals(khoaTrang)
                        || "orders".equals(khoaTrang);
            default:
                return false;
        }
    }

    public boolean duocQuanLyChuyenBay() {
        return coToanQuyen();
    }

    public boolean duocSuaHanhKhach() {
        return this == STAFF || coToanQuyen();
    }

    public boolean duocXoaDuLieu() {
        return coToanQuyen();
    }

    public boolean duocHuyDon() {
        return this == STAFF || coToanQuyen();
    }
}

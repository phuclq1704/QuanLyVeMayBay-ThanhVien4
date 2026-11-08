public class QuanLyNhanVien {
    private NhanVien[] danhsach;
    private int soluong;

    public QuanLyNhanVien() {
        this.danhsach = new NhanVien[1000];
        this.soluong = 0;
    }

    private NhanVien layma (String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            return null;
        }
        String ma = maNV.trim();
        for (int i = 0; i < soluong; i++) {
            if (danhsach[i].getMaNhanVien().equalsIgnoreCase(ma)) {
                return danhsach[i];
            }
        }
        return null;
    }
    
    public boolean themNhanVIen( NhanVien nv) {
        if (nv == null || soluong >= danhsach.length) {
            return false;
        }
        String maNhanVien = nv.getMaNhanVien();
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return false;
        }
        if (layma(maNhanVien) != null) {
            return false;
        }
        danhsach[soluong] = nv;
        soluong++;
        return true;
    }

    /** Tên phương thức chuẩn; giữ themNhanVIen để tương thích mã cũ. */
    public boolean themNhanVien(NhanVien nv) {
        return themNhanVIen(nv);
    }

    public int getSoLuong() {
        return soluong;
    }

    public NhanVien[] getDanhSach() {
        NhanVien[] ketQua = new NhanVien[soluong];
        System.arraycopy(danhsach, 0, ketQua, 0, soluong);
        return ketQua;
    }

    public boolean suaNhanVien(String maNV, String hoTen, String soDienThoai, String matKhau) {
        NhanVien nv = timNhanVien(maNV);
        if (nv == null || hoTen == null || hoTen.trim().isEmpty()
                || soDienThoai == null || soDienThoai.trim().isEmpty()
                || matKhau == null || matKhau.trim().isEmpty()) return false;
        nv.setHoTen(hoTen.trim());
        nv.setSoDienThoai(soDienThoai.trim());
        nv.setMK(matKhau.trim());
        return true;
    }
    public boolean xoaNhanVien(String maNV) {
        NhanVien nv = layma(maNV);
        if (nv == null|| !nv.isStatus()) {
            return false;
        }
        nv.setStatus(false);
        return true;
    }
    public NhanVien timNhanVien(String maNV) {
        NhanVien nv = layma(maNV);
        if (nv == null || !nv.isStatus()) {
            return null;
        }
        return nv;
    }
}

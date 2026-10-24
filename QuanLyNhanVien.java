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
        nv.setStatus(true);
        danhsach[soluong] = nv;
        soluong++;
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

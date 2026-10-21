import java.nio.file.Files;
import java.nio.file.Paths;

/** Tao va nap du lieu demo cho hanh khach, chuyen bay va dai ly. */
public final class DuLieuMau {
    private DuLieuMau() {
    }

    public static void napTatCa(
            QuanLyHanhKhach qlHanhKhach,
            QuanLyChuyenBay qlChuyenBay,
            QuanLyDaiLy qlDaiLy) {
        if (qlHanhKhach == null || qlChuyenBay == null || qlDaiLy == null) {
            throw new IllegalArgumentException("Cac lop quan ly khong duoc null.");
        }
        napHanhKhach(qlHanhKhach);
        napChuyenBay(qlChuyenBay);
        napDaiLy(qlDaiLy);
    }

    /**
     * Nap du lieu tu file neu file da co; neu chua co thi tao mock va ghi ra file.
     * Goi mot lan luc khoi dong chuong trinh, sau khi tao cac lop QuanLy.
     */
    public static void napHoacTaoDuLieuDemo(
            QuanLyHanhKhach qlHanhKhach,
            QuanLyChuyenBay qlChuyenBay,
            QuanLyDaiLy qlDaiLy,
            String fileHanhKhach,
            String fileChuyenBay,
            String fileDaiLy) {
        napHoacTaoHanhKhach(qlHanhKhach, fileHanhKhach);
        napHoacTaoChuyenBay(qlChuyenBay, fileChuyenBay);
        napHoacTaoDaiLy(qlDaiLy, fileDaiLy);
    }

    private static void napHoacTaoHanhKhach(QuanLyHanhKhach ql, String tenFile) {
        if (Files.exists(Paths.get(tenFile))) {
            if (!XuLyFile.docVaThemHanhKhach(tenFile, ql)) {
                throw new IllegalStateException("Khong nap duoc hanh khach tu " + tenFile);
            }
            return;
        }
        napHanhKhach(ql);
        HanhKhach[] duLieu = new HanhKhach[ql.getSoLuongHanhKhach()];
        for (int i = 0; i < duLieu.length; i++) {
            duLieu[i] = ql.getHanhKhach(i);
        }
        if (!XuLyFile.ghiHanhKhachXuongFile(duLieu, duLieu.length, tenFile)) {
            throw new IllegalStateException("Khong ghi duoc mock hanh khach vao " + tenFile);
        }
    }

    private static void napHoacTaoChuyenBay(QuanLyChuyenBay ql, String tenFile) {
        if (Files.exists(Paths.get(tenFile))) {
            if (!XuLyFile.docVaThemChuyenBay(tenFile, ql)) {
                throw new IllegalStateException("Khong nap duoc chuyen bay tu " + tenFile);
            }
            return;
        }
        napChuyenBay(ql);
        ChuyenBay[] duLieu = new ChuyenBay[ql.getSoLuongChuyenBay()];
        for (int i = 0; i < duLieu.length; i++) {
            duLieu[i] = ql.getChuyenBay(i);
        }
        if (!XuLyFile.ghiChuyenBayXuongFile(duLieu, duLieu.length, tenFile)) {
            throw new IllegalStateException("Khong ghi duoc mock chuyen bay vao " + tenFile);
        }
    }

    private static void napHoacTaoDaiLy(QuanLyDaiLy ql, String tenFile) {
        if (Files.exists(Paths.get(tenFile))) {
            if (!XuLyFile.docVaThemDaiLy(tenFile, ql)) {
                throw new IllegalStateException("Khong nap duoc dai ly tu " + tenFile);
            }
            return;
        }
        napDaiLy(ql);
        DaiLy[] duLieu = new DaiLy[ql.getSoLuongDaiLy()];
        for (int i = 0; i < duLieu.length; i++) {
            duLieu[i] = ql.getDaiLy(i);
        }
        if (!XuLyFile.ghiDaiLyXuongFile(duLieu, duLieu.length, tenFile)) {
            throw new IllegalStateException("Khong ghi duoc mock dai ly vao " + tenFile);
        }
    }

    public static void napHanhKhach(QuanLyHanhKhach ql) {
        themHanhKhach(ql, "HK0001", "Nguyen Minh Anh", "0900000001", "P0000001");
        themHanhKhach(ql, "HK0002", "Tran Quoc Bao", "0900000002", "P0000002");
        themHanhKhach(ql, "HK0003", "Le Thu Ha", "0900000003", "P0000003");
        themHanhKhach(ql, "HK0004", "Pham Gia Huy", "0900000004", "P0000004");
        themHanhKhach(ql, "HK0005", "Vo Ngoc Lan", "0900000005", "P0000005");
        themHanhKhach(ql, "HK0006", "Dang Tuan Kiet", "0900000006", "P0000006");
        themHanhKhach(ql, "HK0007", "Bui Khanh Linh", "0900000007", "P0000007");
        themHanhKhach(ql, "HK0008", "Do Thanh Nam", "0900000008", "P0000008");
        themHanhKhach(ql, "HK0009", "Hoang Mai Phuong", "0900000009", "P0000009");
        themHanhKhach(ql, "HK0010", "Ngo Duc Thinh", "0900000010", "P0000010");
        themHanhKhach(ql, "HK0011", "Duong Bao Tram", "0900000011", "P0000011");
        themHanhKhach(ql, "HK0012", "Ly Hoang Yen", "0900000012", "P0000012");
        themHanhKhach(ql, "HK0013", "Mai Anh Tuan", "0900000013", "P0000013");
        themHanhKhach(ql, "HK0014", "Truong Ngoc Diep", "0900000014", "P0000014");
        themHanhKhach(ql, "HK0015", "Cao Phuong Thao", "0900000015", "P0000015");
        themHanhKhach(ql, "HK0016", "Vu Tien Dat", "0900000016", "P0000016");
        themHanhKhach(ql, "HK0017", "Phan My Duyen", "0900000017", "P0000017");
        themHanhKhach(ql, "HK0018", "Ho Bao Long", "0900000018", "P0000018");
        themHanhKhach(ql, "HK0019", "Ta Nhu Quynh", "0900000019", "P0000019");
        themHanhKhach(ql, "HK0020", "Dinh Minh Quan", "0900000020", "P0000020");
    }

    public static void napChuyenBay(QuanLyChuyenBay ql) {
        themChuyenBay(ql, "CB0001", "Ha Noi", "Ho Chi Minh", 1, 11, 2026, 180);
        themChuyenBay(ql, "CB0002", "Ho Chi Minh", "Da Nang", 2, 11, 2026, 160);
        themChuyenBay(ql, "CB0003", "Da Nang", "Ha Noi", 3, 11, 2026, 150);
        themChuyenBay(ql, "CB0004", "Ha Noi", "Nha Trang", 4, 11, 2026, 150);
        themChuyenBay(ql, "CB0005", "Ho Chi Minh", "Phu Quoc", 5, 11, 2026, 140);
        themChuyenBay(ql, "CB0006", "Can Tho", "Ha Noi", 6, 11, 2026, 150);
        themChuyenBay(ql, "CB0007", "Ha Noi", "Da Lat", 7, 11, 2026, 140);
        themChuyenBay(ql, "CB0008", "Da Nang", "Ho Chi Minh", 8, 11, 2026, 160);
        themChuyenBay(ql, "CB0009", "Ho Chi Minh", "Hai Phong", 9, 11, 2026, 180);
        themChuyenBay(ql, "CB0010", "Nha Trang", "Ha Noi", 10, 11, 2026, 150);
    }

    public static void napDaiLy(QuanLyDaiLy ql) {
        themDaiLy(ql, "DL0001", "Dai Ly Sao Viet", "0900000101", "Cap 1", 2500000.0);
        themDaiLy(ql, "DL0002", "Dai Ly Bien Xanh", "0900000102", "Cap 2", 1250000.0);
        themDaiLy(ql, "DL0003", "Dai Ly May Bay Tre", "0900000103", "Cap 1", 0.0);
    }

    private static void themHanhKhach(
            QuanLyHanhKhach ql, String ma, String hoTen, String dienThoai, String hoChieu) {
        HanhKhach hk = new HanhKhach();
        hk.setMaSo(ma);
        hk.setHoTen(hoTen);
        hk.setSoDienThoai(dienThoai);
        hk.setSoHoChieu(hoChieu);
        if (!ql.themHanhKhach(hk)) {
            throw new IllegalStateException("Khong them duoc hanh khach " + ma);
        }
    }

    private static void themChuyenBay(
            QuanLyChuyenBay ql, String ma, String diemDi, String diemDen,
            int ngay, int thang, int nam, int tongSoGhe) {
        ChuyenBay cb = new ChuyenBay();
        cb.setSoHieuChuyenBay(ma);
        cb.setDiemDi(diemDi);
        cb.setDiemDen(diemDen);
        cb.setNgayKhoiHanh(new NgayThang(ngay, thang, nam));
        cb.setTongSoGhe(tongSoGhe);
        cb.setSoGheTrong(tongSoGhe);
        cb.setTrangThai("Mo ban");
        if (!ql.themChuyenBay(cb)) {
            throw new IllegalStateException("Khong them duoc chuyen bay " + ma);
        }
    }

    private static void themDaiLy(
            QuanLyDaiLy ql, String ma, String ten, String dienThoai,
            String loai, double duNo) {
        DaiLy dl = new DaiLy();
        dl.setMaSo(ma);
        dl.setHoTen(ten);
        dl.setSoDienThoai(dienThoai);
        dl.setLoaiDaiLy(loai);
        dl.setDuNo(duNo);
        if (!ql.themDaiLy(dl)) {
            throw new IllegalStateException("Khong them duoc dai ly " + ma);
        }
    }
}

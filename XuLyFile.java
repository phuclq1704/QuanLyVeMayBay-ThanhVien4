import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

class XuLyFile {
    private static final Path THU_MUC = Paths.get("data");

    // API theo so do UML
    public static boolean ghiVe(VeMayBay[] dsVe, int n) {
        return ghiMang(dsVe, n, "ve.txt");
    }
    public static VeMayBay[] docVe() { return docMang("ve.txt", VeMayBay.class); }
    public static boolean ghiDonDat(DonDatVe[] dsDonDat, int n) {
        return ghiMang(dsDonDat, n, "dondat.txt");
    }
    public static DonDatVe[] docDonDat() { return docMang("dondat.txt", DonDatVe.class); }

    // Luu/nap cac lop du lieu khac de ung dung co the mo lai trang thai.
    public static boolean ghiChuyenBay(ChuyenBay[] ds, int n) { return ghiMang(ds, n, "chuyenbay.txt"); }
    public static ChuyenBay[] docChuyenBay() { return docMang("chuyenbay.txt", ChuyenBay.class); }
    public static boolean ghiHanhKhach(HanhKhach[] ds, int n) { return ghiMang(ds, n, "hanhkhach.txt"); }
    public static HanhKhach[] docHanhKhach() { return docMang("hanhkhach.txt", HanhKhach.class); }
    public static boolean ghiNhanVien(NhanVien[] ds, int n) { return ghiMang(ds, n, "nhanvien.txt"); }
    public static NhanVien[] docNhanVien() { return docMang("nhanvien.txt", NhanVien.class); }
    public static boolean ghiDaiLy(DaiLy[] ds, int n) { return ghiMang(ds, n, "daily.txt"); }
    public static DaiLy[] docDaiLy() { return docMang("daily.txt", DaiLy.class); }

    private static boolean ghiMang(Object[] ds, int n, String tenTep) {
        if (ds == null || n < 0 || n > ds.length) return false;
        String[] dong = new String[n];
        try {
            for (int i = 0; i < n; i++) {
                if (ds[i] == null) return false;
                dong[i] = maHoa(ds[i]);
            }
            Files.createDirectories(THU_MUC);
            Files.write(THU_MUC.resolve(tenTep), Arrays.asList(dong), StandardCharsets.UTF_8);
            return true;
        } catch (IOException | RuntimeException e) {
            System.err.println("Loi ghi " + tenTep + ": " + e.getMessage());
            return false;
        }
    }

    private static <T> T[] docMang(String tenTep, Class<T> loai) {
        try {
            Path tep = THU_MUC.resolve(tenTep);
            if (!Files.exists(tep)) return mangRong(loai);
            List<String> dong = Files.readAllLines(tep, StandardCharsets.UTF_8);
            T[] ketQua = mang(loai, dong.size());
            for (int i = 0; i < dong.size(); i++) {
                Object obj = giaiMa(dong.get(i));
                if (!loai.isInstance(obj)) throw new IOException("Sai kieu du lieu tai dong " + (i + 1));
                ketQua[i] = loai.cast(obj);
            }
            return ketQua;
        } catch (IOException | ClassNotFoundException | RuntimeException e) {
            System.err.println("Loi doc " + tenTep + ": " + e.getMessage());
            return mangRong(loai);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] mang(Class<T> loai, int n) {
        return (T[]) java.lang.reflect.Array.newInstance(loai, n);
    }
    private static <T> T[] mangRong(Class<T> loai) { return mang(loai, 0); }

    private static String maHoa(Object obj) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) { out.writeObject(obj); }
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }
    private static Object giaiMa(String text) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(text);
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return in.readObject();
        }
    }

    public static void luuTatCa(QuanLyChuyenBay cb, QuanLyHanhKhach hk,
            QuanLyNhanVien nv, QuanLyDaiLy dl, QuanLyVe ve, QuanLyDonDatVe don) throws IOException {
        if (!ghiChuyenBay(cb.getDanhSach(), cb.getSoLuong())
                || !ghiHanhKhach(hk.getDanhSach(), hk.getSoLuong())
                || !ghiNhanVien(nv.getDanhSach(), nv.getSoLuong())
                || !ghiDaiLy(dl.getDanhSach(), dl.getSoLuong())
                || !ghiVe(ve.getDanhSach(), ve.getSoLuong())
                || !ghiDonDat(don.getDanhSach(), don.getSoLuong())) {
            throw new IOException("Khong the ghi mot hoac nhieu tep du lieu.");
        }
    }

    public static boolean napDuLieu(QuanLyChuyenBay cb, QuanLyHanhKhach hk,
            QuanLyNhanVien nv, QuanLyDaiLy dl, QuanLyVe ve, QuanLyDonDatVe don) throws IOException {
        if (!Files.exists(THU_MUC.resolve("chuyenbay.txt"))
                || !Files.exists(THU_MUC.resolve("hanhkhach.txt"))) return false;

        ChuyenBay[] dsCB = docChuyenBay();
        HanhKhach[] dsHK = docHanhKhach();
        NhanVien[] dsNV = docNhanVien();
        DaiLy[] dsDL = docDaiLy();
        VeMayBay[] dsVe = docVe();
        DonDatVe[] dsDon = docDonDat();

        for (ChuyenBay item : dsCB) cb.themChuyenBay(item);
        for (HanhKhach item : dsHK) hk.themHanhKhach(item);
        for (NhanVien item : dsNV) nv.themNhanVien(item);
        for (DaiLy item : dsDL) dl.themDaiLy(item);

        for (VeMayBay item : dsVe) {
            ChuyenBay flight = cb.timChuyenBay(item.getChuyenBay().getSoHieuChuyenBay());
            if (flight == null) throw new IOException("Ve tham chieu chuyen bay khong ton tai: " + item.getMaVeHeThong());
            item.setChuyenBay(flight);
            ve.themVe(item);
        }
        for (DonDatVe item : dsDon) {
            VeMayBay ticket = item.getVe() == null ? null : ve.timVeChinhXac(item.getVe().getMaVeHeThong());
            HanhKhach passenger = item.getHanhKhach() == null ? null : hk.timHanhKhachTheoMa(item.getHanhKhach().getMaHanhKhach());
            NhanVien employee = item.getNhanVienLap() == null ? null : nv.timNhanVien(item.getNhanVienLap().getMaNhanVien());
            if (ticket == null || passenger == null) throw new IOException("Don dat co lien ket khong hop le: " + item.getMaDonDat());
            item.setVe(ticket);
            item.setHanhKhach(passenger);
            item.setNguoiDat(passenger);
            item.setNhanVienLap(employee);
            don.themDon(item);
        }
        return true;
    }

    public static void napDuLieuDemo(QuanLyChuyenBay cb, QuanLyHanhKhach hk,
            QuanLyNhanVien nv, QuanLyDaiLy dl) {
        LocalDate start = LocalDate.now().plusDays(3);
        String[][] route = {
            {"Ha Noi", "TP Ho Chi Minh"}, {"TP Ho Chi Minh", "Da Nang"},
            {"Ha Noi", "Da Nang"}, {"Da Nang", "Ha Noi"},
            {"TP Ho Chi Minh", "Phu Quoc"}, {"Ha Noi", "Nha Trang"},
            {"Can Tho", "Ha Noi"}, {"Hai Phong", "TP Ho Chi Minh"},
            {"TP Ho Chi Minh", "Hue"}, {"Nha Trang", "Ha Noi"}
        };
        for (int i = 0; i < route.length; i++) {
            cb.themChuyenBay(new ChuyenBay(String.format("CB%04d", i + 1), route[i][0], route[i][1], 60,
                    new NgayThang(start.plusDays(i))));
        }
        for (int i = 1; i <= 20; i++) {
            hk.themHanhKhach(new HanhKhach(String.format("HK%04d", i), "Hanh Khach " + i,
                    "090000" + String.format("%04d", i), "P" + String.format("%07d", i)));
        }
        nv.themNhanVien(new NhanVien("NV0001", "NV0001", "Nhan vien Ha Noi", "0901111001", "demo123"));
        nv.themNhanVien(new NhanVien("NV0002", "NV0002", "Nhan vien Sai Gon", "0901111002", "demo123"));
        dl.themDaiLy(new DaiLy("DL0001", "Dai ly Ha Noi", "0902222001", "Cap 1", 0));
        dl.themDaiLy(new DaiLy("DL0002", "Dai ly Sai Gon", "0902222002", "Cap 1", 0));
        dl.themDaiLy(new DaiLy("DL0003", "Dai ly Da Nang", "0902222003", "Cap 2", 0));
    }
}

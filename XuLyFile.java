import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

class XuLyFile {
    private static final Path THU_MUC = Paths.get("data");
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    // Cac phuong thuc theo so do UML: ghi/nap mang ve va don dat.
    public static boolean ghiVe(VeMayBay[] dsVe, int n) {
        return ghiMangDoiTuong(dsVe, n, "ve_mang.txt");
    }

    public static VeMayBay[] docVe() {
        java.util.List<String> dong;
        try {
            Path tep = THU_MUC.resolve("ve_mang.txt");
            if (!Files.exists(tep)) return new VeMayBay[0];
            dong = Files.readAllLines(tep, StandardCharsets.UTF_8);
            VeMayBay[] ketQua = new VeMayBay[dong.size()];
            for (int i = 0; i < dong.size(); i++) {
                ketQua[i] = (VeMayBay) giaiMaDoiTuong(dong.get(i));
            }
            return ketQua;
        } catch (IOException | ClassNotFoundException | ClassCastException | IllegalArgumentException e) {
            System.err.println("Loi doc danh sach ve: " + e.getMessage());
            return new VeMayBay[0];
        }
    }

    public static boolean ghiDonDat(DonDatVe[] dsDonDat, int n) {
        return ghiMangDoiTuong(dsDonDat, n, "dondat_mang.txt");
    }

    public static DonDatVe[] docDonDat() {
        java.util.List<String> dong;
        try {
            Path tep = THU_MUC.resolve("dondat_mang.txt");
            if (!Files.exists(tep)) return new DonDatVe[0];
            dong = Files.readAllLines(tep, StandardCharsets.UTF_8);
            DonDatVe[] ketQua = new DonDatVe[dong.size()];
            for (int i = 0; i < dong.size(); i++) {
                ketQua[i] = (DonDatVe) giaiMaDoiTuong(dong.get(i));
            }
            return ketQua;
        } catch (IOException | ClassNotFoundException | ClassCastException | IllegalArgumentException e) {
            System.err.println("Loi doc danh sach don dat: " + e.getMessage());
            return new DonDatVe[0];
        }
    }

    private static boolean ghiMangDoiTuong(Object[] danhSach, int n, String tenTep) {
        if (danhSach == null || n < 0 || n > danhSach.length) return false;
        String[] dong = new String[n];
        try {
            for (int i = 0; i < n; i++) {
                if (danhSach[i] == null) return false;
                dong[i] = maHoaDoiTuong(danhSach[i]);
            }
            Files.createDirectories(THU_MUC);
            Files.write(THU_MUC.resolve(tenTep),
                    java.util.Arrays.asList(dong), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("Loi ghi tep " + tenTep + ": " + e.getMessage());
            return false;
        }
    }

    private static String maHoaDoiTuong(Object doiTuong) throws IOException {
        java.io.ByteArrayOutputStream boNho = new java.io.ByteArrayOutputStream();
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(boNho)) {
            out.writeObject(doiTuong);
        }
        return java.util.Base64.getEncoder().encodeToString(boNho.toByteArray());
    }

    private static Object giaiMaDoiTuong(String chuoi) throws IOException, ClassNotFoundException {
        byte[] duLieu = java.util.Base64.getDecoder().decode(chuoi);
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(
                new java.io.ByteArrayInputStream(duLieu))) {
            return in.readObject();
        }
    }

    /** Nap tu tep neu da co du lieu; neu chua co thi tao du lieu demo va luu lai. */
    public static void napDuLieu(QuanLyChuyenBay cb, QuanLyHanhKhach hk, QuanLyDaiLy dl,
                                 QuanLyVe ve, QuanLyDonDatVe don) throws IOException {
        Path tepChuyenBay = THU_MUC.resolve("chuyenbay.txt");
        Path tepHanhKhach = THU_MUC.resolve("hanhkhach.txt");
        if (Files.exists(tepChuyenBay) && Files.exists(tepHanhKhach)) {
            docChuyenBay(cb);
            docHanhKhach(hk);
            docVe(ve, cb, hk);
            docDonDat(don, ve, hk);
            napDaiLyDemo(dl);
        } else {
            napDuLieuDemo(cb, hk, dl);
            luuChuyenBay(cb);
            luuHanhKhach(hk);
            luuVe(ve);
            luuDonDat(don);
        }
    }

    /** Tao du lieu mau: 10 chuyen bay, 20 hanh khach va 3 dai ly. */
    public static void napDuLieuDemo(QuanLyChuyenBay cb, QuanLyHanhKhach hk, QuanLyDaiLy dl) {
        LocalDateTime ngay = LocalDateTime.now().plusDays(3).withHour(8).withMinute(30);
        String[][] tuyen = {{"Ha Noi","TP Ho Chi Minh"},{"TP Ho Chi Minh","Da Nang"},
            {"Ha Noi","Da Nang"},{"Da Nang","Ha Noi"},{"TP Ho Chi Minh","Phu Quoc"},
            {"Ha Noi","Nha Trang"},{"Can Tho","Ha Noi"},{"Hai Phong","TP Ho Chi Minh"},
            {"TP Ho Chi Minh","Hue"},{"Nha Trang","Ha Noi"}};
        for (int i = 0; i < tuyen.length; i++)
            cb.them(new ChuyenBay("CB"+String.format("%03d",i+1), tuyen[i][0], tuyen[i][1],
                new NgayThang(ngay.plusDays(i)), 1500000+i*100000, 60));
        for (int i = 1; i <= 20; i++)
            hk.them(new HanhKhach("HK"+String.format("%03d",i), "Hanh Khach "+i,
                "090000"+String.format("%04d",i), "khach"+i+"@mail.com"));
        napDaiLyDemo(dl);
    }

    private static void napDaiLyDemo(QuanLyDaiLy dl) {
        dl.them(new DaiLy("DL001","Dai ly Ha Noi","0901111111","hn@ve.vn",0));
        dl.them(new DaiLy("DL002","Dai ly Sai Gon","0902222222","sg@ve.vn",0));
        dl.them(new DaiLy("DL003","Dai ly Da Nang","0903333333","dn@ve.vn",0));
    }

    private static void ghi(String ten, StringBuilder noiDung) throws IOException {
        Files.createDirectories(THU_MUC);
        Files.write(THU_MUC.resolve(ten), noiDung.toString().getBytes(StandardCharsets.UTF_8));
    }
    private static String[] cacCot(String dong, int soCot) throws IOException {
        String[] cot = dong.split("\\|", -1);
        if (cot.length != soCot) throw new IOException("Dong du lieu sai dinh dang: " + dong);
        return cot;
    }
    private static java.util.List<String> docDong(String ten) throws IOException {
        Path tep = THU_MUC.resolve(ten);
        return Files.exists(tep) ? Files.readAllLines(tep, StandardCharsets.UTF_8) : java.util.Collections.emptyList();
    }

    public static void luuChuyenBay(QuanLyChuyenBay q) throws IOException {
        StringBuilder b=new StringBuilder();
        for(int i=0;i<q.getSoLuong();i++){ChuyenBay c=q.get(i);b.append(c.getMa()).append('|').append(c.getNoiDi()).append('|').append(c.getNoiDen()).append('|').append(c.getKhoiHanh()).append('|').append(c.getGiaCoBan()).append('|').append(c.getSoGheTrong()).append('/').append(c.getTongSoGhe()).append('\n');}
        ghi("chuyenbay.txt",b);
    }
    public static void docChuyenBay(QuanLyChuyenBay q) throws IOException {
        for(String dong:docDong("chuyenbay.txt")){if(dong.trim().isEmpty())continue;String[] c=cacCot(dong,6);String[] ghe=c[5].split("/");
            LocalDateTime ngay=LocalDateTime.parse(c[3],DINH_DANG_NGAY);
            q.them(new ChuyenBay(c[0],c[1],c[2],new NgayThang(ngay),Double.parseDouble(c[4]),Integer.parseInt(ghe[1])));}
    }
    public static void luuHanhKhach(QuanLyHanhKhach q) throws IOException {
        StringBuilder b=new StringBuilder();for(int i=0;i<q.getSoLuong();i++){HanhKhach h=q.get(i);b.append(h.getMa()).append('|').append(h.getHoTen()).append('|').append(h.getSoDienThoai()).append('|').append(h.getEmail()).append('\n');}ghi("hanhkhach.txt",b);
    }
    public static void docHanhKhach(QuanLyHanhKhach q) throws IOException {
        for(String dong:docDong("hanhkhach.txt")){if(dong.trim().isEmpty())continue;String[] c=cacCot(dong,4);q.them(new HanhKhach(c[0],c[1],c[2],c[3]));}
    }
    public static void luuVe(QuanLyVe q) throws IOException {
        StringBuilder b=new StringBuilder();for(int i=0;i<q.getSoLuong();i++){VeMayBay v=q.get(i);b.append(v.getMaVe()).append('|').append(v.getChuyenBay().getMa()).append('|').append(v.getHanhKhach().getMa()).append('|').append(v.getSoGhe()).append('|').append(v.getClass().getSimpleName()).append('\n');}ghi("ve.txt",b);
    }
    public static void docVe(QuanLyVe q, QuanLyChuyenBay cb, QuanLyHanhKhach hk) throws IOException {
        for(String dong:docDong("ve.txt")){if(dong.trim().isEmpty())continue;String[] c=cacCot(dong,5);ChuyenBay chuyenBay=cb.tim(c[1]);HanhKhach hanhKhach=timHanhKhachTheoMa(hk,c[2]);
            if(chuyenBay==null||hanhKhach==null)throw new IOException("Ve tham chieu ma chuyen bay/hanh khach khong ton tai: "+c[0]);
            int ghe=Integer.parseInt(c[3]);VeMayBay v;
            switch(c[4]){case "VeThuongGia":v=new VeThuongGia(c[0],chuyenBay,hanhKhach,ghe);break;case "VeHangNhat":v=new VeHangNhat(c[0],chuyenBay,hanhKhach,ghe);break;default:v=new VePhoThong(c[0],chuyenBay,hanhKhach,ghe);}
            if(!chuyenBay.datGhe(ghe))throw new IOException("Ghe bi trung hoac khong hop le trong tep ve: "+c[0]);q.them(v);}
    }
    public static void luuDonDat(QuanLyDonDatVe q) throws IOException {
        StringBuilder b=new StringBuilder();for(int i=0;i<q.getSoLuong();i++){DonDatVe d=q.get(i);b.append(d.getMa()).append('|').append(d.getHanhKhach().getMa()).append('|').append(d.getVe().getMaVe()).append('|').append(d.getNgayDat()).append('\n');}ghi("dondat.txt",b);
    }
    public static void docDonDat(QuanLyDonDatVe q, QuanLyVe ve, QuanLyHanhKhach hk) throws IOException {
        for(String dong:docDong("dondat.txt")){if(dong.trim().isEmpty())continue;String[] c=cacCot(dong,4);VeMayBay v=ve.tim(c[2]);HanhKhach h=timHanhKhachTheoMa(hk,c[1]);
            if(v==null||h==null)throw new IOException("Don dat tham chieu ve/hanh khach khong ton tai: "+c[0]);q.them(new DonDatVe(c[0],h,v));}
    }
    private static HanhKhach timHanhKhachTheoMa(QuanLyHanhKhach q,String ma){for(int i=0;i<q.getSoLuong();i++)if(q.get(i).getMa().equalsIgnoreCase(ma))return q.get(i);return null;}
}

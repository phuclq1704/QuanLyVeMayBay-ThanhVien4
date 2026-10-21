import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Doc va ghi cac mang doi tuong vao tep van ban XML.
 * Cac lop du lieu can co constructor khong tham so va getter/setter theo JavaBeans.
 */
public final class XuLyFile {
    private XuLyFile() {
    }

    public static boolean ghiVeXuongFile(VeMayBay[] danhSach, int soLuong, String tenFile) {
        return ghiMang(danhSach, soLuong, tenFile);
    }

    public static VeMayBay[] docVeTuFile(String tenFile) {
        return docMang(tenFile, VeMayBay.class);
    }

    public static boolean docVaThemVe(String tenFile, QuanLyVe quanLy) {
        if (quanLy == null) {
            return false;
        }
        for (VeMayBay ve : docVeTuFile(tenFile)) {
            if (!quanLy.themVe(ve)) {
                return false;
            }
        }
        return true;
    }

    public static boolean ghiHanhKhachXuongFile(
            HanhKhach[] danhSach, int soLuong, String tenFile) {
        return ghiMang(danhSach, soLuong, tenFile);
    }

    public static HanhKhach[] docHanhKhachTuFile(String tenFile) {
        return docMang(tenFile, HanhKhach.class);
    }

    public static boolean docVaThemHanhKhach(String tenFile, QuanLyHanhKhach quanLy) {
        if (quanLy == null) {
            return false;
        }
        for (HanhKhach hk : docHanhKhachTuFile(tenFile)) {
            if (!quanLy.themHanhKhach(hk)) {
                return false;
            }
        }
        return true;
    }

    public static boolean ghiDaiLyXuongFile(DaiLy[] danhSach, int soLuong, String tenFile) {
        return ghiMang(danhSach, soLuong, tenFile);
    }

    public static DaiLy[] docDaiLyTuFile(String tenFile) {
        return docMang(tenFile, DaiLy.class);
    }

    public static boolean docVaThemDaiLy(String tenFile, QuanLyDaiLy quanLy) {
        if (quanLy == null) {
            return false;
        }
        for (DaiLy dl : docDaiLyTuFile(tenFile)) {
            if (!quanLy.themDaiLy(dl)) {
                return false;
            }
        }
        return true;
    }

    public static boolean ghiChuyenBayXuongFile(
            ChuyenBay[] danhSach, int soLuong, String tenFile) {
        return ghiMang(danhSach, soLuong, tenFile);
    }

    public static ChuyenBay[] docChuyenBayTuFile(String tenFile) {
        return docMang(tenFile, ChuyenBay.class);
    }

    public static boolean docVaThemChuyenBay(String tenFile, QuanLyChuyenBay quanLy) {
        if (quanLy == null) {
            return false;
        }
        for (ChuyenBay cb : docChuyenBayTuFile(tenFile)) {
            if (!quanLy.themChuyenBay(cb)) {
                return false;
            }
        }
        return true;
    }

    public static boolean ghiDonDatVeXuongFile(
            DonDatVe[] danhSach, int soLuong, String tenFile) {
        return ghiMang(danhSach, soLuong, tenFile);
    }

    public static DonDatVe[] docDonDatVeTuFile(String tenFile) {
        return docMang(tenFile, DonDatVe.class);
    }

    private static boolean ghiMang(Object[] danhSach, int soLuong, String tenFile) {
        if (danhSach == null || tenFile == null || tenFile.trim().isEmpty()
                || soLuong < 0 || soLuong > danhSach.length) {
            return false;
        }

        Path duongDan = Paths.get(tenFile);
        Path thuMucCha = duongDan.toAbsolutePath().getParent();
        if (thuMucCha != null) {
            try {
                Files.createDirectories(thuMucCha);
            } catch (IOException e) {
                return false;
            }
        }

        Object[] mangCanGhi = (Object[]) Array.newInstance(
                danhSach.getClass().getComponentType(), soLuong);
        System.arraycopy(danhSach, 0, mangCanGhi, 0, soLuong);

        Exception[] loiBean = new Exception[1];
        ExceptionListener boNgheLoi = loi -> {
            if (loiBean[0] == null) {
                loiBean[0] = loi;
            }
        };

        try (XMLEncoder encoder = new XMLEncoder(
                new BufferedOutputStream(Files.newOutputStream(duongDan)))) {
            encoder.setExceptionListener(boNgheLoi);
            encoder.writeObject(mangCanGhi);
        } catch (IOException | RuntimeException e) {
            return false;
        }
        return loiBean[0] == null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] docMang(String tenFile, Class<T> kieuPhanTu) {
        if (tenFile == null || tenFile.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten file khong duoc de trong.");
        }

        Path duongDan = Paths.get(tenFile);
        if (!Files.exists(duongDan)) {
            return (T[]) Array.newInstance(kieuPhanTu, 0);
        }

        Exception[] loiBean = new Exception[1];
        ExceptionListener boNgheLoi = loi -> {
            if (loiBean[0] == null) {
                loiBean[0] = loi;
            }
        };

        try (XMLDecoder decoder = new XMLDecoder(
                new BufferedInputStream(Files.newInputStream(duongDan)), null, boNgheLoi)) {
            Object duLieu = decoder.readObject();
            if (loiBean[0] != null) {
                throw new IllegalStateException("Khong doc duoc du lieu XML.", loiBean[0]);
            }
            if (duLieu == null || !duLieu.getClass().isArray()) {
                throw new IllegalStateException("File khong chua mot mang doi tuong.");
            }

            int doDai = Array.getLength(duLieu);
            T[] ketQua = (T[]) Array.newInstance(kieuPhanTu, doDai);
            for (int i = 0; i < doDai; i++) {
                Object phanTu = Array.get(duLieu, i);
                if (phanTu != null && !kieuPhanTu.isInstance(phanTu)) {
                    throw new IllegalStateException(
                            "Phan tu thu " + i + " khong phai kieu " + kieuPhanTu.getSimpleName());
                }
                ketQua[i] = kieuPhanTu.cast(phanTu);
            }
            return ketQua;
        } catch (IOException | RuntimeException e) {
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            throw new IllegalStateException("Loi khi doc file: " + tenFile, e);
        }
    }
}

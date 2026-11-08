import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** Nhập/xuất dữ liệu SkyDesk dưới dạng sổ làm việc Excel .xlsx. */
public final class ExcelIO {
    private static final String MAIN_NS = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";
    private static final String REL_NS = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";
    private static final String PKG_REL_NS = "http://schemas.openxmlformats.org/package/2006/relationships";
    private static final String[] SHEET_NAMES = {
        "ChuyenBay", "HanhKhach", "NhanVien", "DaiLy", "VeMayBay", "DonDatVe"
    };
    private static final String[][] HEADERS = {
        {"MaChuyenBay", "DiemDi", "DiemDen", "NgayDi", "NgayDen", "TongSoGhe", "SoGheTrong"},
        {"MaHanhKhach", "HoTen", "SoDienThoai", "HoChieu", "Status"},
        {"MaNhanVien", "MaSo", "HoTen", "SoDienThoai", "Status"},
        {"MaDaiLy", "TenDaiLy", "SoDienThoai", "LoaiDaiLy", "DuNo", "Status"},
        {"MaVe", "LoaiVe", "GiaGoc", "PhuPhi", "MaChuyenBay", "MaHanhKhach", "DuocDoiTra", "TongSoVe", "DaHuy"},
        {"MaDon", "MaHanhKhach", "MaChuyenBay", "MaVe", "MaNhanVien", "PhuongThucThanhToan", "NgayDat", "DaHuy", "DaXacNhan", "TongTien"}
    };
    private static final DateTimeFormatter DATE_TIME_OUTPUT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_OUTPUT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ExcelIO() {
    }

    /** Xuất toàn bộ dữ liệu nghiệp vụ thành một workbook có sáu sheet. */
    public static void xuatTatCa(Path tep,
            QuanLyChuyenBay cb, QuanLyHanhKhach hk, QuanLyNhanVien nv,
            QuanLyDaiLy dl, QuanLyVe ve, QuanLyDonDatVe don) throws IOException {
        if (tep == null) throw new IOException("Chưa chọn tệp Excel để lưu.");

        Object[][][] duLieu = new Object[SHEET_NAMES.length][][];
        duLieu[0] = taoDuLieuChuyenBay(cb.getDanhSach());
        duLieu[1] = taoDuLieuHanhKhach(hk.getDanhSach());
        duLieu[2] = taoDuLieuNhanVien(nv.getDanhSach());
        duLieu[3] = taoDuLieuDaiLy(dl.getDanhSach());
        duLieu[4] = taoDuLieuVe(ve.getDanhSach(), ve);
        duLieu[5] = taoDuLieuDon(don.getDanhSach());

        Path parent = tep.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(tep), StandardCharsets.UTF_8)) {
            ghiEntry(zip, "[Content_Types].xml", taoContentTypes());
            ghiEntry(zip, "_rels/.rels", taoPackageRels());
            ghiEntry(zip, "xl/workbook.xml", taoWorkbook());
            ghiEntry(zip, "xl/_rels/workbook.xml.rels", taoWorkbookRels());
            for (int i = 0; i < SHEET_NAMES.length; i++) {
                ghiEntry(zip, "xl/worksheets/sheet" + (i + 1) + ".xml",
                        taoWorksheet(duLieu[i]));
            }
        }
    }

    /**
     * Nhập workbook theo chế độ gộp an toàn: chỉ thêm mã chưa có; mã đã có
     * được giữ nguyên, không ghi đè. Trả về tổng số bản ghi mới được thêm.
     */
    public static int nhapTatCa(Path tep,
            QuanLyChuyenBay cb, QuanLyHanhKhach hk, QuanLyNhanVien nv,
            QuanLyDaiLy dl, QuanLyVe ve, QuanLyDonDatVe don) throws IOException {
        if (tep == null || !Files.isRegularFile(tep)) throw new IOException("Không tìm thấy tệp Excel.");

        String[][][] bang = docWorkbook(tep);
        for (int i = 0; i < bang.length; i++) kiemTraTieuDe(bang[i], HEADERS[i], SHEET_NAMES[i]);
        for (int i = 0; i < bang.length; i++) kiemTraMaTrongBang(bang[i], 0, SHEET_NAMES[i]);
        kiemTraMaTrongBang(bang[5], 3, "Đơn đặt vé (mã vé)");

        ChuyenBay[] themChuyenBay = new ChuyenBay[bang[0].length];
        int soChuyenBayMoi = 0;
        for (int r = 1; r < bang[0].length; r++) {
            String[] row = bang[0][r];
            if (dongRong(row)) continue;
            String ma = batBuoc(row, 0, SHEET_NAMES[0], r + 1);
            String diemDi = batBuoc(row, 1, SHEET_NAMES[0], r + 1);
            String diemDen = batBuoc(row, 2, SHEET_NAMES[0], r + 1);
            String ngayDi = chuanHoaNgayGio(batBuoc(row, 3, SHEET_NAMES[0], r + 1), SHEET_NAMES[0], r + 1);
            String ngayDen = chuanHoaNgayGio(batBuoc(row, 4, SHEET_NAMES[0], r + 1), SHEET_NAMES[0], r + 1);
            int tongGhe = soNguyen(row, 5, SHEET_NAMES[0], r + 1);
            int gheTrong = soNguyen(row, 6, SHEET_NAMES[0], r + 1);
            if (tongGhe < 1 || gheTrong < 0 || gheTrong > tongGhe) {
                throw loiDong(SHEET_NAMES[0], r + 1, "số ghế không hợp lệ");
            }
            if (cb.timChuyenBayTheoMa(ma) == null) {
                themChuyenBay[soChuyenBayMoi++] = new ChuyenBay(gheTrong > 0, ma,
                        diemDi, diemDen, ngayDi, ngayDen, tongGhe, gheTrong);
            }
        }

        HanhKhach[] themHanhKhach = new HanhKhach[bang[1].length];
        int soHanhKhachMoi = 0;
        for (int r = 1; r < bang[1].length; r++) {
            String[] row = bang[1][r];
            if (dongRong(row)) continue;
            String ma = batBuoc(row, 0, SHEET_NAMES[1], r + 1);
            String ten = batBuoc(row, 1, SHEET_NAMES[1], r + 1);
            String dienThoai = batBuoc(row, 2, SHEET_NAMES[1], r + 1);
            String hoChieu = batBuoc(row, 3, SHEET_NAMES[1], r + 1);
            boolean status = giaTriBoolean(row, 4, true, SHEET_NAMES[1], r + 1);
            if (hk.timHanhKhachTheoMa(ma) == null) {
                themHanhKhach[soHanhKhachMoi++] = new HanhKhach(status, ma, ten, dienThoai, hoChieu);
            }
        }

        NhanVien[] themNhanVien = new NhanVien[bang[2].length];
        int soNhanVienMoi = 0;
        for (int r = 1; r < bang[2].length; r++) {
            String[] row = bang[2][r];
            if (dongRong(row)) continue;
            String maNV = batBuoc(row, 0, SHEET_NAMES[2], r + 1);
            String maSo = batBuoc(row, 1, SHEET_NAMES[2], r + 1);
            String ten = batBuoc(row, 2, SHEET_NAMES[2], r + 1);
            String dienThoai = batBuoc(row, 3, SHEET_NAMES[2], r + 1);
            boolean status = giaTriBoolean(row, 4, true, SHEET_NAMES[2], r + 1);
            if (timNhanVien(nv, themNhanVien, soNhanVienMoi, maNV) == null) {
                // Mật khẩu không được đưa vào workbook; tài khoản vai trò mẫu vẫn hoạt động riêng.
                themNhanVien[soNhanVienMoi++] = new NhanVien(status, maSo, ten,
                        dienThoai, maNV, "");
            }
        }

        DaiLy[] themDaiLy = new DaiLy[bang[3].length];
        int soDaiLyMoi = 0;
        for (int r = 1; r < bang[3].length; r++) {
            String[] row = bang[3][r];
            if (dongRong(row)) continue;
            String ma = batBuoc(row, 0, SHEET_NAMES[3], r + 1);
            String ten = batBuoc(row, 1, SHEET_NAMES[3], r + 1);
            String dienThoai = batBuoc(row, 2, SHEET_NAMES[3], r + 1);
            String loai = batBuoc(row, 3, SHEET_NAMES[3], r + 1);
            double duNo = soThuc(row, 4, SHEET_NAMES[3], r + 1);
            if (duNo < 0) throw loiDong(SHEET_NAMES[3], r + 1, "dư nợ không được âm");
            boolean status = giaTriBoolean(row, 5, true, SHEET_NAMES[3], r + 1);
            if (dl.timDaiLyTheoMa(ma) == null) {
                themDaiLy[soDaiLyMoi++] = new DaiLy(status, ma, ten, dienThoai, loai, duNo);
            }
        }

        VeMayBay[] themVe = new VeMayBay[bang[4].length];
        boolean[] veDaHuy = new boolean[bang[4].length];
        int soVeMoi = 0;
        for (int r = 1; r < bang[4].length; r++) {
            String[] row = bang[4][r];
            if (dongRong(row)) continue;
            String maVe = batBuoc(row, 0, SHEET_NAMES[4], r + 1);
            String loai = batBuoc(row, 1, SHEET_NAMES[4], r + 1);
            double gia = soThuc(row, 2, SHEET_NAMES[4], r + 1);
            double phi = soThuc(row, 3, SHEET_NAMES[4], r + 1);
            String maChuyen = batBuoc(row, 4, SHEET_NAMES[4], r + 1);
            String maKhach = batBuoc(row, 5, SHEET_NAMES[4], r + 1);
            boolean doiTra = giaTriBoolean(row, 6, true, SHEET_NAMES[4], r + 1);
            int tongSoVe = soNguyen(row, 7, SHEET_NAMES[4], r + 1);
            boolean daHuy = giaTriBoolean(row, 8, false, SHEET_NAMES[4], r + 1);
            if (gia <= 0 || phi < 0 || tongSoVe < 1) {
                throw loiDong(SHEET_NAMES[4], r + 1, "giá vé, phụ phí hoặc số lượng vé không hợp lệ");
            }
            ChuyenBay chuyenBay = timChuyenBay(cb, themChuyenBay, soChuyenBayMoi, maChuyen);
            HanhKhach hanhKhach = timHanhKhach(hk, themHanhKhach, soHanhKhachMoi, maKhach);
            if (chuyenBay == null || hanhKhach == null) {
                throw loiDong(SHEET_NAMES[4], r + 1, "mã chuyến bay hoặc hành khách không tồn tại");
            }
            VeMayBay veCu = ve.timVeTheoMa(maVe);
            if (veCu == null) {
                VeMayBay veMoi;
                if (laVeThuongGia(loai)) veMoi = new VeThuongGia(maVe, gia, phi);
                else if (laVePhoThong(loai)) veMoi = new VePhoThong(maVe, gia, phi);
                else throw loiDong(SHEET_NAMES[4], r + 1, "loại vé phải là Phổ thông hoặc Thương gia");
                veMoi.setChuyenBay(chuyenBay);
                veMoi.setHanhKhach(hanhKhach);
                veMoi.setDuocDoiTra(doiTra);
                veMoi.setTongSoVe(tongSoVe);
                themVe[soVeMoi] = veMoi;
                veDaHuy[soVeMoi] = daHuy;
                soVeMoi++;
            }
        }

        DonDatVe[] themDon = new DonDatVe[bang[5].length];
        boolean[] donDaHuy = new boolean[bang[5].length];
        int soDonMoi = 0;
        for (int r = 1; r < bang[5].length; r++) {
            String[] row = bang[5][r];
            if (dongRong(row)) continue;
            String maDon = batBuoc(row, 0, SHEET_NAMES[5], r + 1);
            String maKhach = batBuoc(row, 1, SHEET_NAMES[5], r + 1);
            String maChuyen = batBuoc(row, 2, SHEET_NAMES[5], r + 1);
            String maVe = batBuoc(row, 3, SHEET_NAMES[5], r + 1);
            String maNV = giaTri(row, 4);
            String phuongThuc = batBuoc(row, 5, SHEET_NAMES[5], r + 1);
            NgayThang ngayDat = docNgay(row, 6, SHEET_NAMES[5], r + 1);
            boolean daHuy = giaTriBoolean(row, 7, false, SHEET_NAMES[5], r + 1);
            boolean daXacNhan = giaTriBoolean(row, 8, true, SHEET_NAMES[5], r + 1);

            HanhKhach hanhKhach = timHanhKhach(hk, themHanhKhach, soHanhKhachMoi, maKhach);
            ChuyenBay chuyenBay = timChuyenBay(cb, themChuyenBay, soChuyenBayMoi, maChuyen);
            VeMayBay veDat = timVe(ve, themVe, soVeMoi, maVe);
            NhanVien nhanVien = maNV.isEmpty() ? null : timNhanVien(nv, themNhanVien, soNhanVienMoi, maNV);
            if (hanhKhach == null || chuyenBay == null || veDat == null) {
                throw loiDong(SHEET_NAMES[5], r + 1, "mã hành khách, chuyến bay hoặc vé không tồn tại");
            }
            if (!maNV.isEmpty() && nhanVien == null) {
                throw loiDong(SHEET_NAMES[5], r + 1, "mã nhân viên lập không tồn tại");
            }
            if (don.timDonTheoMa(maDon) == null && veDaCoDon(don, maVe)) {
                throw loiDong(SHEET_NAMES[5], r + 1, "vé này đã được gắn với một đơn khác");
            }
            String loaiThanhToan = phuongThuc.toLowerCase();
            IThanhToan thanhToan;
            if (loaiThanhToan.contains("thẻ") || loaiThanhToan.contains("the")) {
                thanhToan = new ThanhToanThe("EXCEL-IMPORT");
            } else if (loaiThanhToan.contains("tiền mặt") || loaiThanhToan.contains("tien mat")) {
                thanhToan = new ThanhToanTienMat(veDat.tinhGiaVe());
            } else {
                throw loiDong(SHEET_NAMES[5], r + 1, "phương thức thanh toán không hợp lệ");
            }

            if (don.timDonTheoMa(maDon) == null) {
                DonDatVe donMoi = new DonDatVe(maDon, hanhKhach, hanhKhach,
                        nhanVien, chuyenBay, veDat, thanhToan, ngayDat);
                donMoi.setDaHuy(daHuy);
                if (daXacNhan) donMoi.xacNhanDatVe();
                themDon[soDonMoi] = donMoi;
                donDaHuy[soDonMoi] = daHuy;
                soDonMoi++;
            }
        }

        if (cb.getSoLuong() + soChuyenBayMoi > 1000
                || hk.getSoLuong() + soHanhKhachMoi > 1000
                || nv.getSoLuong() + soNhanVienMoi > 1000
                || dl.getSoLuong() + soDaiLyMoi > 1000) {
            throw new IOException("Danh sách đã gần giới hạn 1000 bản ghi; chưa nhập dữ liệu nào.");
        }

        // Các sheet đã được kiểm tra trước khi bắt đầu thêm dữ liệu.
        for (int i = 0; i < soChuyenBayMoi; i++) {
            if (!cb.themChuyenBay(themChuyenBay[i])) throw new IOException("Không thêm được chuyến bay " + themChuyenBay[i].getSoHieuChuyenBay());
        }
        for (int i = 0; i < soHanhKhachMoi; i++) {
            if (!hk.themHanhKhach(themHanhKhach[i])) throw new IOException("Không thêm được hành khách " + themHanhKhach[i].getMaSo());
        }
        for (int i = 0; i < soNhanVienMoi; i++) {
            if (!nv.themNhanVien(themNhanVien[i])) throw new IOException("Không thêm được nhân viên " + themNhanVien[i].getMaNhanVien());
        }
        for (int i = 0; i < soDaiLyMoi; i++) {
            if (!dl.themDaiLy(themDaiLy[i])) throw new IOException("Không thêm được đại lý " + themDaiLy[i].getMaSo());
        }
        for (int i = 0; i < soVeMoi; i++) {
            if (!ve.themVe(themVe[i])) throw new IOException("Không thêm được vé " + themVe[i].getMaVe());
            if (veDaHuy[i]) ve.capNhatTrangThaiHuy(themVe[i].getMaVe(), true);
        }
        for (int i = 0; i < soDonMoi; i++) {
            int soDonTruoc = don.getSoLuong();
            don.themDon(themDon[i]);
            if (don.getSoLuong() == soDonTruoc) throw new IOException("Không thêm được đơn " + themDon[i].getMaDonDat());
            if (donDaHuy[i] && themDon[i].getVe() != null) {
                ve.capNhatTrangThaiHuy(themDon[i].getVe().getMaVe(), true);
            }
        }
        return soChuyenBayMoi + soHanhKhachMoi + soNhanVienMoi
                + soDaiLyMoi + soVeMoi + soDonMoi;
    }

    private static Object[][] taoDuLieuChuyenBay(ChuyenBay[] ds) {
        Object[][] rows = bangRong(HEADERS[0], ds.length);
        for (int i = 0; i < ds.length; i++) {
            ChuyenBay x = ds[i];
            rows[i + 1] = new Object[]{x.getSoHieuChuyenBay(), x.getDiemDi(), x.getDiemDen(),
                    x.getNgayDi(), x.getNgayDen(), x.getTongSoGhe(), x.getSoGheTrong()};
        }
        return rows;
    }

    private static Object[][] taoDuLieuHanhKhach(HanhKhach[] ds) {
        Object[][] rows = bangRong(HEADERS[1], ds.length);
        for (int i = 0; i < ds.length; i++) {
            HanhKhach x = ds[i];
            rows[i + 1] = new Object[]{x.getMaSo(), x.getHoTen(), x.getSoDienThoai(),
                    x.getHochieu(), x.isStatus()};
        }
        return rows;
    }

    private static Object[][] taoDuLieuNhanVien(NhanVien[] ds) {
        Object[][] rows = bangRong(HEADERS[2], ds.length);
        for (int i = 0; i < ds.length; i++) {
            NhanVien x = ds[i];
            // Không xuất MK của nhân viên ra workbook.
            rows[i + 1] = new Object[]{x.getMaNhanVien(), x.getMaSo(), x.getHoTen(),
                    x.getSoDienThoai(), x.isStatus()};
        }
        return rows;
    }

    private static Object[][] taoDuLieuDaiLy(DaiLy[] ds) {
        Object[][] rows = bangRong(HEADERS[3], ds.length);
        for (int i = 0; i < ds.length; i++) {
            DaiLy x = ds[i];
            rows[i + 1] = new Object[]{x.getMaSo(), x.getHoTen(), x.getSoDienThoai(),
                    x.getLoaiDaiLy(), x.getDuNo() == null ? 0 : x.getDuNo(), x.isStatus()};
        }
        return rows;
    }

    private static Object[][] taoDuLieuVe(VeMayBay[] ds, QuanLyVe quanLy) {
        Object[][] rows = bangRong(HEADERS[4], ds.length);
        for (int i = 0; i < ds.length; i++) {
            VeMayBay x = ds[i];
            String loai;
            double phi;
            if (x instanceof VeThuongGia) {
                loai = "Thương gia";
                phi = ((VeThuongGia) x).getPhiPhongCho();
            } else {
                loai = "Phổ thông";
                phi = x instanceof VePhoThong ? ((VePhoThong) x).getPhiHanhLy() : 0;
            }
            ChuyenBay chuyenBay = x.getChuyenBay();
            HanhKhach hanhKhach = x.getHanhKhach();
            rows[i + 1] = new Object[]{x.getMaVe(), loai, x.getGiaGoc(), phi,
                    chuyenBay == null ? "" : chuyenBay.getSoHieuChuyenBay(),
                    hanhKhach == null ? "" : hanhKhach.getMaSo(), x.isDuocDoiTra(),
                    x.getTongSoVe(), quanLy.isDaHuy(x.getMaVe())};
        }
        return rows;
    }

    private static Object[][] taoDuLieuDon(DonDatVe[] ds) {
        Object[][] rows = bangRong(HEADERS[5], ds.length);
        for (int i = 0; i < ds.length; i++) {
            DonDatVe x = ds[i];
            HanhKhach hk = x.getHanhKhach();
            ChuyenBay cb = x.getChuyenBay();
            VeMayBay ve = x.getVe();
            NhanVien nv = x.getNhanVienLap();
            rows[i + 1] = new Object[]{x.getMaDonDat(), hk == null ? "" : hk.getMaSo(),
                    cb == null ? "" : cb.getSoHieuChuyenBay(), ve == null ? "" : ve.getMaVe(),
                    nv == null ? "" : nv.getMaNhanVien(), x.getPhuongThucThanhToan(),
                    x.getNgayDat() == null ? "" : x.getNgayDat().toString(), x.isDaHuy(),
                    x.isDaXacNhan(), x.getSoTienThanhToan()};
        }
        return rows;
    }

    private static Object[][] bangRong(String[] headers, int soDong) {
        Object[][] rows = new Object[soDong + 1][headers.length];
        for (int i = 0; i < headers.length; i++) rows[0][i] = headers[i];
        return rows;
    }

    private static String taoContentTypes() {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">")
                .append("<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>")
                .append("<Default Extension=\"xml\" ContentType=\"application/xml\"/>")
                .append("<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>");
        for (int i = 0; i < SHEET_NAMES.length; i++) {
            xml.append("<Override PartName=\"/xl/worksheets/sheet").append(i + 1)
                    .append(".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>");
        }
        return xml.append("</Types>").toString();
    }

    private static String taoPackageRels() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"" + PKG_REL_NS + "\"><Relationship Id=\"rId1\" "
                + "Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" "
                + "Target=\"xl/workbook.xml\"/></Relationships>";
    }

    private static String taoWorkbook() {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<workbook xmlns=\"").append(MAIN_NS).append("\" xmlns:r=\"")
                .append(REL_NS).append("\"><sheets>");
        for (int i = 0; i < SHEET_NAMES.length; i++) {
            xml.append("<sheet name=\"").append(xmlEscape(SHEET_NAMES[i])).append("\" sheetId=\"")
                    .append(i + 1).append("\" r:id=\"rId").append(i + 1).append("\"/>");
        }
        return xml.append("</sheets></workbook>").toString();
    }

    private static String taoWorkbookRels() {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<Relationships xmlns=\"").append(PKG_REL_NS).append("\">");
        for (int i = 0; i < SHEET_NAMES.length; i++) {
            xml.append("<Relationship Id=\"rId").append(i + 1)
                    .append("\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet")
                    .append(i + 1).append(".xml\"/>");
        }
        return xml.append("</Relationships>").toString();
    }

    private static String taoWorksheet(Object[][] rows) {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<worksheet xmlns=\"").append(MAIN_NS).append("\"><sheetData>");
        for (int r = 0; r < rows.length; r++) {
            xml.append("<row r=\"").append(r + 1).append("\">");
            for (int c = 0; c < rows[r].length; c++) {
                Object value = rows[r][c];
                if (value == null || value.toString().isEmpty()) continue;
                String ref = tenCot(c + 1) + (r + 1);
                if (value instanceof Boolean) {
                    xml.append("<c r=\"").append(ref).append("\" t=\"b\"><v>")
                            .append((Boolean) value ? "1" : "0").append("</v></c>");
                } else if (value instanceof Number) {
                    double number = ((Number) value).doubleValue();
                    if (!Double.isFinite(number)) throw new IllegalArgumentException("Không thể xuất số không hữu hạn.");
                    xml.append("<c r=\"").append(ref).append("\"><v>")
                            .append(value.toString()).append("</v></c>");
                } else {
                    xml.append("<c r=\"").append(ref).append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">")
                            .append(xmlEscape(value.toString())).append("</t></is></c>");
                }
            }
            xml.append("</row>");
        }
        return xml.append("</sheetData></worksheet>").toString();
    }

    private static String tenCot(int column) {
        StringBuilder result = new StringBuilder();
        while (column > 0) {
            int remainder = (column - 1) % 26;
            result.insert(0, (char) ('A' + remainder));
            column = (column - 1) / 26;
        }
        return result.toString();
    }

    private static void ghiEntry(ZipOutputStream zip, String name, String contents) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(contents.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String xmlEscape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    private static String[][][] docWorkbook(Path tep) throws IOException {
        String[][][] sheets = new String[SHEET_NAMES.length][][];
        try (ZipFile zip = new ZipFile(tep.toFile(), StandardCharsets.UTF_8)) {
            Document workbook = docXml(zip, "xl/workbook.xml", true);
            Document relationships = docXml(zip, "xl/_rels/workbook.xml.rels", true);
            String[] sharedStrings = docSharedStrings(zip);
            NodeList relationNodes = relationships.getElementsByTagNameNS("*", "Relationship");
            String[] relationIds = new String[relationNodes.getLength()];
            String[] relationTargets = new String[relationNodes.getLength()];
            for (int i = 0; i < relationNodes.getLength(); i++) {
                Element relation = (Element) relationNodes.item(i);
                relationIds[i] = relation.getAttribute("Id");
                relationTargets[i] = relation.getAttribute("Target");
            }

            NodeList sheetNodes = workbook.getElementsByTagNameNS("*", "sheet");
            for (int i = 0; i < sheetNodes.getLength(); i++) {
                Element sheet = (Element) sheetNodes.item(i);
                String name = sheet.getAttribute("name");
                int index = indexOf(SHEET_NAMES, name);
                if (index < 0) continue;
                String relationId = sheet.getAttributeNS(REL_NS, "id");
                if (relationId.isEmpty()) relationId = sheet.getAttribute("r:id");
                String target = null;
                for (int j = 0; j < relationIds.length; j++) {
                    if (relationIds[j].equals(relationId)) {
                        target = relationTargets[j];
                        break;
                    }
                }
                if (target == null) throw new IOException("Không tìm thấy sheet " + name + " trong workbook.");
                String entry = target.startsWith("/") ? target.substring(1)
                        : target.startsWith("xl/") ? target : "xl/" + target;
                sheets[index] = docWorksheet(zip, entry, HEADERS[index].length, sharedStrings);
            }
        } catch (SAXException e) {
            throw new IOException("Tệp Excel có cấu trúc XML không hợp lệ.", e);
        }
        for (int i = 0; i < sheets.length; i++) {
            if (sheets[i] == null) throw new IOException("Tệp Excel thiếu trang " + SHEET_NAMES[i] + ".");
        }
        return sheets;
    }

    private static Document docXml(ZipFile zip, String entry, boolean required) throws IOException, SAXException {
        ZipEntry file = zip.getEntry(entry);
        if (file == null) {
            if (required) throw new IOException("Workbook thiếu thành phần " + entry + ".");
            return null;
        }
        try (InputStream input = zip.getInputStream(file)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            try {
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (Exception e) {
                throw new IOException("Không thể cấu hình bộ đọc XML an toàn.", e);
            }
            return factory.newDocumentBuilder().parse(input);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new IOException("Không thể đọc cấu trúc XML của Excel.", e);
        }
    }

    private static String[] docSharedStrings(ZipFile zip) throws IOException, SAXException {
        Document shared = docXml(zip, "xl/sharedStrings.xml", false);
        if (shared == null) return new String[0];
        NodeList items = shared.getElementsByTagNameNS("*", "si");
        String[] result = new String[items.getLength()];
        for (int i = 0; i < items.getLength(); i++) {
            NodeList textNodes = ((Element) items.item(i)).getElementsByTagNameNS("*", "t");
            StringBuilder text = new StringBuilder();
            for (int j = 0; j < textNodes.getLength(); j++) text.append(textNodes.item(j).getTextContent());
            result[i] = text.toString();
        }
        return result;
    }

    private static String[][] docWorksheet(ZipFile zip, String entry, int columnCount,
                                           String[] sharedStrings) throws IOException, SAXException {
        Document document = docXml(zip, entry, true);
        NodeList rowNodes = document.getElementsByTagNameNS("*", "row");
        String[][] rows = new String[rowNodes.getLength()][columnCount];
        for (int r = 0; r < rowNodes.getLength(); r++) {
            Element row = (Element) rowNodes.item(r);
            NodeList cells = row.getElementsByTagNameNS("*", "c");
            for (int c = 0; c < cells.getLength(); c++) {
                Element cell = (Element) cells.item(c);
                int column = cotTuThamChieu(cell.getAttribute("r"));
                if (column < 0 || column >= columnCount) continue;
                String type = cell.getAttribute("t");
                String value;
                if ("inlineStr".equals(type)) {
                    NodeList textNodes = cell.getElementsByTagNameNS("*", "t");
                    StringBuilder text = new StringBuilder();
                    for (int j = 0; j < textNodes.getLength(); j++) text.append(textNodes.item(j).getTextContent());
                    value = text.toString();
                } else {
                    NodeList values = cell.getElementsByTagNameNS("*", "v");
                    value = values.getLength() == 0 ? "" : values.item(0).getTextContent();
                    if ("s".equals(type) && !value.isEmpty()) {
                        int sharedIndex = Integer.parseInt(value);
                        if (sharedIndex < 0 || sharedIndex >= sharedStrings.length) {
                            throw new IOException("Tệp Excel tham chiếu chuỗi dùng chung không hợp lệ.");
                        }
                        value = sharedStrings[sharedIndex];
                    } else if ("b".equals(type)) {
                        value = "1".equals(value) ? "true" : "false";
                    }
                }
                rows[r][column] = value;
            }
        }
        return rows;
    }

    private static int cotTuThamChieu(String reference) {
        int column = 0;
        int letters = 0;
        for (int i = 0; i < reference.length(); i++) {
            char ch = Character.toUpperCase(reference.charAt(i));
            if (ch < 'A' || ch > 'Z') break;
            column = column * 26 + ch - 'A' + 1;
            letters++;
        }
        return letters == 0 ? -1 : column - 1;
    }

    private static int indexOf(String[] values, String key) {
        for (int i = 0; i < values.length; i++) if (values[i].equals(key)) return i;
        return -1;
    }

    private static void kiemTraTieuDe(String[][] rows, String[] expected, String sheet) throws IOException {
        if (rows.length == 0) throw new IOException("Trang " + sheet + " không có hàng tiêu đề.");
        for (int i = 0; i < expected.length; i++) {
            if (!expected[i].equalsIgnoreCase(giaTri(rows[0], i))) {
                throw new IOException("Tiêu đề cột " + (i + 1) + " trong trang " + sheet
                        + " không đúng. Không đổi tên hoặc di chuyển các cột.");
            }
        }
    }

    private static void kiemTraMaTrongBang(String[][] rows, int column, String sheet) throws IOException {
        for (int i = 1; i < rows.length; i++) {
            String key = giaTri(rows[i], column);
            if (key.isEmpty()) {
                if (!dongRong(rows[i])) throw loiDong(sheet, i + 1, "thiếu mã định danh");
                continue;
            }
            for (int j = 1; j < i; j++) {
                if (key.equalsIgnoreCase(giaTri(rows[j], column))) {
                    throw loiDong(sheet, i + 1, "mã bị lặp trong tệp: " + key);
                }
            }
        }
    }

    private static String giaTri(String[] row, int column) {
        return column < row.length && row[column] != null ? row[column].trim() : "";
    }

    private static String batBuoc(String[] row, int column, String sheet, int rowNumber) throws IOException {
        String value = giaTri(row, column);
        if (value.isEmpty()) throw loiDong(sheet, rowNumber, "thiếu giá trị ở cột " + HEADERS[indexOf(SHEET_NAMES, sheet)][column]);
        return value;
    }

    private static boolean dongRong(String[] row) {
        for (String value : row) if (value != null && !value.trim().isEmpty()) return false;
        return true;
    }

    private static int soNguyen(String[] row, int column, String sheet, int rowNumber) throws IOException {
        try {
            return Integer.parseInt(batBuoc(row, column, sheet, rowNumber).replace(",", ""));
        } catch (NumberFormatException e) {
            throw loiDong(sheet, rowNumber, "giá trị số nguyên không hợp lệ");
        }
    }

    private static double soThuc(String[] row, int column, String sheet, int rowNumber) throws IOException {
        String raw = batBuoc(row, column, sheet, rowNumber).replace(" ", "");
        int comma = raw.lastIndexOf(',');
        int dot = raw.lastIndexOf('.');
        if (comma >= 0 && dot >= 0) {
            if (comma > dot) raw = raw.replace(".", "").replace(',', '.');
            else raw = raw.replace(",", "");
        } else if (comma >= 0) {
            int digitsAfter = raw.length() - comma - 1;
            raw = digitsAfter > 0 && digitsAfter <= 2
                    ? raw.substring(0, comma).replace(",", "") + "." + raw.substring(comma + 1)
                    : raw.replace(",", "");
        } else if (dot >= 0 && raw.length() - dot - 1 > 2) {
            raw = raw.replace(".", "");
        }
        try {
            double value = Double.parseDouble(raw);
            if (!Double.isFinite(value)) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException e) {
            throw loiDong(sheet, rowNumber, "giá trị số không hợp lệ");
        }
    }

    private static boolean giaTriBoolean(String[] row, int column, boolean defaultValue,
                                         String sheet, int rowNumber) throws IOException {
        String raw = giaTri(row, column).toLowerCase();
        if (raw.isEmpty()) return defaultValue;
        if (raw.equals("true") || raw.equals("1") || raw.equals("t") || raw.equals("yes")
                || raw.equals("đang hoạt động") || raw.equals("còn hiệu lực")) return true;
        if (raw.equals("false") || raw.equals("0") || raw.equals("f") || raw.equals("no")
                || raw.equals("ngừng hoạt động") || raw.equals("đã hủy")) return false;
        throw loiDong(sheet, rowNumber, "trạng thái phải là true/false");
    }

    private static String chuanHoaNgayGio(String raw, String sheet, int rowNumber) throws IOException {
        String[] patterns = {"dd/MM/yyyy HH:mm", "d/M/yyyy H:mm", "yyyy-MM-dd HH:mm", "yyyy-MM-dd'T'HH:mm"};
        for (String pattern : patterns) {
            try {
                return LocalDateTime.parse(raw, DateTimeFormatter.ofPattern(pattern)).format(DATE_TIME_OUTPUT);
            } catch (DateTimeParseException ignored) {
                // Thử định dạng kế tiếp.
            }
        }
        try {
            double serial = Double.parseDouble(raw);
            long days = (long) Math.floor(serial);
            long seconds = Math.round((serial - days) * 86400d);
            return LocalDate.of(1899, 12, 30).atStartOfDay().plusDays(days)
                    .plusSeconds(seconds).format(DATE_TIME_OUTPUT);
        } catch (RuntimeException ignored) {
            throw loiDong(sheet, rowNumber, "ngày giờ phải theo dd/MM/yyyy HH:mm");
        }
    }

    private static NgayThang docNgay(String[] row, int column, String sheet, int rowNumber) throws IOException {
        String raw = batBuoc(row, column, sheet, rowNumber);
        LocalDate date = null;
        String[] patterns = {"dd/MM/yyyy", "d/M/yyyy", "yyyy-MM-dd"};
        for (String pattern : patterns) {
            try {
                date = LocalDate.parse(raw, DateTimeFormatter.ofPattern(pattern));
                break;
            } catch (DateTimeParseException ignored) {
                // Thử định dạng kế tiếp.
            }
        }
        if (date == null) {
            try {
                double serial = Double.parseDouble(raw);
                date = LocalDate.of(1899, 12, 30).plusDays((long) Math.floor(serial));
            } catch (RuntimeException e) {
                throw loiDong(sheet, rowNumber, "ngày đặt không hợp lệ");
            }
        }
        return new NgayThang(date);
    }

    private static IOException loiDong(String sheet, int row, String message) {
        return new IOException("Trang " + sheet + ", dòng " + row + ": " + message + ".");
    }

    private static boolean laVeThuongGia(String type) {
        String value = type.trim().toLowerCase();
        return value.contains("thương gia") || value.contains("thuong gia") || value.equals("business");
    }

    private static boolean laVePhoThong(String type) {
        String value = type.trim().toLowerCase();
        return value.contains("phổ thông") || value.contains("pho thong") || value.equals("economy");
    }

    private static ChuyenBay timChuyenBay(QuanLyChuyenBay manager, ChuyenBay[] staged,
                                          int count, String code) {
        ChuyenBay value = manager.timChuyenBayTheoMa(code);
        if (value != null) return value;
        for (int i = 0; i < count; i++) {
            if (staged[i].getSoHieuChuyenBay().equalsIgnoreCase(code)) return staged[i];
        }
        return null;
    }

    private static HanhKhach timHanhKhach(QuanLyHanhKhach manager, HanhKhach[] staged,
                                          int count, String code) {
        HanhKhach value = manager.timHanhKhachTheoMa(code);
        if (value != null) return value;
        for (int i = 0; i < count; i++) {
            if (staged[i].getMaSo().equalsIgnoreCase(code)) return staged[i];
        }
        return null;
    }

    private static NhanVien timNhanVien(QuanLyNhanVien manager, NhanVien[] staged,
                                        int count, String code) {
        if (code == null || code.trim().isEmpty()) return null;
        for (NhanVien value : manager.getDanhSach()) {
            if (value.getMaNhanVien().equalsIgnoreCase(code)) return value;
        }
        for (int i = 0; i < count; i++) {
            if (staged[i].getMaNhanVien().equalsIgnoreCase(code)) return staged[i];
        }
        return null;
    }

    private static VeMayBay timVe(QuanLyVe manager, VeMayBay[] staged, int count, String code) {
        VeMayBay value = manager.timVeTheoMa(code);
        if (value != null) return value;
        for (int i = 0; i < count; i++) {
            if (staged[i].getMaVe().equalsIgnoreCase(code)) return staged[i];
        }
        return null;
    }

    private static boolean veDaCoDon(QuanLyDonDatVe manager, String maVe) {
        for (DonDatVe order : manager.getDanhSach()) {
            VeMayBay ticket = order.getVe();
            if (ticket != null && ticket.getMaVe() != null
                    && ticket.getMaVe().equalsIgnoreCase(maVe)) return true;
        }
        return false;
    }
}

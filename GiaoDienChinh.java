import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

/** Giao diện desktop Swing cho chương trình quản lý vé máy bay. */
public class GiaoDienChinh extends JFrame {
    private static final Color NAVY = new Color(24, 40, 63);
    private static final Color NAVY_LIGHT = new Color(38, 59, 87);
    private static final Color TEAL = new Color(24, 142, 125);
    private static final Color BACKGROUND = new Color(244, 247, 250);
    private static final Color TEXT = new Color(39, 52, 69);
    private static final Color MUTED = new Color(112, 126, 143);

    private final QuanLyChuyenBay quanLyChuyenBay;
    private final QuanLyHanhKhach quanLyHanhKhach;
    private final QuanLyDonDatVe quanLyDonDatVe;
    private final QuanLyVe quanLyVe;
    private final QuanLyNhanVien quanLyNhanVien;
    private final QuanLyDaiLy quanLyDaiLy;
    private final VaiTro vaiTro;
    private final String tenTaiKhoan;

    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(pageLayout);
    private final JLabel pageTitle = new JLabel("Tổng quan");
    private final JLabel pageSubtitle = new JLabel("Quản lý hoạt động đặt vé máy bay");
    private final JLabel[] metricValues = new JLabel[3];
    private final JLabel revenueValue = new JLabel("0 VND");

    private final GridTableModel flightModel = new GridTableModel(
            new String[]{"Mã chuyến", "Điểm đi", "Điểm đến", "Ngày đi", "Ngày đến", "Ghế trống / Tổng ghế", "Trạng thái"});
    private final GridTableModel orderModel = new GridTableModel(
            new String[]{"Mã đơn", "Hành khách", "Chuyến bay", "Mã vé", "Thành tiền", "Ngày đặt", "Trạng thái"});
    private final GridTableModel revenueModel = new GridTableModel(
            new String[]{"Chuyến bay", "Số đơn còn hiệu lực", "Doanh thu"});
    private final GridTableModel passengerModel = new GridTableModel(
            new String[]{"Mã hành khách", "Họ tên", "Điện thoại", "Hộ chiếu", "Trạng thái"});
    private final GridTableModel ticketModel = new GridTableModel(
            new String[]{"Mã vé", "Loại vé", "Hành khách", "Chuyến bay", "Giá", "Trạng thái"});
    private final GridTableModel employeeModel = new GridTableModel(
            new String[]{"Mã nhân viên", "Họ tên", "Điện thoại", "Trạng thái"});
    private final GridTableModel agencyModel = new GridTableModel(
            new String[]{"Mã đại lý", "Tên đại lý", "Điện thoại", "Loại", "Dư nợ"});

    private final JTable flightTable = createTable(flightModel,
            "Chưa có chuyến bay", "Bấm “+ Thêm chuyến bay” để tạo chuyến đầu tiên.");
    private final JTable orderTable = createTable(orderModel,
            "Chưa có đơn đặt vé", "Các đơn sẽ xuất hiện sau khi thanh toán thành công.");
    private final JTable revenueTable = createTable(revenueModel,
            "Chưa có dữ liệu doanh thu", "Đơn đặt thành công sẽ được tổng hợp tại đây.");
    private final JTable passengerTable = createTable(passengerModel,
            "Chưa có hành khách", "Thêm hồ sơ mới hoặc đăng ký hành khách khi đặt vé.");
    private final JTable ticketTable = createTable(ticketModel,
            "Chưa có vé máy bay", "Vé sẽ tự xuất hiện sau khi một đơn được thanh toán thành công.");
    private final JTable employeeTable = createTable(employeeModel);
    private final JTable agencyTable = createTable(agencyModel,
            "Chưa có đại lý", "Bấm “+ Thêm đại lý” để lưu hồ sơ và công nợ.");
    private JTextField orderSearchField;
    private JTextField flightSearchField;
    private JTextField ticketSearchField;
    private JTextField passengerSearchField;
    private JTextField employeeSearchField;
    private JTextField agencySearchField;
    private JButton activeNavigationButton;

    public GiaoDienChinh() {
        this(new QuanLyChuyenBay(), new QuanLyHanhKhach(), new QuanLyDonDatVe(),
                new QuanLyVe(), new QuanLyNhanVien(), new QuanLyDaiLy(), true,
                VaiTro.ADMIN, "admin");
    }

    public GiaoDienChinh(VaiTro vaiTro, String tenTaiKhoan) {
        this(new QuanLyChuyenBay(), new QuanLyHanhKhach(), new QuanLyDonDatVe(),
                new QuanLyVe(), new QuanLyNhanVien(), new QuanLyDaiLy(), true,
                vaiTro, tenTaiKhoan);
    }

    public GiaoDienChinh(QuanLyChuyenBay quanLyChuyenBay,
                         QuanLyHanhKhach quanLyHanhKhach,
                         QuanLyDonDatVe quanLyDonDatVe) {
        this(quanLyChuyenBay, quanLyHanhKhach, quanLyDonDatVe,
                new QuanLyVe(), new QuanLyNhanVien(), new QuanLyDaiLy(), false,
                VaiTro.ADMIN, "admin");
    }

    public GiaoDienChinh(QuanLyChuyenBay quanLyChuyenBay,
                         QuanLyHanhKhach quanLyHanhKhach,
                         QuanLyDonDatVe quanLyDonDatVe,
                         QuanLyVe quanLyVe,
                         QuanLyNhanVien quanLyNhanVien,
                         QuanLyDaiLy quanLyDaiLy) {
        this(quanLyChuyenBay, quanLyHanhKhach, quanLyDonDatVe,
                quanLyVe, quanLyNhanVien, quanLyDaiLy, false, VaiTro.ADMIN, "admin");
    }

    private GiaoDienChinh(QuanLyChuyenBay quanLyChuyenBay,
                          QuanLyHanhKhach quanLyHanhKhach,
                          QuanLyDonDatVe quanLyDonDatVe,
                          QuanLyVe quanLyVe,
                          QuanLyNhanVien quanLyNhanVien,
                          QuanLyDaiLy quanLyDaiLy,
                          boolean napDuLieuKhiKhoiDong,
                          VaiTro vaiTro,
                          String tenTaiKhoan) {
        this.quanLyChuyenBay = quanLyChuyenBay;
        this.quanLyHanhKhach = quanLyHanhKhach;
        this.quanLyDonDatVe = quanLyDonDatVe;
        this.quanLyVe = quanLyVe;
        this.quanLyNhanVien = quanLyNhanVien;
        this.quanLyDaiLy = quanLyDaiLy;
        this.vaiTro = vaiTro == null ? VaiTro.CLIENT : vaiTro;
        this.tenTaiKhoan = tenTaiKhoan == null ? "" : tenTaiKhoan;

        if (napDuLieuKhiKhoiDong) {
            boolean loiNapDuLieu = false;
            try {
                XuLyFile.napDuLieu(quanLyChuyenBay, quanLyHanhKhach,
                        quanLyNhanVien, quanLyDaiLy, quanLyVe, quanLyDonDatVe);
            } catch (IOException e) {
                System.err.println("Không nạp được dữ liệu đã lưu: " + e.getMessage());
                loiNapDuLieu = true;
            }
            if (!loiNapDuLieu && tatCaDanhSachRong()) {
                try {
                    XuLyFile.napDuLieuDemo(quanLyChuyenBay, quanLyHanhKhach,
                            quanLyNhanVien, quanLyDaiLy, quanLyVe, quanLyDonDatVe);
                    XuLyFile.luuTatCa(quanLyChuyenBay, quanLyHanhKhach,
                            quanLyNhanVien, quanLyDaiLy, quanLyVe, quanLyDonDatVe);
                } catch (IOException | RuntimeException e) {
                    System.err.println("Không tạo được dữ liệu minh họa: " + e.getMessage());
                }
            }
        }

        setTitle("SkyDesk | Quản lý vé máy bay");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if (luuDuLieu()) dispose();
            }
        });
        setMinimumSize(new Dimension(1020, 660));
        setSize(1240, 800);
        setLocationRelativeTo(null);
        setContentPane(buildApplication());
        refreshAll();
    }

    private boolean tatCaDanhSachRong() {
        return quanLyChuyenBay.getSoLuong() == 0
                && quanLyHanhKhach.getSoLuong() == 0
                && quanLyDonDatVe.getSoLuong() == 0
                && quanLyVe.getSoLuong() == 0
                && quanLyNhanVien.getSoLuong() == 0
                && quanLyDaiLy.getSoLuong() == 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Dùng giao diện Swing mặc định nếu Look and Feel của hệ thống không khả dụng.
            }
            new DangNhap().setVisible(true);
        });
    }

    /** Hiển thị khung chính và trang tổng quan. */
    public void hienThiMenu() {
        showPage("home", "Tổng quan", "Quản lý hoạt động đặt vé máy bay", null);
        setVisible(true);
    }

    private JPanel buildApplication() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(BACKGROUND);
        workspace.add(buildHeader(), BorderLayout.NORTH);

        pageContainer.setBackground(BACKGROUND);
        pageContainer.setBorder(BorderFactory.createEmptyBorder(8, 28, 24, 28));
        pageContainer.add(buildDashboardPage(), "home");
        pageContainer.add(buildFlightsPage(), "flights");
        pageContainer.add(buildPassengersPage(), "passengers");
        pageContainer.add(buildTicketsPage(), "tickets");
        pageContainer.add(buildEmployeesPage(), "employees");
        pageContainer.add(buildAgenciesPage(), "agencies");
        pageContainer.add(buildBookingPage(), "booking");
        pageContainer.add(buildOrdersPage(), "orders");
        pageContainer.add(buildRevenuePage(), "revenue");
        workspace.add(pageContainer, BorderLayout.CENTER);

        root.add(workspace, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(238, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 16, 20, 16));

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel("SKYDESK");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 21));
        JLabel brandCaption = new JLabel("QUẢN LÝ VÉ MÁY BAY");
        brandCaption.setForeground(new Color(166, 185, 205));
        brandCaption.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        brandCaption.setBorder(BorderFactory.createEmptyBorder(8, 30, 0, 0));
        JLabel accountInfo = new JLabel("<html><b>" + tenTaiKhoan.toUpperCase()
                + "</b> • " + vaiTro.getTenHienThi() + "<br>"
                + vaiTro.getMoTaQuyen() + "</html>");
        accountInfo.setForeground(new Color(190, 207, 224));
        accountInfo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        accountInfo.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        brand.add(logo);
        brand.add(brandCaption);
        brand.add(accountInfo);
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel navigation = new JPanel();
        navigation.setOpaque(false);
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
        navigation.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        themMucMenu(navigation, "Tổng quan", "home", "Tổng quan", "Số chuyến bay, đơn còn hiệu lực và doanh thu hiện tại");
        themMucMenu(navigation, "Chuyến bay", "flights", "Chuyến bay", "Tra cứu chuyến bay và số ghế còn trống");
        themMucMenu(navigation, "Hành khách", "passengers", "Hành khách", "Quản lý hồ sơ hành khách");
        themMucMenu(navigation, "Vé máy bay", "tickets", "Vé máy bay", "Tra cứu vé và xem trạng thái hiệu lực");
        themMucMenu(navigation, "Nhân viên", "employees", "Nhân viên", "Quản lý hồ sơ nhân viên");
        themMucMenu(navigation, "Đại lý", "agencies", "Đại lý", "Quản lý thông tin và công nợ đại lý");
        themMucMenu(navigation, "Đặt vé", "booking", "Đặt vé", "Tạo đơn sau khi chuyến bay đã được thêm");
        themMucMenu(navigation, "Đơn đặt vé", "orders", "Đơn đặt vé", "Tìm đơn theo mã đơn hoặc thông tin hành khách");
        themMucMenu(navigation, "Doanh thu", "revenue", "Báo cáo doanh thu", "Tổng giá vé của các đơn còn hiệu lực");
        sidebar.add(navigation, BorderLayout.CENTER);

        JLabel version = new JLabel("QUYỀN: " + vaiTro.getMoTaQuyen().toUpperCase());
        version.setForeground(new Color(145, 164, 185));
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sidebar.add(version, BorderLayout.SOUTH);
        return sidebar;
    }

    private void themMucMenu(JPanel menu, String nhan, String khoa,
                             String tieuDe, String moTa) {
        if (!vaiTro.duocMoTrang(khoa)) return;
        if (menu.getComponentCount() > 0) menu.add(Box.createVerticalStrut(6));
        menu.add(navButton(nhan, khoa, tieuDe, moTa));
    }

    private JButton navButton(String label, String key, String title, String subtitle) {
        JButton button = new JButton(label);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setForeground(new Color(222, 231, 240));
        button.setBackground(NAVY);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setBorder(BorderFactory.createEmptyBorder(13, 16, 13, 12));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(event -> showPage(key, title, subtitle, button));
        return button;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(22, 30, 20, 30));
        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        pageTitle.setForeground(TEXT);
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 23));
        pageSubtitle.setForeground(MUTED);
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pageSubtitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        labels.add(pageTitle);
        labels.add(pageSubtitle);
        header.add(labels, BorderLayout.WEST);

        JLabel badge = new JLabel("  TỰ ĐỘNG LƯU DỮ LIỆU  ");
        badge.setOpaque(true);
        badge.setBackground(new Color(230, 246, 242));
        badge.setForeground(TEAL);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerActions.setOpaque(false);
        if (vaiTro.coToanQuyen()) {
            JButton importExcel = secondaryButton("Nhập Excel");
            importExcel.addActionListener(event -> nhapExcel());
            JButton exportExcel = secondaryButton("Xuất Excel");
            exportExcel.addActionListener(event -> xuatExcel());
            headerActions.add(importExcel);
            headerActions.add(exportExcel);
        }
        JButton logout = secondaryButton("Đăng xuất");
        logout.addActionListener(event -> dangXuat());
        headerActions.add(badge);
        headerActions.add(logout);
        header.add(headerActions, BorderLayout.EAST);
        return header;
    }

    private void dangXuat() {
        if (!luuDuLieu()) return;
        dispose();
        new DangNhap().setVisible(true);
    }

    private void xuatExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất dữ liệu SkyDesk ra Excel");
        chooser.setFileFilter(new FileNameExtensionFilter("Sổ làm việc Excel (*.xlsx)", "xlsx"));
        chooser.setSelectedFile(new java.io.File("SkyDesk_du_lieu.xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path tep = duongDanXlsx(chooser.getSelectedFile().toPath());
        try {
            ExcelIO.xuatTatCa(tep, quanLyChuyenBay, quanLyHanhKhach,
                    quanLyNhanVien, quanLyDaiLy, quanLyVe, quanLyDonDatVe);
            JOptionPane.showMessageDialog(this,
                    "Đã xuất dữ liệu ra file:\n" + tep.toAbsolutePath(),
                    "Xuất Excel thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | RuntimeException e) {
            showError("Không xuất được file Excel.\n" + e.getMessage());
        }
    }

    private void nhapExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file Excel để nhập dữ liệu");
        chooser.setFileFilter(new FileNameExtensionFilter("Sổ làm việc Excel (*.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        int answer = JOptionPane.showConfirmDialog(this,
                "Dữ liệu mới trong file sẽ được thêm vào hệ thống. Mã đã tồn tại sẽ được giữ nguyên, không ghi đè.\n"
                        + "Chỉ nhập file .xlsx có đủ 6 trang theo mẫu SkyDesk. Tiếp tục?",
                "Xác nhận nhập Excel", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;

        try {
            int soBanGhiMoi = ExcelIO.nhapTatCa(chooser.getSelectedFile().toPath(),
                    quanLyChuyenBay, quanLyHanhKhach, quanLyNhanVien,
                    quanLyDaiLy, quanLyVe, quanLyDonDatVe);
            refreshAll();
            if (!luuDuLieu()) return;
            JOptionPane.showMessageDialog(this,
                    soBanGhiMoi == 0
                            ? "Không có bản ghi mới. Các mã đã có được giữ nguyên."
                            : "Đã nhập và lưu " + soBanGhiMoi + " bản ghi mới.",
                    "Nhập Excel hoàn tất", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | RuntimeException e) {
            showError("Không nhập được file Excel.\n" + e.getMessage());
        }
    }

    private Path duongDanXlsx(Path tep) {
        String ten = tep.getFileName().toString();
        if (ten.toLowerCase().endsWith(".xlsx")) return tep;
        return tep.resolveSibling(ten + ".xlsx");
    }

    private JPanel buildDashboardPage() {
        JPanel page = pagePanel();
        page.add(sectionHeading("Bảng điều khiển", "Xem nhanh số chuyến bay, đơn đặt vé và doanh thu."));

        JPanel metrics = new JPanel(new GridLayout(1, 3, 16, 16));
        metrics.setOpaque(false);
        metrics.setBorder(BorderFactory.createEmptyBorder(18, 0, 24, 0));
        metrics.add(metricCard("CHUYẾN BAY", "0", "Số chuyến đã thêm vào chương trình", new Color(55, 122, 214), 0));
        metrics.add(metricCard("ĐƠN CÒN HIỆU LỰC", "0", "Đơn có vé chưa bị hủy", TEAL, 1));
        metrics.add(metricCard("DOANH THU", "0 VND", "Đơn đã hủy không được tính", new Color(222, 137, 49), 2));
        metrics.setPreferredSize(new Dimension(900, 150));
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        page.add(metrics);

        JPanel welcome = cardPanel();
        welcome.setLayout(new BorderLayout(16, 10));
        welcome.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        welcome.setPreferredSize(new Dimension(900, 178));
        welcome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 178));
        JLabel welcomeTitle = new JLabel("Bắt đầu sử dụng");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        welcomeTitle.setForeground(TEXT);
        String thaoTacTheoVaiTro = vaiTro.coToanQuyen()
                ? "Bạn có toàn quyền quản lý chuyến bay, hồ sơ, đơn đặt vé và doanh thu."
                : vaiTro == VaiTro.STAFF
                        ? "Bạn có thể tra cứu chuyến bay, lập vé và xử lý hồ sơ/đơn đặt vé."
                        : "Bạn có thể tra cứu chuyến bay và tạo đơn đặt vé.";
        JLabel welcomeText = new JLabel("<html>Dữ liệu minh họa được nạp tự động khi chương trình chưa có dữ liệu.<br>"
                + thaoTacTheoVaiTro + "<br>"
                + "Mọi thay đổi được lưu vào thư mục <b>data</b> khi có thao tác và lúc đóng chương trình.</html>");
        welcomeText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeText.setForeground(MUTED);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(welcomeTitle);
        text.add(Box.createVerticalStrut(8));
        text.add(welcomeText);
        welcome.add(text, BorderLayout.CENTER);
        JButton addFlight = primaryButton(vaiTro.duocQuanLyChuyenBay()
                ? "Thêm chuyến bay" : "Xem chuyến bay");
        addFlight.setPreferredSize(new Dimension(170, 42));
        addFlight.addActionListener(event -> {
            if (vaiTro.duocQuanLyChuyenBay()) showAddFlightDialog();
            else showPage("flights", "Chuyến bay", "Tra cứu chuyến bay và số ghế còn trống", null);
        });
        JPanel actionArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionArea.setOpaque(false);
        actionArea.add(addFlight);
        welcome.add(actionArea, BorderLayout.EAST);
        page.add(welcome);
        return page;
    }

    private JPanel metricCard(String caption, String initialValue, String detail,
                              Color accent, int index) {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createEmptyBorder(17, 18, 17, 18)));
        JLabel heading = new JLabel(caption);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 10));
        heading.setForeground(MUTED);
        metricValues[index] = new JLabel(initialValue);
        metricValues[index].setFont(new Font("Segoe UI", Font.BOLD, 25));
        metricValues[index].setForeground(TEXT);
        JLabel note = new JLabel(detail);
        note.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        note.setForeground(MUTED);
        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labels.add(heading);
        labels.add(Box.createVerticalStrut(8));
        labels.add(metricValues[index]);
        labels.add(Box.createVerticalStrut(4));
        labels.add(note);
        card.add(labels, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFlightsPage() {
        JPanel page = pagePanel();
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(sectionHeading("Danh sách chuyến bay", "Cột ghế cho biết số ghế còn trống trên tổng số ghế."), BorderLayout.WEST);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        flightSearchField = new JTextField(12);
        JButton search = secondaryButton("Tìm");
        search.addActionListener(event -> refreshFlights(flightSearchField.getText()));
        flightSearchField.addActionListener(event -> refreshFlights(flightSearchField.getText()));
        JButton refresh = secondaryButton("Làm mới");
        refresh.addActionListener(event -> refreshAll());
        actions.add(flightSearchField);
        actions.add(search);
        actions.add(refresh);
        if (vaiTro.duocQuanLyChuyenBay()) {
            JButton add = primaryButton("+ Thêm chuyến bay");
            add.addActionListener(event -> showAddFlightDialog());
            JButton edit = secondaryButton("Sửa chuyến bay");
            edit.addActionListener(event -> editSelectedFlight());
            JButton delete = dangerButton("Xóa chuyến");
            delete.addActionListener(event -> deleteSelectedFlight());
            actions.add(add);
            actions.add(edit);
            actions.add(delete);
        }
        toolbar.add(actions, BorderLayout.EAST);
        page.add(toolbar);
        page.add(Box.createVerticalStrut(16));
        page.add(infoBanner("Thêm chuyến trước khi đặt vé. Mã chuyến phải duy nhất; ngày giờ theo dd/MM/yyyy HH:mm. Chuyến đã có vé không thể sửa hoặc xóa."));
        page.add(Box.createVerticalStrut(12));
        page.add(tableCard(flightTable));
        return page;
    }

    private JPanel buildPassengersPage() {
        JPanel page = pagePanel();
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(sectionHeading("Danh sách hành khách", "Tìm theo mã, tên hoặc số điện thoại; hồ sơ có vé chưa thể xóa."), BorderLayout.WEST);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        passengerSearchField = new JTextField(12);
        JButton search = secondaryButton("Tìm");
        search.addActionListener(event -> refreshPassengers(passengerSearchField.getText()));
        passengerSearchField.addActionListener(event -> refreshPassengers(passengerSearchField.getText()));
        actions.add(passengerSearchField);
        actions.add(search);
        if (vaiTro.duocSuaHanhKhach()) {
            JButton add = primaryButton("+ Thêm hành khách");
            add.addActionListener(event -> showPassengerDialog(null));
            JButton edit = secondaryButton("Sửa hồ sơ");
            edit.addActionListener(event -> editSelectedPassenger());
            actions.add(add);
            actions.add(edit);
        }
        if (vaiTro.duocXoaDuLieu()) {
            JButton delete = dangerButton("Xóa hồ sơ");
            delete.addActionListener(event -> deleteSelectedPassenger());
            actions.add(delete);
        }
        toolbar.add(actions, BorderLayout.EAST);
        page.add(toolbar);
        page.add(Box.createVerticalStrut(16));
        page.add(infoBanner("Mỗi hành khách cần mã riêng, họ tên, điện thoại và hộ chiếu. Tìm theo mã/tên/điện thoại; hồ sơ đã có vé được giữ để bảo toàn lịch sử."));
        page.add(Box.createVerticalStrut(12));
        page.add(tableCard(passengerTable));
        return page;
    }

    private JPanel buildTicketsPage() {
        JPanel page = pagePanel();
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(sectionHeading("Danh sách vé máy bay", "Vé được tạo cùng đơn đặt; hủy vé tại đây sẽ trả ghế và cập nhật doanh thu."), BorderLayout.WEST);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        ticketSearchField = new JTextField(12);
        JButton search = secondaryButton("Tìm");
        search.addActionListener(event -> refreshTickets(ticketSearchField.getText()));
        ticketSearchField.addActionListener(event -> refreshTickets(ticketSearchField.getText()));
        JButton refresh = secondaryButton("Làm mới");
        refresh.addActionListener(event -> refreshTickets());
        actions.add(ticketSearchField);
        actions.add(search);
        actions.add(refresh);
        if (vaiTro.duocHuyDon()) {
            JButton cancel = dangerButton("Hủy vé đã chọn");
            cancel.addActionListener(event -> cancelSelectedTicket());
            actions.add(cancel);
        }
        toolbar.add(actions, BorderLayout.EAST);
        page.add(toolbar);
        page.add(Box.createVerticalStrut(16));
        page.add(infoBanner("Vé được tạo tự động khi đặt vé. Hủy tại đây sẽ hủy đơn liên kết và trả ghế; chương trình không tự hoàn tiền."));
        page.add(Box.createVerticalStrut(12));
        page.add(tableCard(ticketTable));
        return page;
    }

    private JPanel buildEmployeesPage() {
        JPanel page = pagePanel();
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(sectionHeading("Danh sách nhân viên", "Quản lý tài khoản nhân viên lập đơn; xóa sẽ chuyển hồ sơ sang trạng thái ngừng hoạt động."), BorderLayout.WEST);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        employeeSearchField = new JTextField(12);
        JButton search = secondaryButton("Tìm");
        search.addActionListener(event -> refreshEmployees(employeeSearchField.getText()));
        employeeSearchField.addActionListener(event -> refreshEmployees(employeeSearchField.getText()));
        JButton add = primaryButton("+ Thêm nhân viên");
        add.addActionListener(event -> showEmployeeDialog(null));
        JButton edit = secondaryButton("Sửa hồ sơ");
        edit.addActionListener(event -> editSelectedEmployee());
        JButton deactivate = dangerButton("Ngừng hoạt động");
        deactivate.addActionListener(event -> deactivateSelectedEmployee());
        actions.add(employeeSearchField);
        actions.add(search);
        actions.add(add);
        actions.add(edit);
        actions.add(deactivate);
        toolbar.add(actions, BorderLayout.EAST);
        page.add(toolbar);
        page.add(Box.createVerticalStrut(16));
        page.add(tableCard(employeeTable));
        return page;
    }

    private JPanel buildAgenciesPage() {
        JPanel page = pagePanel();
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(sectionHeading("Danh sách đại lý", "Theo dõi liên hệ, cấp đại lý và dư nợ."), BorderLayout.WEST);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        agencySearchField = new JTextField(12);
        JButton search = secondaryButton("Tìm");
        search.addActionListener(event -> refreshAgencies(agencySearchField.getText()));
        agencySearchField.addActionListener(event -> refreshAgencies(agencySearchField.getText()));
        JButton add = primaryButton("+ Thêm đại lý");
        add.addActionListener(event -> showAgencyDialog(null));
        JButton edit = secondaryButton("Sửa hồ sơ");
        edit.addActionListener(event -> editSelectedAgency());
        JButton delete = dangerButton("Xóa đại lý");
        delete.addActionListener(event -> deleteSelectedAgency());
        actions.add(agencySearchField);
        actions.add(search);
        actions.add(add);
        actions.add(edit);
        actions.add(delete);
        toolbar.add(actions, BorderLayout.EAST);
        page.add(toolbar);
        page.add(Box.createVerticalStrut(16));
        page.add(infoBanner("Mã đại lý cần duy nhất. Dư nợ được nhập bằng VND; dùng Sửa hồ sơ để cập nhật thông tin hoặc công nợ."));
        page.add(Box.createVerticalStrut(12));
        page.add(tableCard(agencyTable));
        return page;
    }

    private JPanel buildBookingPage() {
        JPanel page = pagePanel();
        page.add(sectionHeading("Đặt vé", "Tạo đơn mới và thanh toán bằng tiền mặt hoặc thẻ."));
        page.add(Box.createVerticalStrut(14));
        page.add(infoBanner("Bắt đầu sau khi đã có chuyến bay. Chuẩn bị mã hành khách và chọn đúng phương thức thanh toán."));
        page.add(Box.createVerticalStrut(14));

        JPanel bookingCard = cardPanel();
        bookingCard.setLayout(new GridBagLayout());
        bookingCard.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        bookingCard.setPreferredSize(new Dimension(900, 390));
        bookingCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 410));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.CENTER;
        JLabel title = new JLabel("Tạo đơn đặt vé mới");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT);
        bookingCard.add(title, c);
        c.gridy++;
        JLabel description = new JLabel("Chọn chuyến bay đã có. Khách mới có thể đăng ký ngay trong lúc đặt vé.");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(MUTED);
        bookingCard.add(description, c);
        c.gridy++;

        JPanel steps = new JPanel(new GridLayout(1, 3, 12, 12));
        steps.setOpaque(false);
        steps.add(bookingStepCard("01", "Chọn chuyến", "Nhập mã chuyến bay và kiểm tra ghế còn trống.", new Color(55, 122, 214)));
        steps.add(bookingStepCard("02", "Thông tin khách", "Dùng mã khách đã có hoặc đăng ký hồ sơ mới.", TEAL));
        steps.add(bookingStepCard("03", "Thanh toán", "Nhập số tiền đủ trả hoặc thông tin thẻ.", new Color(222, 137, 49)));
        steps.setPreferredSize(new Dimension(850, 112));
        c.fill = GridBagConstraints.HORIZONTAL;
        bookingCard.add(steps, c);
        c.fill = GridBagConstraints.NONE;
        c.gridy++;

        JLabel priceInfo = new JLabel("Tổng tiền = (giá gốc + phụ phí) × 1,08, đã gồm VAT 8%.");
        priceInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceInfo.setForeground(MUTED);
        bookingCard.add(priceInfo, c);
        c.gridy++;
        JLabel resultInfo = new JLabel("Thanh toán thành công sẽ tạo mã đơn, mã vé và tự cập nhật số ghế.");
        resultInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultInfo.setForeground(MUTED);
        bookingCard.add(resultInfo, c);
        c.gridy++;
        JButton create = primaryButton("Bắt đầu đặt vé");
        create.addActionListener(event -> showBookingDialog());
        bookingCard.add(create, c);
        page.add(bookingCard);
        return page;
    }

    private JPanel bookingStepCard(String number, String title, String description, Color accent) {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(10, 6));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createEmptyBorder(12, 14, 12, 12)));
        JLabel numberLabel = new JLabel(number);
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        numberLabel.setForeground(accent);
        card.add(numberLabel, BorderLayout.WEST);
        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.setForeground(TEXT);
        JLabel detail = new JLabel("<html>" + description + "</html>");
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detail.setForeground(MUTED);
        detail.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        copy.add(heading);
        copy.add(detail);
        card.add(copy, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildOrdersPage() {
        JPanel page = pagePanel();
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(sectionHeading("Đơn đặt vé", "Tìm đơn, xem thông tin hoặc hủy đơn đặt vé."), BorderLayout.WEST);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        orderSearchField = new JTextField(14);
        orderSearchField.setToolTipText("Tìm theo mã đơn, mã hành khách hoặc tên hành khách");
        JButton search = secondaryButton("Tìm");
        search.addActionListener(event -> refreshOrders(orderSearchField.getText()));
        orderSearchField.addActionListener(event -> refreshOrders(orderSearchField.getText()));
        actions.add(orderSearchField);
        actions.add(search);
        if (vaiTro.duocSuaHanhKhach()) {
            JButton editPassenger = secondaryButton("Sửa hành khách");
            editPassenger.addActionListener(event -> editPassengerFromSelectedOrder());
            actions.add(editPassenger);
        }
        if (vaiTro.duocHuyDon()) {
            JButton cancel = dangerButton("Hủy đơn đã chọn");
            cancel.addActionListener(event -> cancelSelectedOrder());
            actions.add(cancel);
        }
        toolbar.add(actions, BorderLayout.EAST);
        page.add(toolbar);
        page.add(Box.createVerticalStrut(16));
        page.add(infoBanner("Tìm theo mã đơn, mã hoặc tên hành khách. Sửa hành khách cập nhật hồ sơ; hủy đơn sẽ trả ghế, lưu lịch sử và không tự hoàn tiền."));
        page.add(Box.createVerticalStrut(12));
        page.add(tableCard(orderTable));
        return page;
    }

    private JPanel buildRevenuePage() {
        JPanel page = pagePanel();
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(sectionHeading("Báo cáo doanh thu", "Tổng giá vé từ các đơn còn hiệu lực, được cộng theo từng chuyến bay."), BorderLayout.WEST);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        JButton refresh = secondaryButton("Cập nhật báo cáo");
        refresh.addActionListener(event -> refreshRevenue());
        toolbar.add(refresh, BorderLayout.EAST);
        page.add(toolbar);
        page.add(Box.createVerticalStrut(16));

        page.add(infoBanner("Doanh thu chỉ tính đơn còn hiệu lực, đã gồm VAT 8%; bảng bên dưới chia số đơn và doanh thu theo chuyến bay."));
        page.add(Box.createVerticalStrut(12));

        JPanel totalCard = cardPanel();
        totalCard.setLayout(new BoxLayout(totalCard, BoxLayout.Y_AXIS));
        totalCard.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        JLabel caption = new JLabel("TỔNG DOANH THU TỪ ĐƠN CÒN HIỆU LỰC");
        caption.setFont(new Font("Segoe UI", Font.BOLD, 10));
        caption.setForeground(MUTED);
        revenueValue.setFont(new Font("Segoe UI", Font.BOLD, 27));
        revenueValue.setForeground(TEAL);
        totalCard.add(caption);
        totalCard.add(Box.createVerticalStrut(8));
        totalCard.add(revenueValue);
        totalCard.setPreferredSize(new Dimension(900, 92));
        totalCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        page.add(totalCard);
        page.add(Box.createVerticalStrut(16));
        page.add(tableCard(revenueTable));
        return page;
    }

    private void showPage(String key, String title, String subtitle, JButton navigationButton) {
        if (!vaiTro.duocMoTrang(key)) {
            pageLayout.show(pageContainer, "home");
            pageTitle.setText("Tổng quan");
            pageSubtitle.setText("Bạn không có quyền mở mục này.");
            return;
        }
        pageLayout.show(pageContainer, key);
        pageTitle.setText(title);
        pageSubtitle.setText(subtitle);
        if (activeNavigationButton != null) {
            activeNavigationButton.setBackground(NAVY);
            activeNavigationButton.setForeground(new Color(222, 231, 240));
        }
        activeNavigationButton = navigationButton;
        if (navigationButton != null) {
            navigationButton.setBackground(NAVY_LIGHT);
            navigationButton.setForeground(Color.WHITE);
        }
        refreshAll();
    }

    private void showAddFlightDialog() {
        showFlightDialog(null);
    }

    private void showFlightDialog(ChuyenBay existing) {
        JTextField code = new JTextField(20);
        JTextField origin = new JTextField(20);
        JTextField destination = new JTextField(20);
        JTextField departure = new JTextField(20);
        JTextField arrival = new JTextField(20);
        JTextField seats = new JTextField(20);
        if (existing != null) {
            code.setText(existing.getSoHieuChuyenBay());
            code.setEnabled(false);
            origin.setText(existing.getDiemDi());
            destination.setText(existing.getDiemDen());
            departure.setText(existing.getNgayDi());
            arrival.setText(existing.getNgayDen());
            seats.setText(String.valueOf(existing.getTongSoGhe()));
        }
        JPanel form = formPanel();
        addFormRow(form, 0, "Mã chuyến bay (không trùng)", code);
        addFormRow(form, 1, "Điểm đi", origin);
        addFormRow(form, 2, "Điểm đến", destination);
        addFormRow(form, 3, "Ngày, giờ khởi hành", departure);
        addFormRow(form, 4, "Ngày, giờ đến", arrival);
        addFormRow(form, 5, "Tổng số ghế trên chuyến", seats);
        departure.setToolTipText("Ví dụ: 15/12/2026 08:30");
        arrival.setToolTipText("Ví dụ: 15/12/2026 10:45");
        JLabel dateHint = new JLabel("Nhập ngày giờ theo mẫu dd/MM/yyyy HH:mm, ví dụ 15/12/2026 08:30.");
        dateHint.setForeground(MUTED);
        dateHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        GridBagConstraints dateHintConstraints = new GridBagConstraints();
        dateHintConstraints.gridx = 0;
        dateHintConstraints.gridy = 6;
        dateHintConstraints.gridwidth = 2;
        dateHintConstraints.anchor = GridBagConstraints.WEST;
        dateHintConstraints.insets = new Insets(8, 4, 0, 4);
        form.add(dateHint, dateHintConstraints);

        int choice = JOptionPane.showConfirmDialog(this, form,
                existing == null ? "Thêm chuyến bay" : "Sửa chuyến bay",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            String ma = requiredText(code, "Mã chuyến bay");
            String diemDi = requiredText(origin, "Điểm đi");
            String diemDen = requiredText(destination, "Điểm đến");
            String ngayDi = requiredText(departure, "Ngày đi");
            String ngayDen = requiredText(arrival, "Ngày đến");
            int tongSoGhe = Integer.parseInt(requiredText(seats, "Tổng số ghế"));
            if (tongSoGhe <= 0) {
                throw new IllegalArgumentException("Tổng số ghế phải lớn hơn 0.");
            }
            ChuyenBay chuyenBay = new ChuyenBay(true, ma, diemDi, diemDen,
                    ngayDi, ngayDen, tongSoGhe, tongSoGhe);
            boolean thanhCong = existing == null
                    ? quanLyChuyenBay.themChuyenBay(chuyenBay)
                    : quanLyChuyenBay.suaChuyenBayTheoMa(existing.getSoHieuChuyenBay(), chuyenBay, quanLyVe);
            if (!thanhCong) {
                throw new IllegalArgumentException(existing == null
                        ? "Không thêm được chuyến bay. Kiểm tra mã trùng hoặc dữ liệu."
                        : "Không sửa được chuyến bay; chuyến có thể đã có vé.");
            }
            luuDuLieu();
            refreshAll();
            JOptionPane.showMessageDialog(this,
                    existing == null ? "Đã thêm chuyến bay " + ma + "." : "Đã cập nhật chuyến bay " + ma + ".",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void editSelectedFlight() {
        String code = selectedCode(flightTable, flightModel);
        ChuyenBay existing = code == null ? null : quanLyChuyenBay.timChuyenBayTheoMa(code);
        if (existing == null) {
            showError("Chọn một chuyến bay để sửa.");
            return;
        }
        showFlightDialog(existing);
    }

    private void deleteSelectedFlight() {
        String code = selectedCode(flightTable, flightModel);
        if (code == null) {
            showError("Chọn một chuyến bay để xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa chuyến bay " + code + "? Chuyến đã có vé không thể xóa.",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        if (!quanLyChuyenBay.xoaChuyenBayTheoMa(code, quanLyVe)) {
            showError("Không xóa được chuyến bay; chuyến có thể không tồn tại hoặc đã có vé.");
            return;
        }
        luuDuLieu();
        refreshAll();
    }

    private void showPassengerDialog(HanhKhach existing) {
        JTextField code = new JTextField(20);
        JTextField name = new JTextField(20);
        JTextField phone = new JTextField(20);
        JTextField passport = new JTextField(20);
        if (existing != null) {
            code.setText(existing.getMaSo());
            code.setEnabled(false);
            name.setText(existing.getHoTen());
            phone.setText(existing.getSoDienThoai());
            passport.setText(existing.getHochieu());
        }
        JPanel form = formPanel();
        addFormRow(form, 0, "Mã hành khách", code);
        addFormRow(form, 1, "Họ tên", name);
        addFormRow(form, 2, "Điện thoại", phone);
        addFormRow(form, 3, "Số hộ chiếu", passport);
        int answer = JOptionPane.showConfirmDialog(this, form,
                existing == null ? "Thêm hành khách" : "Sửa hồ sơ hành khách",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) return;
        try {
            String ma = requiredText(code, "Mã hành khách");
            String hoTen = requiredText(name, "Họ tên");
            String dienThoai = requiredText(phone, "Điện thoại");
            String hoChieu = requiredText(passport, "Số hộ chiếu");
            boolean thanhCong;
            if (existing == null) {
                thanhCong = quanLyHanhKhach.themHanhKhach(
                        new HanhKhach(true, ma, hoTen, dienThoai, hoChieu));
            } else {
                thanhCong = quanLyHanhKhach.suaThongTinHanhKhach(
                        existing.getMaSo(), hoTen, dienThoai, hoChieu);
            }
            if (!thanhCong) throw new IllegalArgumentException("Mã hành khách có thể đã tồn tại hoặc hồ sơ không hợp lệ.");
            luuDuLieu();
            refreshAll();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void editSelectedPassenger() {
        String code = selectedCode(passengerTable, passengerModel);
        HanhKhach passenger = code == null ? null : quanLyHanhKhach.timHanhKhachTheoMa(code);
        if (passenger == null) {
            showError("Chọn một hành khách để sửa.");
            return;
        }
        showPassengerDialog(passenger);
    }

    private void editPassengerFromSelectedOrder() {
        String orderCode = selectedCode(orderTable, orderModel);
        DonDatVe order = orderCode == null ? null : quanLyDonDatVe.timDonTheoMa(orderCode);
        if (order == null || order.getHanhKhach() == null || order.isDaHuy()) {
            showError("Chọn một đơn còn hiệu lực để sửa thông tin hành khách.");
            return;
        }
        showPassengerDialog(order.getHanhKhach());
    }

    private void deleteSelectedPassenger() {
        String code = selectedCode(passengerTable, passengerModel);
        if (code == null) {
            showError("Chọn một hành khách để xóa.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Xóa hồ sơ hành khách " + code + "? Hồ sơ đã có vé sẽ được giữ lại.",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        if (!quanLyHanhKhach.xoaHanhKhachTheoMa(code, quanLyVe)) {
            showError("Không xóa được hồ sơ; hành khách có thể đã có vé.");
            return;
        }
        luuDuLieu();
        refreshAll();
    }

    private void cancelSelectedTicket() {
        String ticketCode = selectedCode(ticketTable, ticketModel);
        if (ticketCode == null) {
            showError("Chọn một vé để hủy.");
            return;
        }
        VeMayBay ticket = quanLyVe.timVeTheoMa(ticketCode);
        DonDatVe order = timDonTheoMaVe(ticketCode);
        if (ticket == null || order == null) {
            showError("Không tìm thấy đơn liên kết với vé này.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Hủy vé " + ticketCode + "? Ghế sẽ được trả về chuyến bay.",
                "Xác nhận hủy vé", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        if (!quanLyDonDatVe.huyDonTheoMa(order.getMaDonDat())) {
            showError("Không hủy được vé; đơn có thể đã bị hủy trước đó.");
            return;
        }
        quanLyVe.capNhatTrangThaiHuy(ticketCode, true);
        luuDuLieu();
        refreshAll();
    }

    private DonDatVe timDonTheoMaVe(String maVe) {
        for (DonDatVe order : quanLyDonDatVe.getDanhSach()) {
            if (order.getVe() != null && maVe.equalsIgnoreCase(order.getVe().getMaVe())) return order;
        }
        return null;
    }

    private void showEmployeeDialog(NhanVien existing) {
        JTextField code = new JTextField(20);
        JTextField name = new JTextField(20);
        JTextField phone = new JTextField(20);
        JTextField password = new JTextField(20);
        if (existing != null) {
            code.setText(existing.getMaNhanVien());
            code.setEnabled(false);
            name.setText(existing.getHoTen());
            phone.setText(existing.getSoDienThoai());
            password.setText(existing.getMK());
        }
        JPanel form = formPanel();
        addFormRow(form, 0, "Mã nhân viên", code);
        addFormRow(form, 1, "Họ tên", name);
        addFormRow(form, 2, "Điện thoại", phone);
        addFormRow(form, 3, "Mật khẩu", password);
        int answer = JOptionPane.showConfirmDialog(this, form,
                existing == null ? "Thêm nhân viên" : "Sửa hồ sơ nhân viên",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) return;
        try {
            String ma = requiredText(code, "Mã nhân viên");
            String hoTen = requiredText(name, "Họ tên");
            String dienThoai = requiredText(phone, "Điện thoại");
            String matKhau = requiredText(password, "Mật khẩu");
            boolean thanhCong = existing == null
                    ? quanLyNhanVien.themNhanVien(new NhanVien(true, ma, hoTen, dienThoai, ma, matKhau))
                    : quanLyNhanVien.suaNhanVien(existing.getMaNhanVien(), hoTen, dienThoai, matKhau);
            if (!thanhCong) throw new IllegalArgumentException("Mã nhân viên có thể đã tồn tại hoặc hồ sơ không hợp lệ.");
            luuDuLieu();
            refreshAll();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void editSelectedEmployee() {
        String code = selectedCode(employeeTable, employeeModel);
        NhanVien employee = timNhanVienTrongDanhSach(code);
        if (employee == null || !employee.isStatus()) {
            showError("Chọn một nhân viên đang hoạt động để sửa.");
            return;
        }
        showEmployeeDialog(employee);
    }

    private void deactivateSelectedEmployee() {
        String code = selectedCode(employeeTable, employeeModel);
        if (code == null) {
            showError("Chọn một nhân viên để ngừng hoạt động.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Ngừng hoạt động nhân viên " + code + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        if (!quanLyNhanVien.xoaNhanVien(code)) {
            showError("Không tìm thấy nhân viên đang hoạt động có mã này.");
            return;
        }
        luuDuLieu();
        refreshAll();
    }

    private NhanVien timNhanVienTrongDanhSach(String ma) {
        if (ma == null) return null;
        for (NhanVien employee : quanLyNhanVien.getDanhSach()) {
            if (employee.getMaNhanVien() != null && employee.getMaNhanVien().equalsIgnoreCase(ma)) return employee;
        }
        return null;
    }

    private void showAgencyDialog(DaiLy existing) {
        JTextField code = new JTextField(20);
        JTextField name = new JTextField(20);
        JTextField phone = new JTextField(20);
        JTextField type = new JTextField(20);
        JTextField debt = new JTextField(20);
        if (existing != null) {
            code.setText(existing.getMaSo());
            code.setEnabled(false);
            name.setText(existing.getHoTen());
            phone.setText(existing.getSoDienThoai());
            type.setText(existing.getLoaiDaiLy());
            debt.setText(String.valueOf(existing.getDuNo()));
        } else {
            debt.setText("0");
        }
        JPanel form = formPanel();
        addFormRow(form, 0, "Mã đại lý", code);
        addFormRow(form, 1, "Tên đại lý", name);
        addFormRow(form, 2, "Điện thoại", phone);
        addFormRow(form, 3, "Loại đại lý", type);
        addFormRow(form, 4, "Dư nợ ban đầu (VND)", debt);
        int answer = JOptionPane.showConfirmDialog(this, form,
                existing == null ? "Thêm đại lý" : "Sửa hồ sơ đại lý",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) return;
        try {
            String ma = requiredText(code, "Mã đại lý");
            String ten = requiredText(name, "Tên đại lý");
            String dienThoai = requiredText(phone, "Điện thoại");
            String loai = requiredText(type, "Loại đại lý");
            double duNo = parseMoney(debt, "Dư nợ");
            if (existing == null) {
                if (!quanLyDaiLy.themDaiLy(new DaiLy(true, ma, ten, dienThoai, loai, duNo)))
                    throw new IllegalArgumentException("Mã đại lý có thể đã tồn tại hoặc dữ liệu không hợp lệ.");
            } else {
                existing.setHoTen(ten);
                existing.setSoDienThoai(dienThoai);
                existing.setLoaiDaiLy(loai);
                existing.setDuNo(duNo);
            }
            luuDuLieu();
            refreshAll();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void editSelectedAgency() {
        String code = selectedCode(agencyTable, agencyModel);
        DaiLy agency = code == null ? null : quanLyDaiLy.timDaiLyTheoMa(code);
        if (agency == null) {
            showError("Chọn một đại lý để sửa.");
            return;
        }
        showAgencyDialog(agency);
    }

    private void deleteSelectedAgency() {
        String code = selectedCode(agencyTable, agencyModel);
        if (code == null) {
            showError("Chọn một đại lý để xóa.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Xóa đại lý " + code + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        if (!quanLyDaiLy.xoaDaiLyTheoMa(code)) {
            showError("Không xóa được đại lý.");
            return;
        }
        luuDuLieu();
        refreshAll();
    }

    private String selectedCode(JTable table, GridTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        row = table.convertRowIndexToModel(row);
        return String.valueOf(model.getValueAt(row, 0));
    }

    private void showBookingDialog() {
        JTextField flightCode = new JTextField(20);
        JTextField passengerCode = new JTextField(20);
        JTextField passengerName = new JTextField(20);
        JTextField phone = new JTextField(20);
        JTextField passport = new JTextField(20);
        JComboBox<String> ticketType = new JComboBox<>(new String[]{"Phổ thông", "Thương gia"});
        JTextField basePrice = new JTextField(20);
        JTextField surcharge = new JTextField(20);
        JComboBox<String> paymentType = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ"});
        JTextField paymentInfo = new JTextField(20);
        JTextField employeeCode = new JTextField(20);
        paymentType.addActionListener(event -> {
            paymentInfo.setText("");
            paymentInfo.setToolTipText(paymentType.getSelectedIndex() == 0
                    ? "Nhập số tiền khách đưa" : "Nhập mã/số thẻ");
        });
        paymentInfo.setToolTipText("Nhập số tiền khách đưa");

        JPanel form = formPanel();
        addFormRow(form, 0, "Mã chuyến bay (đã tạo trước)", flightCode);
        addFormRow(form, 1, "Mã hành khách", passengerCode);
        addFormRow(form, 2, "Họ tên (nếu là khách mới)", passengerName);
        addFormRow(form, 3, "Điện thoại (khách mới)", phone);
        addFormRow(form, 4, "Hộ chiếu (khách mới)", passport);
        addFormRow(form, 5, "Loại vé", ticketType);
        addFormRow(form, 6, "Giá gốc (VND)", basePrice);
        addFormRow(form, 7, "Phụ phí (VND)", surcharge);
        addFormRow(form, 8, "Thanh toán", paymentType);
        addFormRow(form, 9, "Số tiền khách đưa / mã thẻ", paymentInfo);
        int hintRow = 10;
        if (vaiTro != VaiTro.CLIENT) {
            addFormRow(form, 10, "Mã nhân viên lập (tùy chọn)", employeeCode);
            hintRow = 11;
        }
        JLabel hint = new JLabel("<html>Nếu hành khách chưa có hồ sơ, hãy nhập họ tên, điện thoại và hộ chiếu.<br>"
                + "Vé phổ thông dùng phụ phí hành lý; vé thương gia dùng phụ phí phòng chờ. VAT 8% được cộng vào tổng tiền.</html>");
        hint.setForeground(MUTED);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        GridBagConstraints hintConstraints = new GridBagConstraints();
        hintConstraints.gridx = 0;
        hintConstraints.gridy = hintRow;
        hintConstraints.gridwidth = 2;
        hintConstraints.anchor = GridBagConstraints.WEST;
        hintConstraints.insets = new Insets(10, 4, 0, 4);
        form.add(hint, hintConstraints);

        int choice = JOptionPane.showConfirmDialog(this, new JScrollPane(form), "Tạo đơn đặt vé",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String maChuyenBay = requiredText(flightCode, "Mã chuyến bay");
            ChuyenBay chuyenBay = quanLyChuyenBay.timChuyenBayTheoMa(maChuyenBay);
            if (chuyenBay == null) {
                throw new IllegalArgumentException("Không tìm thấy chuyến bay " + maChuyenBay + ".");
            }

            String maHanhKhach = requiredText(passengerCode, "Mã hành khách");
            HanhKhach hanhKhach = quanLyHanhKhach.timHanhKhachTheoMa(maHanhKhach);
            if (hanhKhach == null) {
                String hoTen = requiredText(passengerName, "Họ tên hành khách mới");
                String soDienThoai = requiredText(phone, "Số điện thoại");
                String soHoChieu = requiredText(passport, "Số hộ chiếu");
                hanhKhach = new HanhKhach(true, maHanhKhach, hoTen, soDienThoai, soHoChieu);
                if (!quanLyHanhKhach.themHanhKhach(hanhKhach)) {
                    throw new IllegalArgumentException("Không đăng ký được hành khách; mã có thể đã tồn tại.");
                }
            }

            double gia = parseMoney(basePrice, "Giá gốc");
            double phi = parseMoney(surcharge, "Phụ phí");
            IThanhToan thanhToan;
            String tenPhuongThuc;
            if (paymentType.getSelectedIndex() == 0) {
                thanhToan = new ThanhToanTienMat(parseMoney(paymentInfo, "Số tiền khách đưa"));
                tenPhuongThuc = "Tiền mặt";
            } else {
                String soThe = requiredText(paymentInfo, "Mã/số thẻ");
                thanhToan = new ThanhToanThe(soThe);
                tenPhuongThuc = "Thẻ";
            }

            String maNhanVien = employeeCode.getText() == null ? "" : employeeCode.getText().trim();
            NhanVien nhanVienLap = null;
            if (!maNhanVien.isEmpty()) {
                nhanVienLap = quanLyNhanVien.timNhanVien(maNhanVien);
                if (nhanVienLap == null) {
                    throw new IllegalArgumentException("Không tìm thấy nhân viên đang hoạt động " + maNhanVien + ".");
                }
            }

            DonDatVe don = quanLyDonDatVe.datVeTuDong(hanhKhach, chuyenBay,
                    ticketType.getSelectedIndex() + 1, gia, phi, thanhToan, tenPhuongThuc);
            if (don == null) {
                throw new IllegalArgumentException(quanLyDonDatVe.getThongBaoGanNhat());
            }
            don.setNhanVienLap(nhanVienLap);
            if (!quanLyVe.themVe(don.getVe())) {
                quanLyDonDatVe.xoaDon(don.getMaDonDat());
                throw new IllegalArgumentException("Không thể thêm vé vào danh sách quản lý; đơn đã được hoàn tác.");
            }
            luuDuLieu();
            refreshAll();
            JOptionPane.showMessageDialog(this,
                    "Đặt vé thành công.\nMã đơn: " + don.getMaDonDat()
                            + "\nMã vé: " + don.getVe().getMaVe()
                            + "\nTổng tiền: " + formatMoney(don.getSoTienThanhToan()),
                    "Đặt vé thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void cancelSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            showError("Chọn một đơn trong bảng trước.");
            return;
        }
        row = orderTable.convertRowIndexToModel(row);
        String maDon = String.valueOf(orderModel.getValueAt(row, 0));
        int answer = JOptionPane.showConfirmDialog(this,
                "Hủy đơn " + maDon + " và trả ghế về chuyến bay?\n"
                        + "Hệ thống hiện chưa có chức năng hoàn tiền tự động.",
                "Xác nhận hủy đơn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        if (quanLyDonDatVe.huyDonTheoMa(maDon)) {
            DonDatVe order = quanLyDonDatVe.timDonTheoMa(maDon);
            if (order != null && order.getVe() != null) {
                quanLyVe.capNhatTrangThaiHuy(order.getVe().getMaVe(), true);
            }
            luuDuLieu();
            refreshAll();
            JOptionPane.showMessageDialog(this, "Đã hủy đơn " + maDon + ".",
                    "Đã cập nhật", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Không thể hủy đơn. Đơn có thể không tồn tại hoặc đã bị hủy.");
        }
    }

    private void refreshAll() {
        refreshFlights(flightSearchField == null ? "" : flightSearchField.getText());
        refreshOrders(orderSearchField == null ? "" : orderSearchField.getText());
        refreshPassengers(passengerSearchField == null ? "" : passengerSearchField.getText());
        refreshTickets();
        refreshEmployees(employeeSearchField == null ? "" : employeeSearchField.getText());
        refreshAgencies(agencySearchField == null ? "" : agencySearchField.getText());
        refreshRevenue();
        refreshDashboard();
    }

    private void refreshFlights() {
        refreshFlights(flightSearchField == null ? "" : flightSearchField.getText());
    }

    private void refreshFlights(String keyword) {
        String search = keyword == null ? "" : keyword.trim().toLowerCase();
        ChuyenBay[] flights = quanLyChuyenBay.getDanhSach();
        int count = 0;
        for (ChuyenBay flight : flights) {
            if (matchesFlight(flight, search)) count++;
        }
        Object[][] rows = new Object[count][7];
        int index = 0;
        for (ChuyenBay flight : flights) {
            if (!matchesFlight(flight, search)) continue;
            rows[index][0] = flight.getSoHieuChuyenBay();
            rows[index][1] = flight.getDiemDi();
            rows[index][2] = flight.getDiemDen();
            rows[index][3] = flight.getNgayDi();
            rows[index][4] = flight.getNgayDen();
            rows[index][5] = flight.getSoGheTrong() + " / " + flight.getTongSoGhe();
            rows[index][6] = flight.isStatus() ? "Còn ghế" : "Hết ghế";
            index++;
        }
        flightModel.setRows(rows);
    }

    private boolean matchesFlight(ChuyenBay flight, String search) {
        return search.isEmpty() || (String.valueOf(flight.getSoHieuChuyenBay()) + " "
                + String.valueOf(flight.getDiemDi()) + " " + String.valueOf(flight.getDiemDen()))
                .toLowerCase().contains(search);
    }

    private void refreshPassengers(String keyword) {
        String search = keyword == null ? "" : keyword.trim().toLowerCase();
        HanhKhach[] passengers = quanLyHanhKhach.getDanhSach();
        int count = 0;
        for (HanhKhach passenger : passengers) {
            if ((passenger.getMaSo() + " " + passenger.getHoTen() + " " + passenger.getSoDienThoai())
                    .toLowerCase().contains(search)) count++;
        }
        Object[][] rows = new Object[count][5];
        int index = 0;
        for (HanhKhach passenger : passengers) {
            if (!(passenger.getMaSo() + " " + passenger.getHoTen() + " " + passenger.getSoDienThoai())
                    .toLowerCase().contains(search)) continue;
            rows[index][0] = passenger.getMaSo();
            rows[index][1] = passenger.getHoTen();
            rows[index][2] = passenger.getSoDienThoai();
            rows[index][3] = passenger.getHochieu();
            rows[index][4] = passenger.isStatus() ? "Đang hoạt động" : "Ngừng hoạt động";
            index++;
        }
        passengerModel.setRows(rows);
    }

    private void refreshTickets() {
        refreshTickets(ticketSearchField == null ? "" : ticketSearchField.getText());
    }

    private void refreshTickets(String keyword) {
        String search = keyword == null ? "" : keyword.trim().toLowerCase();
        VeMayBay[] all = quanLyVe.getDanhSach();
        int count = 0;
        for (VeMayBay ticket : all) {
            if (matchesTicket(ticket, search)) count++;
        }
        Object[][] rows = new Object[count][6];
        int index = 0;
        for (VeMayBay ticket : all) {
            if (!matchesTicket(ticket, search)) continue;
            HanhKhach passenger = ticket.getHanhKhach();
            ChuyenBay flight = ticket.getChuyenBay();
            rows[index][0] = ticket.getMaVe();
            rows[index][1] = ticket instanceof VeThuongGia ? "Thương gia" : "Phổ thông";
            rows[index][2] = passenger == null ? "" : passenger.getMaSo() + " - " + passenger.getHoTen();
            rows[index][3] = flight == null ? "" : flight.getSoHieuChuyenBay();
            rows[index][4] = formatMoney(ticket.tinhGiaVe());
            rows[index][5] = quanLyVe.isDaHuy(ticket.getMaVe()) ? "Đã hủy" : "Còn hiệu lực";
            index++;
        }
        ticketModel.setRows(rows);
    }

    private boolean matchesTicket(VeMayBay ticket, String search) {
        if (search.isEmpty()) return true;
        HanhKhach passenger = ticket.getHanhKhach();
        ChuyenBay flight = ticket.getChuyenBay();
        String passengerFields = passenger == null ? "" : passenger.getMaSo() + " " + passenger.getHoTen();
        String flightCode = flight == null ? "" : flight.getSoHieuChuyenBay();
        return (ticket.getMaVe() + " " + passengerFields + " " + flightCode).toLowerCase().contains(search);
    }

    private void refreshEmployees(String keyword) {
        String search = keyword == null ? "" : keyword.trim().toLowerCase();
        NhanVien[] employees = quanLyNhanVien.getDanhSach();
        int count = 0;
        for (NhanVien employee : employees) {
            if ((employee.getMaNhanVien() + " " + employee.getHoTen() + " " + employee.getSoDienThoai())
                    .toLowerCase().contains(search)) count++;
        }
        Object[][] rows = new Object[count][4];
        int index = 0;
        for (NhanVien employee : employees) {
            if (!(employee.getMaNhanVien() + " " + employee.getHoTen() + " " + employee.getSoDienThoai())
                    .toLowerCase().contains(search)) continue;
            rows[index][0] = employee.getMaNhanVien();
            rows[index][1] = employee.getHoTen();
            rows[index][2] = employee.getSoDienThoai();
            rows[index][3] = employee.isStatus() ? "Đang hoạt động" : "Ngừng hoạt động";
            index++;
        }
        employeeModel.setRows(rows);
    }

    private void refreshAgencies(String keyword) {
        String search = keyword == null ? "" : keyword.trim().toLowerCase();
        DaiLy[] agencies = quanLyDaiLy.getDanhSach();
        int count = 0;
        for (DaiLy agency : agencies) {
            if ((agency.getMaSo() + " " + agency.getHoTen() + " " + agency.getSoDienThoai())
                    .toLowerCase().contains(search)) count++;
        }
        Object[][] rows = new Object[count][5];
        int index = 0;
        for (DaiLy agency : agencies) {
            if (!(agency.getMaSo() + " " + agency.getHoTen() + " " + agency.getSoDienThoai())
                    .toLowerCase().contains(search)) continue;
            rows[index][0] = agency.getMaSo();
            rows[index][1] = agency.getHoTen();
            rows[index][2] = agency.getSoDienThoai();
            rows[index][3] = agency.getLoaiDaiLy();
            rows[index][4] = formatMoney(agency.getDuNo() == null ? 0 : agency.getDuNo());
            index++;
        }
        agencyModel.setRows(rows);
    }

    private void refreshOrders(String keyword) {
        DonDatVe[] all = quanLyDonDatVe.getDanhSach();
        int matchCount = 0;
        String search = keyword == null ? "" : keyword.trim().toLowerCase();
        for (DonDatVe order : all) {
            if (matchesOrder(order, search)) {
                matchCount++;
            }
        }
        Object[][] rows = new Object[matchCount][7];
        int index = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (DonDatVe order : all) {
            if (!matchesOrder(order, search)) {
                continue;
            }
            HanhKhach passenger = order.getHanhKhach();
            ChuyenBay flight = order.getChuyenBay();
            VeMayBay ticket = order.getVe();
            rows[index][0] = order.getMaDonDat();
            rows[index][1] = passenger == null ? "" : passenger.getMaSo() + " - " + passenger.getHoTen();
            rows[index][2] = flight == null ? "" : flight.getSoHieuCHuyenBay();
            rows[index][3] = ticket == null ? "" : ticket.getMaVe();
            rows[index][4] = formatMoney(order.getSoTienThanhToan());
            rows[index][5] = order.getThoiGianDat() == null
                    ? "" : order.getThoiGianDat().format(formatter);
            rows[index][6] = order.isDaHuy() ? "Đã hủy" : "Còn hiệu lực";
            index++;
        }
        orderModel.setRows(rows);
    }

    private boolean matchesOrder(DonDatVe order, String search) {
        if (search.isEmpty()) {
            return true;
        }
        String code = order.getMaDonDat() == null ? "" : order.getMaDonDat().toLowerCase();
        HanhKhach passenger = order.getHanhKhach();
        String passengerCode = passenger == null || passenger.getMaSo() == null
                ? "" : passenger.getMaSo().toLowerCase();
        String passengerName = passenger == null || passenger.getHoTen() == null
                ? "" : passenger.getHoTen().toLowerCase();
        return code.contains(search) || passengerCode.contains(search) || passengerName.contains(search);
    }

    private void refreshRevenue() {
        DonDatVe[] orders = quanLyDonDatVe.getDanhSach();
        String[] flightCodes = new String[orders.length];
        int[] orderCounts = new int[orders.length];
        double[] revenue = new double[orders.length];
        int flightCount = 0;
        for (DonDatVe order : orders) {
            if (order.isDaHuy() || order.getChuyenBay() == null) {
                continue;
            }
            String code = order.getChuyenBay().getSoHieuCHuyenBay();
            if (code == null) {
                code = "(không có mã)";
            }
            int position = 0;
            while (position < flightCount && !flightCodes[position].equalsIgnoreCase(code)) {
                position++;
            }
            if (position == flightCount) {
                flightCodes[flightCount] = code;
                flightCount++;
            }
            orderCounts[position]++;
            revenue[position] += order.getSoTienThanhToan();
        }
        Object[][] rows = new Object[flightCount][3];
        for (int i = 0; i < flightCount; i++) {
            rows[i][0] = flightCodes[i];
            rows[i][1] = orderCounts[i];
            rows[i][2] = formatMoney(revenue[i]);
        }
        revenueModel.setRows(rows);
        revenueValue.setText(formatMoney(quanLyDonDatVe.tinhDoanhThu()));
    }

    private void refreshDashboard() {
        if (metricValues[0] == null) {
            return;
        }
        metricValues[0].setText(String.valueOf(quanLyChuyenBay.getSoLuong()));
        int activeOrders = 0;
        for (DonDatVe order : quanLyDonDatVe.getDanhSach()) {
            if (!order.isDaHuy()) {
                activeOrders++;
            }
        }
        metricValues[1].setText(String.valueOf(activeOrders));
        metricValues[2].setText(formatMoney(quanLyDonDatVe.tinhDoanhThu()));
    }

    private static JPanel infoBanner(String text) {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(233, 243, 250));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 231, 241)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel message = new JLabel(text);
        message.setForeground(new Color(55, 83, 105));
        message.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        banner.add(message, BorderLayout.CENTER);
        banner.setPreferredSize(new Dimension(900, 42));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return banner;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Không thể thực hiện", JOptionPane.ERROR_MESSAGE);
    }

    private boolean luuDuLieu() {
        try {
            XuLyFile.luuTatCa(quanLyChuyenBay, quanLyHanhKhach,
                    quanLyNhanVien, quanLyDaiLy, quanLyVe, quanLyDonDatVe);
            return true;
        } catch (IOException | RuntimeException e) {
            showError("Không lưu được dữ liệu. Kiểm tra quyền ghi thư mục data.\n" + e.getMessage());
            return false;
        }
    }

    private static JPanel pagePanel() {
        JPanel page = new JPanel();
        page.setBackground(BACKGROUND);
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        return page;
    }

    private static JPanel sectionHeading(String title, String description) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 17));
        heading.setForeground(TEXT);
        JLabel detail = new JLabel(description);
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detail.setForeground(MUTED);
        detail.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        panel.add(heading);
        panel.add(detail);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        return panel;
    }

    private static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(229, 234, 240)));
        return panel;
    }

    private static JPanel tableCard(JTable table) {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(700, 420));
        return card;
    }

    private static JTable createTable(GridTableModel model) {
        return createTable(model, null, null);
    }

    private static JTable createTable(GridTableModel model, String emptyTitle, String emptyDescription) {
        JTable table = new JTable(model) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (model.getRowCount() != 0 || emptyTitle == null) return;

                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2 - 22;
                g.setColor(new Color(232, 244, 242));
                g.fillOval(centerX - 30, centerY - 30, 60, 60);
                g.setColor(TEAL);
                g.setStroke(new java.awt.BasicStroke(2f));
                g.drawRoundRect(centerX - 12, centerY - 13, 24, 27, 5, 5);
                g.drawLine(centerX - 7, centerY - 5, centerX + 7, centerY - 5);
                g.drawLine(centerX - 7, centerY + 1, centerX + 7, centerY + 1);
                g.drawLine(centerX - 7, centerY + 7, centerX + 4, centerY + 7);

                g.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g.setColor(TEXT);
                java.awt.FontMetrics titleMetrics = g.getFontMetrics();
                g.drawString(emptyTitle, centerX - titleMetrics.stringWidth(emptyTitle) / 2, centerY + 55);

                g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g.setColor(MUTED);
                java.awt.FontMetrics descriptionMetrics = g.getFontMetrics();
                g.drawString(emptyDescription,
                        centerX - descriptionMetrics.stringWidth(emptyDescription) / 2, centerY + 80);
                g.dispose();
            }
        };
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(TEXT);
        table.setGridColor(new Color(235, 239, 243));
        table.setSelectionBackground(new Color(221, 241, 237));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setForeground(new Color(76, 91, 108));
        header.setBackground(new Color(247, 249, 251));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        return table;
    }

    private static JButton primaryButton(String text) {
        JButton button = styledButton(text, TEAL, Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        return button;
    }

    private static JButton secondaryButton(String text) {
        JButton button = styledButton(text, Color.WHITE, TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 224, 232)),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        return button;
    }

    private static JButton dangerButton(String text) {
        JButton button = styledButton(text, new Color(181, 67, 67), Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        return button;
    }

    private static JButton styledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private static JPanel formPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return form;
    }

    private static void addFormRow(JPanel form, int row, String label, JComponent input) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 4, 5, 12);
        JLabel caption = new JLabel(label);
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        caption.setForeground(TEXT);
        form.add(caption, labelConstraints);

        GridBagConstraints inputConstraints = new GridBagConstraints();
        inputConstraints.gridx = 1;
        inputConstraints.gridy = row;
        inputConstraints.weightx = 1;
        inputConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputConstraints.insets = new Insets(5, 4, 5, 4);
        if (input instanceof JTextField) {
            ((JTextField) input).setFont(new Font("Segoe UI", Font.PLAIN, 12));
        } else if (input instanceof AbstractButton) {
            input.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        form.add(input, inputConstraints);
    }

    private static String requiredText(JTextField field, String label) {
        String value = field.getText() == null ? "" : field.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(label + " không được để trống.");
        }
        return value;
    }

    private static double parseMoney(JTextField field, String label) {
        String value = requiredText(field, label).replace(',', '.');
        try {
            double amount = Double.parseDouble(value);
            if (!Double.isFinite(amount) || amount < 0) {
                throw new NumberFormatException();
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " phải là số không âm hợp lệ.");
        }
    }

    private static String formatMoney(double amount) {
        return String.format("%,.0f VND", amount);
    }

    /** Bảng đọc dữ liệu dựa trên mảng hai chiều, không dùng collection. */
    private static final class GridTableModel extends AbstractTableModel {
        private final String[] columns;
        private Object[][] rows;

        private GridTableModel(String[] columns) {
            this.columns = columns;
            this.rows = new Object[0][columns.length];
        }

        private void setRows(Object[][] rows) {
            this.rows = rows;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rows.length;
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rows[rowIndex][columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
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

    private final JTable flightTable = createTable(flightModel);
    private final JTable orderTable = createTable(orderModel);
    private final JTable revenueTable = createTable(revenueModel);
    private JTextField orderSearchField;
    private JButton activeNavigationButton;

    public GiaoDienChinh() {
        this(new QuanLyChuyenBay(), new QuanLyHanhKhach(), new QuanLyDonDatVe());
    }

    public GiaoDienChinh(QuanLyChuyenBay quanLyChuyenBay,
                         QuanLyHanhKhach quanLyHanhKhach,
                         QuanLyDonDatVe quanLyDonDatVe) {
        this.quanLyChuyenBay = quanLyChuyenBay;
        this.quanLyHanhKhach = quanLyHanhKhach;
        this.quanLyDonDatVe = quanLyDonDatVe;

        setTitle("SkyDesk | Quản lý vé máy bay");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1020, 660));
        setSize(1240, 800);
        setLocationRelativeTo(null);
        setContentPane(buildApplication());
        refreshAll();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Dùng giao diện Swing mặc định nếu Look and Feel của hệ thống không khả dụng.
            }
            new GiaoDienChinh().hienThiMenu();
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
        brand.add(logo);
        brand.add(brandCaption);
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel navigation = new JPanel();
        navigation.setOpaque(false);
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
        navigation.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        navigation.add(navButton("Tổng quan", "home", "Tổng quan", "Số chuyến bay, đơn còn hiệu lực và doanh thu hiện tại"));
        navigation.add(Box.createVerticalStrut(8));
        navigation.add(navButton("Chuyến bay", "flights", "Chuyến bay", "Thêm chuyến bay và kiểm tra số ghế còn trống"));
        navigation.add(Box.createVerticalStrut(8));
        navigation.add(navButton("Đặt vé", "booking", "Đặt vé", "Tạo đơn sau khi chuyến bay đã được thêm"));
        navigation.add(Box.createVerticalStrut(8));
        navigation.add(navButton("Đơn đặt vé", "orders", "Đơn đặt vé", "Tìm đơn theo mã đơn hoặc thông tin hành khách"));
        navigation.add(Box.createVerticalStrut(8));
        navigation.add(navButton("Doanh thu", "revenue", "Báo cáo doanh thu", "Tổng giá vé của các đơn còn hiệu lực"));
        sidebar.add(navigation, BorderLayout.CENTER);

        JLabel version = new JLabel("ĐỒ ÁN OOP • JAVA SWING");
        version.setForeground(new Color(145, 164, 185));
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sidebar.add(version, BorderLayout.SOUTH);
        return sidebar;
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

        JLabel badge = new JLabel("  DỮ LIỆU TRONG PHIÊN  ");
        badge.setOpaque(true);
        badge.setBackground(new Color(230, 246, 242));
        badge.setForeground(TEAL);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        header.add(badge, BorderLayout.EAST);
        return header;
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
        JLabel welcomeText = new JLabel("<html>1. Vào <b>Chuyến bay</b> để thêm mã chuyến, tuyến bay, lịch khởi hành và số ghế.<br>"
                + "2. Vào <b>Đặt vé</b>, nhập mã chuyến bay và thông tin hành khách để tạo đơn.<br>"
                + "3. Xem <b>Doanh thu</b> để biết tổng tiền từ các đơn còn hiệu lực.<br>"
                + "Dữ liệu hiện chỉ được giữ trong lúc chương trình đang mở.</html>");
        welcomeText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeText.setForeground(MUTED);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(welcomeTitle);
        text.add(Box.createVerticalStrut(8));
        text.add(welcomeText);
        welcome.add(text, BorderLayout.CENTER);
        JButton addFlight = primaryButton("Thêm chuyến bay");
        addFlight.setPreferredSize(new Dimension(170, 42));
        addFlight.addActionListener(event -> showAddFlightDialog());
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
        JButton refresh = secondaryButton("Làm mới");
        refresh.addActionListener(event -> refreshAll());
        JButton add = primaryButton("+ Thêm chuyến bay");
        add.addActionListener(event -> showAddFlightDialog());
        actions.add(refresh);
        actions.add(add);
        toolbar.add(actions, BorderLayout.EAST);
        page.add(toolbar);
        page.add(Box.createVerticalStrut(16));
        page.add(infoBanner("Ngày giờ: dd/MM/yyyy HH:mm. Mỗi mã chuyến bay cần duy nhất."));
        page.add(Box.createVerticalStrut(12));
        page.add(tableCard(flightTable));
        return page;
    }

    private JPanel buildBookingPage() {
        JPanel page = pagePanel();
        page.add(sectionHeading("Đặt vé", "Tạo đơn mới và thanh toán bằng tiền mặt hoặc thẻ."));
        JPanel bookingCard = cardPanel();
        bookingCard.setLayout(new GridBagLayout());
        bookingCard.setBorder(BorderFactory.createEmptyBorder(34, 36, 34, 36));
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
        JLabel priceInfo = new JLabel("Tổng tiền = (giá gốc + phụ phí) × 1,08, đã gồm VAT 8%.");
        priceInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceInfo.setForeground(MUTED);
        bookingCard.add(priceInfo, c);
        c.gridy++;
        JButton create = primaryButton("Bắt đầu đặt vé");
        create.addActionListener(event -> showBookingDialog());
        bookingCard.add(create, c);
        page.add(Box.createVerticalStrut(14));
        page.add(bookingCard);
        return page;
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
        JButton cancel = dangerButton("Hủy đơn đã chọn");
        cancel.addActionListener(event -> cancelSelectedOrder());
        actions.add(orderSearchField);
        actions.add(search);
        actions.add(cancel);
        toolbar.add(actions, BorderLayout.EAST);
        page.add(toolbar);
        page.add(Box.createVerticalStrut(16));
        page.add(infoBanner("Chọn một đơn rồi bấm “Hủy đơn” để trả ghế về chuyến bay."));
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
        JTextField code = new JTextField(20);
        JTextField origin = new JTextField(20);
        JTextField destination = new JTextField(20);
        JTextField departure = new JTextField(20);
        JTextField arrival = new JTextField(20);
        JTextField seats = new JTextField(20);
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

        int choice = JOptionPane.showConfirmDialog(this, form, "Thêm chuyến bay",
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
            if (!quanLyChuyenBay.themChuyenBay(chuyenBay)) {
                throw new IllegalArgumentException("Không thêm được chuyến bay. Kiểm tra mã trùng hoặc dữ liệu.");
            }
            refreshAll();
            JOptionPane.showMessageDialog(this, "Đã thêm chuyến bay " + ma + ".",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
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
        JLabel hint = new JLabel("<html>Nếu hành khách chưa có hồ sơ, hãy nhập họ tên, điện thoại và hộ chiếu.<br>"
                + "Vé phổ thông dùng phụ phí hành lý; vé thương gia dùng phụ phí phòng chờ. VAT 8% được cộng vào tổng tiền.</html>");
        hint.setForeground(MUTED);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        GridBagConstraints hintConstraints = new GridBagConstraints();
        hintConstraints.gridx = 0;
        hintConstraints.gridy = 10;
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

            DonDatVe don = quanLyDonDatVe.datVeTuDong(hanhKhach, chuyenBay,
                    ticketType.getSelectedIndex() + 1, gia, phi, thanhToan, tenPhuongThuc);
            if (don == null) {
                throw new IllegalArgumentException(quanLyDonDatVe.getThongBaoGanNhat());
            }
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
            refreshAll();
            JOptionPane.showMessageDialog(this, "Đã hủy đơn " + maDon + ".",
                    "Đã cập nhật", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Không thể hủy đơn. Đơn có thể không tồn tại hoặc đã bị hủy.");
        }
    }

    private void refreshAll() {
        refreshFlights();
        refreshOrders(orderSearchField == null ? "" : orderSearchField.getText());
        refreshRevenue();
        refreshDashboard();
    }

    private void refreshFlights() {
        int count = quanLyChuyenBay.getSoLuong();
        Object[][] rows = new Object[count][7];
        for (int i = 0; i < count; i++) {
            ChuyenBay flight = quanLyChuyenBay.layChuyenBayTaiViTri(i);
            rows[i][0] = flight.getSoHieuCHuyenBay();
            rows[i][1] = flight.getDiemDi();
            rows[i][2] = flight.getDiemDen();
            rows[i][3] = flight.getNgayDi();
            rows[i][4] = flight.getNgayDen();
            rows[i][5] = flight.getSoGheTrong() + " / " + flight.getTongSoGhe();
            rows[i][6] = flight.isStatus() ? "Còn ghế" : "Hết ghế";
        }
        flightModel.setRows(rows);
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
        JTable table = new JTable(model);
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/** Màn hình xác thực trước khi mở hệ thống quản lý vé. */
public final class DangNhap extends JFrame {
    private static final TaiKhoan[] TAI_KHOAN_MAU = {
        new TaiKhoan("client", "123456", VaiTro.CLIENT),
        new TaiKhoan("staff", "123456", VaiTro.STAFF),
        new TaiKhoan("manager", "123456", VaiTro.MANAGER),
        new TaiKhoan("admin", "123456", VaiTro.ADMIN)
    };
    private static final Color NAVY = new Color(24, 40, 63);
    private static final Color TEAL = new Color(24, 142, 125);
    private static final Color TEXT = new Color(39, 52, 69);
    private static final Color MUTED = new Color(112, 126, 143);

    private final JTextField truongTenDangNhap = new JTextField(20);
    private final JPasswordField truongMatKhau = new JPasswordField(20);
    private JButton nutDangNhap;

    public DangNhap() {
        setTitle("SkyDesk | Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(470, 420);
        setResizable(false);
        setLocationRelativeTo(null);
        setContentPane(taoNoiDung());
        getRootPane().setDefaultButton(nutDangNhap);
    }

    private JPanel taoNoiDung() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(244, 247, 250));
        root.setBorder(BorderFactory.createEmptyBorder(22, 28, 24, 28));

        JPanel brand = new JPanel(new BorderLayout());
        brand.setBackground(NAVY);
        brand.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        JLabel logo = new JLabel("SKYDESK");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel caption = new JLabel("QUẢN LÝ VÉ MÁY BAY");
        caption.setForeground(new Color(190, 207, 224));
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        brand.add(logo, BorderLayout.NORTH);
        brand.add(caption, BorderLayout.SOUTH);
        root.add(brand, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 234, 240)),
                BorderFactory.createEmptyBorder(20, 22, 18, 22)));

        JLabel heading = new JLabel("Đăng nhập hệ thống");
        heading.setForeground(TEXT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 14, 0);
        form.add(heading, c);

        themDong(form, 1, "Tên đăng nhập", truongTenDangNhap);
        themDong(form, 2, "Mật khẩu", truongMatKhau);

        JLabel hint = new JLabel("<html>Tài khoản mẫu: client, staff, manager, admin.<br>Mật khẩu của cả 4 tài khoản: 123456.</html>");
        hint.setForeground(MUTED);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, 0, 12, 0);
        form.add(hint, c);

        JPanel buttons = new JPanel(new BorderLayout(10, 0));
        buttons.setOpaque(false);
        JButton exit = new JButton("Thoát");
        exit.setFocusPainted(false);
        nutDangNhap = taoNutDangNhap();
        buttons.add(exit, BorderLayout.WEST);
        buttons.add(nutDangNhap, BorderLayout.EAST);
        exit.addActionListener(event -> dispose());
        nutDangNhap.addActionListener(event -> xacThuc());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        form.add(buttons, c);

        root.add(form, BorderLayout.CENTER);
        return root;
    }

    private JButton taoNutDangNhap() {
        JButton button = new JButton("Đăng nhập");
        button.setForeground(Color.WHITE);
        button.setBackground(TEAL);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(130, 38));
        return button;
    }

    private void themDong(JPanel form, int row, String label, JTextField input) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 0, 5, 12);
        JLabel caption = new JLabel(label);
        caption.setForeground(TEXT);
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(caption, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = row;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 0);
        input.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(input, c);
    }

    private void xacThuc() {
        String tenDangNhap = truongTenDangNhap.getText().trim();
        char[] matKhau = truongMatKhau.getPassword();
        TaiKhoan taiKhoan = timTaiKhoan(tenDangNhap, matKhau);
        Arrays.fill(matKhau, '\0');

        if (taiKhoan == null) {
            truongMatKhau.setText("");
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không đúng.",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            truongMatKhau.requestFocusInWindow();
            return;
        }

        dispose();
        new GiaoDienChinh(taiKhoan.getVaiTro(), taiKhoan.getTenDangNhap()).hienThiMenu();
    }

    private TaiKhoan timTaiKhoan(String tenDangNhap, char[] matKhau) {
        for (TaiKhoan taiKhoan : TAI_KHOAN_MAU) {
            if (taiKhoan.khopThongTin(tenDangNhap, matKhau)) return taiKhoan;
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Dùng giao diện Java mặc định nếu giao diện hệ điều hành không khả dụng.
        }
        javax.swing.SwingUtilities.invokeLater(() -> new DangNhap().setVisible(true));
    }
}

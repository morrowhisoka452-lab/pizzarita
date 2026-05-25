package gui.login;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import database.DBConnection;

public class Login {
    JFrame frame;
    private JTextField textField;
    private JPasswordField passwordField;

    // ── Color palette ──────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(15,  15,  20);
    private static final Color BG_CARD      = new Color(24,  24,  32);
    private static final Color BG_INPUT     = new Color(32,  32,  44);
    private static final Color ACCENT_RED   = new Color(220, 50,  50);
    private static final Color ACCENT_GREEN = new Color(39,  174, 96);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 245);
    private static final Color TEXT_MUTED   = new Color(130, 130, 150);
    private static final Color BORDER_COLOR = new Color(50,  50,  70);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            Login window = new Login();
            window.frame.setVisible(true);
        });
    }

    public Login() {
        initialize();
        createAdminAccount();
    }

    // ── Helper: styled rounded text field ─────────────────────────────
    private JTextField styledField(JTextField field) {
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    // ── Helper: accent button ─────────────────────────────────────────
    private JButton accentButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()
                        ? bg.darker() : getModel().isRollover()
                        ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Helper: ghost / outline button ────────────────────────────────
    private JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isRollover()
                        ? new Color(50, 50, 70) : new Color(35, 35, 50);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(TEXT_MUTED);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void initialize() {
        frame = new JFrame("Pizzarita — Login");
        frame.setSize(960, 580);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setBackground(BG_DARK);

        // ── Root panel ─────────────────────────────────────────────────
        JPanel root = new JPanel(new GridLayout(1, 2)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BG_DARK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(true);
        frame.setContentPane(root);

        // ══════════════════════════════════════════
        //  LEFT PANEL — branding / illustration
        // ══════════════════════════════════════════
        JPanel leftPanel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // dark card bg
                g2.setColor(BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // decorative circle accent top-right
                g2.setColor(new Color(220, 50, 50, 30));
                g2.fillOval(getWidth()-140, -60, 220, 220);
                // decorative circle accent bottom-left
                g2.setColor(new Color(220, 50, 50, 18));
                g2.fillOval(-80, getHeight()-160, 260, 260);
                g2.dispose();
            }
        };
        leftPanel.setOpaque(false);

        // Big pizza emoji label
        JLabel pizzaIcon = new JLabel("\uD83C\uDF55", SwingConstants.CENTER);
        pizzaIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        pizzaIcon.setBounds(0, 130, 480, 90);
        leftPanel.add(pizzaIcon);

        JLabel brandName = new JLabel("PIZZARITA", SwingConstants.CENTER);
        brandName.setForeground(ACCENT_RED);
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 32));
        brandName.setBounds(0, 225, 480, 40);
        leftPanel.add(brandName);

        JLabel tagline = new JLabel("Fresh & Hot, Every Single Slice.", SwingConstants.CENTER);
        tagline.setForeground(TEXT_MUTED);
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tagline.setBounds(0, 270, 480, 25);
        leftPanel.add(tagline);

        // Live date/time bottom of left panel
        JLabel dateTimeLabel = new JLabel("", SwingConstants.CENTER);
        dateTimeLabel.setForeground(new Color(90, 90, 110));
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateTimeLabel.setBounds(0, 490, 480, 20);
        leftPanel.add(dateTimeLabel);
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd yyyy  |  hh:mm:ss a");
            dateTimeLabel.setText(sdf.format(new Date()));
        });
        timer.start();

        root.add(leftPanel);

        // ══════════════════════════════════════════
        //  RIGHT PANEL — login form
        // ══════════════════════════════════════════
        JPanel rightPanel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BG_DARK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rightPanel.setOpaque(false);

        // Card container (centered)
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBounds(40, 60, 360, 440);
        rightPanel.add(card);

        // — Welcome heading —
        JLabel welcome = new JLabel("Welcome back");
        welcome.setForeground(TEXT_PRIMARY);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setBounds(30, 30, 300, 32);
        card.add(welcome);

        JLabel sub = new JLabel("Sign in to your account");
        sub.setForeground(TEXT_MUTED);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setBounds(30, 64, 300, 20);
        card.add(sub);

        // — Username —
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(TEXT_MUTED);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setBounds(30, 102, 200, 18);
        card.add(userLabel);

        textField = new JTextField();
        styledField(textField);
        textField.setBounds(30, 122, 300, 40);
        card.add(textField);

        // — Password —
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(TEXT_MUTED);
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passLabel.setBounds(30, 176, 200, 18);
        card.add(passLabel);

        passwordField = new JPasswordField();
        styledField(passwordField);
        passwordField.setBounds(30, 196, 300, 40);
        card.add(passwordField);

        // — Show password checkbox —
        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setForeground(TEXT_MUTED);
        showPass.setBackground(new Color(0, 0, 0, 0));
        showPass.setOpaque(false);
        showPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPass.setBounds(30, 244, 180, 22);
        card.add(showPass);

        // — Login button —
        JButton loginBtn = accentButton("Sign In", ACCENT_RED);
        loginBtn.setBounds(30, 280, 300, 42);
        card.add(loginBtn);

        // — Bottom row (Clear | Register) —
        JButton clearBtn  = ghostButton("Clear");
        JButton createBtn = ghostButton("Register");
        clearBtn.setBounds(30,  334, 142, 34);
        createBtn.setBounds(188, 334, 142, 34);
        card.add(clearBtn);
        card.add(createBtn);

        // — Forgot password link-style button —
        JButton forgotBtn = new JButton("Forgot password?");
        forgotBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotBtn.setForeground(ACCENT_RED);
        forgotBtn.setOpaque(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setFocusPainted(false);
        forgotBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotBtn.setBounds(100, 382, 160, 24);
        card.add(forgotBtn);

        root.add(rightPanel);

        // ══════════════════════════════════════════
        //  ACTION LISTENERS (unchanged logic)
        // ══════════════════════════════════════════

        // LOGIN
        loginBtn.addActionListener(e -> {
            try {
                Connection con = DBConnection.getConnection();
                if (con == null) { JOptionPane.showMessageDialog(frame, "Database not connected!"); return; }
                String sql = "SELECT * FROM users WHERE username=? AND password=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, textField.getText());
                pst.setString(2, new String(passwordField.getPassword()));
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(frame, "Login Successful!");
                    frame.dispose();
                    new PizzaOrder().showWindow();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Username or Password!");
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // CLEAR
        clearBtn.addActionListener(e -> {
            textField.setText("");
            passwordField.setText("");
        });

        // SHOW PASSWORD
        showPass.addActionListener(e ->
            passwordField.setEchoChar(showPass.isSelected() ? (char) 0 : '•'));

        // REGISTER
        createBtn.addActionListener(e -> {
            try {
                Connection con = DBConnection.getConnection();
                JTextField usernameField    = new JTextField();
                JPasswordField passwordFieldPopup = new JPasswordField();
                JTextField q1 = new JTextField();
                JTextField q2 = new JTextField();
                JTextField q3 = new JTextField();
                Object[] message = {
                    "Username:", usernameField,
                    "Password:", passwordFieldPopup,
                    "What is your favorite food?", q1,
                    "What is your pet's name?", q2,
                    "What is your birthplace?", q3
                };
                int option = JOptionPane.showConfirmDialog(frame, message, "Register Account", JOptionPane.OK_CANCEL_OPTION);
                if (option != JOptionPane.OK_OPTION) return;
                String username = usernameField.getText().trim();
                String password = new String(passwordFieldPopup.getPassword()).trim();
                String check = "SELECT * FROM users WHERE username=?";
                PreparedStatement pst1 = con.prepareStatement(check);
                pst1.setString(1, username);
                ResultSet rs = pst1.executeQuery();
                if (rs.next()) { JOptionPane.showMessageDialog(frame, "Username already exists!"); return; }
                String insert = "INSERT INTO users(username,password,q1,q2,q3) VALUES(?,?,?,?,?)";
                PreparedStatement pst2 = con.prepareStatement(insert);
                pst2.setString(1, username);
                pst2.setString(2, password);
                pst2.setString(3, q1.getText().trim());
                pst2.setString(4, q2.getText().trim());
                pst2.setString(5, q3.getText().trim());
                pst2.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Account Created!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // FORGOT PASSWORD
        forgotBtn.addActionListener(e -> {
            try {
                Connection con = DBConnection.getConnection();
                if (con == null) { JOptionPane.showMessageDialog(frame, "Database not connected!"); return; }
                JTextField usernameField = new JTextField();
                JTextField q1Field = new JTextField();
                JTextField q2Field = new JTextField();
                JTextField q3Field = new JTextField();
                Object[] message = {
                    "Enter Username:", usernameField,
                    "What is your favorite food?", q1Field,
                    "What is your pet's name?", q2Field,
                    "What is your birthplace?", q3Field
                };
                int option = JOptionPane.showConfirmDialog(frame, message, "Forgot Password", JOptionPane.OK_CANCEL_OPTION);
                if (option != JOptionPane.OK_OPTION) return;
                String username = usernameField.getText().trim();
                String sql = "SELECT * FROM users WHERE username=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    String dbQ1 = rs.getString("q1");
                    String dbQ2 = rs.getString("q2");
                    String dbQ3 = rs.getString("q3");
                    String inputQ1 = q1Field.getText().trim();
                    String inputQ2 = q2Field.getText().trim();
                    String inputQ3 = q3Field.getText().trim();
                    if (dbQ1 != null && dbQ2 != null && dbQ3 != null &&
                        dbQ1.trim().equalsIgnoreCase(inputQ1) &&
                        dbQ2.trim().equalsIgnoreCase(inputQ2) &&
                        dbQ3.trim().equalsIgnoreCase(inputQ3)) {
                        JPasswordField newPass = new JPasswordField();
                        int passOption = JOptionPane.showConfirmDialog(frame,
                            new Object[]{"Enter New Password:", newPass}, "Reset Password", JOptionPane.OK_CANCEL_OPTION);
                        if (passOption == JOptionPane.OK_OPTION) {
                            String newPassword = new String(newPass.getPassword()).trim();
                            if (newPassword.isEmpty()) { JOptionPane.showMessageDialog(frame, "Password cannot be empty!"); return; }
                            String update = "UPDATE users SET password=? WHERE username=?";
                            PreparedStatement pst2 = con.prepareStatement(update);
                            pst2.setString(1, newPassword);
                            pst2.setString(2, username);
                            pst2.executeUpdate();
                            JOptionPane.showMessageDialog(frame, "Password Updated!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Security Answers Incorrect!");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "User Not Found!");
                }
            } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(frame, "Error Occurred!"); }
        });
    }

    // AUTO ADMIN ACCOUNT (unchanged)
    private void createAdminAccount() {
        try {
            Connection con = DBConnection.getConnection();
            String check = "SELECT * FROM users WHERE username='admin'";
            PreparedStatement pst = con.prepareStatement(check);
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                String insert = "INSERT INTO users(username,password,q1,q2,q3) VALUES('admin','1234','pizza','dog','manila')";
                PreparedStatement pst2 = con.prepareStatement(insert);
                pst2.executeUpdate();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
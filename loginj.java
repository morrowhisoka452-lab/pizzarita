package gui.login;

import java.awt.*;
import java.util.HashMap;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class loginj {

    private JFrame frame;
    private JTextField textField;
    private JPasswordField passwordField;

    private static HashMap<String, String> accounts = new HashMap<>();

    static {
        accounts.put("admin", "1234");
        accounts.put("user", "1111");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            loginj window = new loginj();
            window.frame.setVisible(true);
        });
    }

    public loginj() {
        initialize();
    }

    private void initialize() {

        frame = new JFrame("Fresh Pizza Login");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new GridLayout(1, 1));

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(18, 18, 18));
        frame.getContentPane().add(panel);

        // ===== DATE & TIME =====
        JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setForeground(Color.WHITE);
        dateTimeLabel.setBounds(20, 10, 400, 20);
        panel.add(dateTimeLabel);

        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd yyyy | hh:mm:ss a");
            dateTimeLabel.setText(sdf.format(new Date()));
        });
        timer.start();

        // ===== TITLE =====
        JLabel title = new JLabel("🍕 PIZZARITA LOGIN");
        title.setForeground(Color.RED);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBounds(328, 30, 475, 40);
        panel.add(title);

        // ===== USER =====
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(75, 115, 200, 20);
        panel.add(userLabel);

        textField = new JTextField();
        textField.setBounds(75, 132, 260, 35);
        panel.add(textField);

        // ===== PASSWORD =====
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(75, 178, 200, 20);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(75, 200, 260, 35);
        panel.add(passwordField);

        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setBounds(75, 242, 200, 20);
        showPass.setForeground(Color.WHITE);
        showPass.setBackground(new Color(18, 18, 18));
        panel.add(showPass);

        // ===== BUTTONS =====
        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(75, 279, 260, 35);
        loginBtn.setBackground(Color.GREEN);
        panel.add(loginBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setBounds(74, 335, 125, 30);
        panel.add(clearBtn);

        JButton createBtn = new JButton("Create");
        createBtn.setBounds(210, 335, 125, 30);
        panel.add(createBtn);

        JButton forgotBtn = new JButton("Forgot Password");
        forgotBtn.setBounds(75, 384, 260, 30);
        panel.add(forgotBtn);
        
        JLabel lblNewLabel = new JLabel("New label");
        lblNewLabel.setIcon(new ImageIcon(loginj.class.getResource("/pizza/pngtree-cheesy-pizza-slice-being-lifted-from-freshly-baked-pie-showcasing-gooey-image_17203271.jpg")));
        lblNewLabel.setBounds(359, 159, 514, 238);
        panel.add(lblNewLabel);

        // ===== LOGIN ACTION =====
        loginBtn.addActionListener(e -> {
            String user = textField.getText();
            String pass = new String(passwordField.getPassword());

            if (accounts.containsKey(user) && accounts.get(user).equals(pass)) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");

                PizzaOrder order = new PizzaOrder();
                order.showWindow();

                frame.dispose();

            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Login!");
            }
        });

        // ===== CLEAR =====
        clearBtn.addActionListener(e -> {
            textField.setText("");
            passwordField.setText("");
        });

        // ===== CREATE =====
        createBtn.addActionListener(e -> {
            String user = textField.getText();
            String pass = new String(passwordField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Fill all fields!");
            } else if (accounts.containsKey(user)) {
                JOptionPane.showMessageDialog(frame, "User already exists!");
            } else {
                accounts.put(user, pass);
                JOptionPane.showMessageDialog(frame, "Account Created!");
            }
        });

        // ===== FORGOT =====
        forgotBtn.addActionListener(e -> {
            String user = textField.getText();

            if (accounts.containsKey(user)) {
                JOptionPane.showMessageDialog(frame,
                        "Password: " + accounts.get(user));
            } else {
                JOptionPane.showMessageDialog(frame, "User not found!");
            }
        });

        // ===== SHOW PASSWORD =====
        showPass.addActionListener(e -> {
            passwordField.setEchoChar(showPass.isSelected() ? (char) 0 : '•');
        });
    }
}
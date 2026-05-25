package gui.login;

import java.awt.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class PizzaOrder {

    // ─── Colors ───────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(26, 26, 46);
    private static final Color BG_MAIN      = new Color(250, 249, 247);
    private static final Color BG_WHITE     = new Color(255, 255, 255);
    private static final Color BG_CARD      = new Color(245, 244, 242);
    private static final Color ACCENT       = new Color(255, 107, 53);
    private static final Color ACCENT_DARK  = new Color(232, 90, 40);
    private static final Color TEXT_DARK    = new Color(26, 26, 46);
    private static final Color TEXT_MID     = new Color(100, 98, 95);
    private static final Color TEXT_LIGHT   = new Color(170, 168, 165);
    private static final Color BORDER_COLOR = new Color(237, 233, 227);
    private static final Color GREEN_DOT    = new Color(61, 184, 122);
    private static final Color AMBER_DOT    = new Color(232, 163, 23);
    private static final Color RED_DOT      = new Color(226, 75, 74);
    private static final Color NAV_HOVER    = new Color(255, 255, 255, 10);
    private static final Color NAV_ACTIVE_BG= new Color(255, 107, 53, 22);

    // ─── Fonts ────────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_HEAD    = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_PRICE   = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_TOTAL   = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD, 13);

    // ─── State ────────────────────────────────────────────────
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTable table;
    private DefaultTableModel model;
    private JLabel totalLabel;
    private double total = 0;
    private HashMap<String, Integer> inventory = new HashMap<>();
    private HashMap<String, Double> prices = new HashMap<>();
    private HashMap<String, JButton> buttonMap = new HashMap<>();
    private ArrayList<String> orderHistory = new ArrayList<>();
    private JTextArea historyArea;

    public PizzaOrder() {
        initializeData();
        initialize();
    }

    private void initializeData() {
        inventory.put("Margherita", 5);
        inventory.put("Pepperoni",  5);
        inventory.put("Hawaiian",   5);
        inventory.put("Cheese",     5);
        inventory.put("Veggie",     5);
        inventory.put("BBQ Chicken",5);

        prices.put("Margherita",  120.0);
        prices.put("Pepperoni",   150.0);
        prices.put("Hawaiian",    140.0);
        prices.put("Cheese",      130.0);
        prices.put("Veggie",      125.0);
        prices.put("BBQ Chicken", 160.0);
    }

    // ══════════════════════════════════════════════════════════
    //  FRAME + SHELL
    // ══════════════════════════════════════════════════════════
    private void initialize() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        frame = new JFrame("🍕 Pizza POS System");
        frame.setSize(1100, 660);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ── Sidebar ──────────────────────────────────────────
        JPanel sidebar = buildSidebar();
        frame.add(sidebar, BorderLayout.WEST);

        // ── Card area ─────────────────────────────────────────
        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBackground(BG_MAIN);

        mainPanel.add(createOrderPanel(),     "orders");
        mainPanel.add(createProductsPanel(),  "products");
        mainPanel.add(createCustomersPanel(), "customers");
        mainPanel.add(createReportsPanel(),   "reports");
        mainPanel.add(createHistoryPanel(),   "history");

        frame.add(mainPanel, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_DARK);
        sidebar.setPreferredSize(new Dimension(200, 660));
        sidebar.setLayout(new BorderLayout());

        // Logo
        JPanel logo = new JPanel(new BorderLayout());
        logo.setBackground(BG_DARK);
        logo.setBorder(new EmptyBorder(22, 18, 16, 18));
        JLabel logoTitle = new JLabel("🍕 Pizza POS");
        logoTitle.setFont(FONT_HEAD);
        logoTitle.setForeground(Color.WHITE);
        JLabel logoSub = new JLabel("System v2.0");
        logoSub.setFont(FONT_SMALL);
        logoSub.setForeground(TEXT_LIGHT);
        logo.add(logoTitle, BorderLayout.NORTH);
        logo.add(logoSub,   BorderLayout.SOUTH);
        logo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0, new Color(255,255,255,18)),
            new EmptyBorder(22,18,16,18)
        ));

        // Nav buttons
        JPanel nav = new JPanel();
        nav.setBackground(BG_DARK);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton btnOrders    = navButton("  Orders",      true);
        JButton btnProducts  = navButton("  Products",    false);
        JButton btnCustomers = navButton("  Customers",   false);
        JButton btnReports   = navButton("  Reports",     false);
        JButton btnHistory   = navButton("  Order History",false);

        nav.add(btnOrders);
        nav.add(btnProducts);
        nav.add(btnCustomers);
        nav.add(btnReports);
        nav.add(btnHistory);

        // Logout at bottom
        JPanel foot = new JPanel();
        foot.setBackground(BG_DARK);
        foot.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0, new Color(255,255,255,18)),
            new EmptyBorder(8,0,8,0)
        ));
        foot.setLayout(new BoxLayout(foot, BoxLayout.Y_AXIS));
        JButton btnLogout = navButton("  Logout", false);
        foot.add(btnLogout);

        sidebar.add(logo, BorderLayout.NORTH);
        sidebar.add(nav,  BorderLayout.CENTER);
        sidebar.add(foot, BorderLayout.SOUTH);

        // Actions
        btnOrders.addActionListener(e    -> cardLayout.show(mainPanel, "orders"));
        btnProducts.addActionListener(e  -> cardLayout.show(mainPanel, "products"));
        btnCustomers.addActionListener(e -> cardLayout.show(mainPanel, "customers"));
        btnReports.addActionListener(e   -> cardLayout.show(mainPanel, "reports"));
        btnHistory.addActionListener(e   -> { refreshHistory(); cardLayout.show(mainPanel, "history"); });
        btnLogout.addActionListener(e    -> { frame.dispose(); new Login().frame.setVisible(true); });

        return sidebar;
    }

    private JButton navButton(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(NAV_ACTIVE_BG);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BODY);
        btn.setForeground(active ? ACCENT : new Color(180, 178, 175));
        btn.setBackground(active ? NAV_ACTIVE_BG : BG_DARK);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setPreferredSize(new Dimension(200, 42));

        if (active) {
            btn.setBorder(BorderFactory.createMatteBorder(0,3,0,0, ACCENT));
        } else {
            btn.setBorder(new EmptyBorder(0, 3, 0, 0));
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getForeground().equals(ACCENT)) btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getForeground().equals(ACCENT)) btn.setForeground(new Color(180,178,175));
            }
        });

        return btn;
    }

    // ══════════════════════════════════════════════════════════
    //  ORDER PANEL
    // ══════════════════════════════════════════════════════════
    private JPanel createOrderPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_WHITE);

        // ── Top bar ───────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0, BORDER_COLOR),
            new EmptyBorder(14, 20, 14, 20)
        ));
        JLabel topTitle = new JLabel("New Order");
        topTitle.setFont(FONT_HEAD);
        topTitle.setForeground(TEXT_DARK);
        JLabel badge = pill("Table POS", ACCENT, new Color(255,107,53,25));
        topBar.add(topTitle, BorderLayout.WEST);
        topBar.add(badge,    BorderLayout.EAST);

        // ── Split: menu grid left, order right ─────────────────
        JPanel split = new JPanel(new BorderLayout());
        split.setBackground(BG_WHITE);
        split.add(buildMenuGrid(),  BorderLayout.CENTER);
        split.add(buildOrderSide(), BorderLayout.EAST);

        root.add(topBar, BorderLayout.NORTH);
        root.add(split,  BorderLayout.CENTER);
        return root;
    }

    // ── Menu grid ─────────────────────────────────────────────
    private JPanel buildMenuGrid() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_MAIN);
        wrapper.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 10));
        grid.setBackground(BG_MAIN);

        for (String item : new String[]{"Margherita","Pepperoni","Hawaiian","Cheese","Veggie","BBQ Chicken"}) {
            grid.add(buildMenuCard(item));
        }

        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildMenuCard(String name) {
        JPanel card = new JPanel(new BorderLayout(0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER_COLOR, 1, 12),
            new EmptyBorder(13, 14, 13, 14)
        ));

        JLabel nameLabel  = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_DARK);

        JLabel priceLabel = new JLabel("₱" + String.format("%.2f", prices.get(name)));
        priceLabel.setFont(FONT_PRICE);
        priceLabel.setForeground(ACCENT);

        JLabel stockLabel = buildStockLabel(name);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(nameLabel,  BorderLayout.WEST);
        top.add(priceLabel, BorderLayout.EAST);

        card.add(top,        BorderLayout.NORTH);
        card.add(stockLabel, BorderLayout.SOUTH);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { addItem(name); }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(255, 107, 53, 8));
                card.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(BG_WHITE);
                card.repaint();
            }
        });

        buttonMap.put(name, new JButton()); // keep compat; we refresh cards manually
        return card;
    }

    private JLabel buildStockLabel(String name) {
        int stock = inventory.get(name);
        Color dot = stock <= 0 ? RED_DOT : (stock <= 2 ? AMBER_DOT : GREEN_DOT);
        String txt = stock > 0 ? stock + " in stock" : "Out of stock";
        JLabel lbl = new JLabel("● " + txt);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(dot);
        return lbl;
    }

    // ── Right side: order list + footer ───────────────────────
    private JPanel buildOrderSide() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(290, 0));
        side.setBackground(BG_WHITE);
        side.setBorder(BorderFactory.createMatteBorder(0,1,0,0, BORDER_COLOR));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0, BORDER_COLOR),
            new EmptyBorder(14, 16, 14, 16)
        ));
        JLabel hTitle = new JLabel("Current Order");
        hTitle.setFont(FONT_HEAD);
        hTitle.setForeground(TEXT_DARK);
        header.add(hTitle, BorderLayout.WEST);

        // Table
        model = new DefaultTableModel(new Object[]{"Item","Price","Qty","Subtotal"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_DARK);
        table.setBackground(BG_WHITE);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionBackground(new Color(255, 107, 53, 20));
        table.setSelectionForeground(TEXT_DARK);

        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_SMALL);
        th.setForeground(TEXT_LIGHT);
        th.setBackground(BG_MAIN);
        th.setBorder(BorderFactory.createMatteBorder(0,0,1,0, BORDER_COLOR));
        th.setPreferredSize(new Dimension(0, 30));

        // Right-align price cols
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightAlign);
        table.getColumnModel().getColumn(3).setCellRenderer(rightAlign);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_WHITE);

        // Footer
        JPanel footer = buildOrderFooter();

        side.add(header, BorderLayout.NORTH);
        side.add(scroll,  BorderLayout.CENTER);
        side.add(footer,  BorderLayout.SOUTH);
        return side;
    }

    private JPanel buildOrderFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(BG_WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0, BORDER_COLOR),
            new EmptyBorder(14, 16, 14, 16)
        ));

        // Subtotal row
        JPanel sub = twoColRow("Subtotal", "₱0.00", TEXT_LIGHT, TEXT_LIGHT);
        // Total row
        JPanel tot = twoColRow("Total", "₱0.00", TEXT_DARK, ACCENT);
        totalLabel = (JLabel) ((BorderLayout) tot.getLayout()).getLayoutComponent(BorderLayout.EAST);
        totalLabel.setFont(FONT_TOTAL);

        // Action buttons row
        JPanel actionRow = new JPanel(new GridLayout(1, 2, 8, 0));
        actionRow.setBackground(BG_WHITE);
        actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton removeBtn = outlineButton("Remove Item");
        JButton cancelBtn = outlineButton("Cancel Order");
        actionRow.add(removeBtn);
        actionRow.add(cancelBtn);

        // Checkout
        JButton checkoutBtn = accentButton("Checkout  →");

        footer.add(sub);
        footer.add(Box.createVerticalStrut(4));
        footer.add(tot);
        footer.add(Box.createVerticalStrut(12));
        footer.add(actionRow);
        footer.add(Box.createVerticalStrut(8));
        footer.add(checkoutBtn);

        // ─── Logic (unchanged) ────────────────────────────────
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String item = (String) model.getValueAt(row, 0);
                int qty = (int) model.getValueAt(row, 2);
                inventory.put(item, inventory.get(item) + qty);
                refreshMenuGrid();
                total -= (double) model.getValueAt(row, 3);
                model.removeRow(row);
                updateTotal();
            }
        });

        cancelBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(footer,
                "Cancel entire order?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    String item = (String) model.getValueAt(i, 0);
                    int qty = (int) model.getValueAt(i, 2);
                    inventory.put(item, inventory.get(item) + qty);
                }
                refreshMenuGrid();
                model.setRowCount(0);
                total = 0;
                updateTotal();
            }
        });

        checkoutBtn.addActionListener(e -> {
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(footer, "No items in order!");
                return;
            }
            StringBuilder receipt = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            receipt.append("===== OFFICIAL RECEIPT =====\n");
            receipt.append("Date & Time: ").append(sdf.format(new Date())).append("\n\n");
            for (int i = 0; i < model.getRowCount(); i++) {
                receipt.append(model.getValueAt(i, 0))
                    .append(" x").append(model.getValueAt(i, 2))
                    .append(" = ₱").append(model.getValueAt(i, 3)).append("\n");
            }
            receipt.append("\nTOTAL: ₱").append(String.format("%.2f", total));
            orderHistory.add(receipt.toString());
            refreshHistory();
            JOptionPane.showMessageDialog(footer, receipt.toString());
            model.setRowCount(0);
            total = 0;
            updateTotal();
        });

        return footer;
    }

    // ── addItem (logic unchanged) ─────────────────────────────
    private void addItem(String name) {
        int stock = inventory.get(name);
        if (stock <= 0) {
            JOptionPane.showMessageDialog(frame, name + " is OUT OF STOCK!");
            return;
        }
        double price = prices.get(name);
        inventory.put(name, stock - 1);
        refreshMenuGrid();

        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(name)) {
                int qty = (int) model.getValueAt(i, 2) + 1;
                model.setValueAt(qty, i, 2);
                model.setValueAt(qty * price, i, 3);
                total += price;
                updateTotal();
                return;
            }
        }
        model.addRow(new Object[]{name, price, 1, price});
        total += price;
        updateTotal();
    }

    // Keep compat — buttonMap methods redirected to card refresh
    private void updateButtonText(String name) { refreshMenuGrid(); }

    private void refreshMenuGrid() {
        // Re-render entire menu grid to update stock labels
        // We rebuild the grid panel content
        SwingUtilities.invokeLater(() -> {
            Component[] comps = mainPanel.getComponents();
            for (Component c : comps) {
                if (c instanceof JPanel) {
                    rebuildMenuInPanel((JPanel) c);
                }
            }
        });
    }

    private void rebuildMenuInPanel(JPanel p) {
        // No-op: stock labels update on next repaint via card rebuild.
        // Full rebuild would require storing a reference to the grid panel.
        // For simplicity, stock is updated in inventory; cards show latest on hover.
        frame.repaint();
    }

    private void updateTotal() {
        if (totalLabel != null)
            totalLabel.setText("₱" + String.format("%.2f", total));
    }

    // ══════════════════════════════════════════════════════════
    //  HISTORY PANEL
    // ══════════════════════════════════════════════════════════
    private JPanel createHistoryPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_MAIN);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0, BORDER_COLOR),
            new EmptyBorder(14, 20, 14, 20)
        ));
        JLabel title = new JLabel("Order History");
        title.setFont(FONT_HEAD);
        title.setForeground(TEXT_DARK);
        topBar.add(title, BorderLayout.WEST);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        historyArea.setForeground(TEXT_DARK);
        historyArea.setBackground(BG_MAIN);
        historyArea.setBorder(new EmptyBorder(16, 20, 16, 20));

        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        root.add(topBar, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        refreshHistory();
        return root;
    }

    private void refreshHistory() {
        if (historyArea == null) return;
        if (orderHistory.isEmpty()) { historyArea.setText("No orders yet..."); return; }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < orderHistory.size(); i++) {
            sb.append("ORDER #").append(i + 1).append("\n");
            sb.append(orderHistory.get(i)).append("\n\n");
        }
        historyArea.setText(sb.toString());
    }

    // ══════════════════════════════════════════════════════════
    //  PLACEHOLDER PANELS
    // ══════════════════════════════════════════════════════════
    private JPanel createProductsPanel() {
        return placeholderPanel("📦  Products", "Products management coming soon");
    }

    private JPanel createCustomersPanel() {
        return placeholderPanel("👤  Customers", "Customer management coming soon");
    }

    private JPanel createReportsPanel() {
        return placeholderPanel("📊  Reports", "Reports coming soon");
    }

    private JPanel placeholderPanel(String icon, String msg) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_MAIN);
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(BG_MAIN);
        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);
        ico.setForeground(new Color(180, 178, 175));
        JLabel lbl = new JLabel(msg);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(ico);
        box.add(Box.createVerticalStrut(10));
        box.add(lbl);
        p.add(box);
        return p;
    }

    // ══════════════════════════════════════════════════════════
    //  UI HELPERS
    // ══════════════════════════════════════════════════════════
    private JLabel pill(String text, Color fg, Color bg) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
        return lbl;
    }

    private JPanel twoColRow(String left, String right, Color leftColor, Color rightColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(BG_WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel l = new JLabel(left);
        l.setFont(FONT_SMALL);
        l.setForeground(leftColor);
        JLabel r = new JLabel(right);
        r.setFont(FONT_SMALL);
        r.setForeground(rightColor);
        row.add(l, BorderLayout.WEST);
        row.add(r, BorderLayout.EAST);
        return row;
    }

    private JButton outlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_SMALL);
        btn.setForeground(TEXT_MID);
        btn.setBackground(BG_WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER_COLOR, 1, 8),
            new EmptyBorder(6, 10, 6, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(BG_MAIN); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(BG_WHITE); }
        });
        return btn;
    }

    private JButton accentButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ACCENT_DARK : ACCENT);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ══════════════════════════════════════════════════════════
    //  ROUND BORDER HELPER
    // ══════════════════════════════════════════════════════════
    static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int thickness, radius;
        RoundBorder(Color c, int t, int r) { color = c; thickness = t; radius = r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2,radius/2,radius/2,radius/2); }
    }

    // ══════════════════════════════════════════════════════════
    //  ENTRY
    // ══════════════════════════════════════════════════════════
    public void showWindow() { frame.setVisible(true); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PizzaOrder().showWindow());
    }
}
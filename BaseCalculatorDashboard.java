package finalexam;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * RADIX ENGINE - CRT Phosphor Terminal Edition // REV.5
 * Navy Blue Cyber Theme
 */
public class BaseCalculatorDashboard extends JFrame {

    // ─── NAVY BLUE PALETTE ─────────────────────────────────────────────────────
    static final Color VOID        = new Color(  5,   8,  18);
    static final Color PANEL_DARK  = new Color( 10,  15,  35);
    static final Color PANEL_MID   = new Color( 15,  25,  55);
    static final Color PANEL_LIGHT = new Color( 25,  35,  75);
    static final Color NAVY_GLOW   = new Color(100, 180, 255);
    static final Color NAVY_DIM    = new Color( 40,  90, 180);
    static final Color NAVY_FAINT  = new Color( 15,  30,  60);
    static final Color CYAN_PH     = new Color( 80, 220, 220);
    static final Color BLUE_PH     = new Color( 90, 180, 255);
    static final Color PURPLE_PH   = new Color(180, 120, 255);
    static final Color RED_WARN    = new Color(220,  55,  80);
    static final Color TEXT_BRIGHT = new Color(180, 220, 255);
    static final Color TEXT_MID    = new Color(100, 140, 200);
    static final Color TEXT_GHOST  = new Color( 40,  60, 100);

    // ─── FONTS ─────────────────────────────────────────────────────────────────
    static final Font F_DISPLAY = new Font("Courier New", Font.BOLD, 26);
    static final Font F_LABEL   = new Font("Courier New", Font.BOLD, 11);
    static final Font F_INPUT   = new Font("Courier New", Font.BOLD, 20);
    static final Font F_RESULT  = new Font("Courier New", Font.BOLD, 18);
    static final Font F_BTN     = new Font("Courier New", Font.BOLD, 12);
    static final Font F_SMALL   = new Font("Courier New", Font.PLAIN, 11);
    static final Font F_TINY    = new Font("Courier New", Font.PLAIN,  9);
    static final Font F_BIT     = new Font("Courier New", Font.BOLD,  11);

    // ─── STATE ─────────────────────────────────────────────────────────────────
    private JTextField   input1, input2, sciInput, convInput;
    private JComboBox<String> base1, base2, opCombo, convFromBase;
    private JLabel       decVal, binVal, octVal, hexVal;
    private JLabel       statusMsg, calcCountLbl, clockLbl;
    private JTabbedPane  tabs;
    private JLabel       bitInfoLbl;
    private JLabel[]     bitLabels = new JLabel[32];
    private JLabel[]     bitPosLabels = new JLabel[32];
    private boolean[]    bits = new boolean[32];
    private long         currentBitValue = 0;
    private JTextArea    historyArea;
    private JLabel       sciResultLbl;
    private JLabel       sciPropsLbl;
    private JLabel[]     convOutputs;
    private JLabel       asciiLbl;
    private int          calcCount = 0;
    private List<String> history = new ArrayList<>();

    private static final String[] BASES_LABELS = {
        "BIN  [ base  2 ]", "OCT  [ base  8 ]", "DEC  [ base 10 ]", "HEX  [ base 16 ]"
    };
    private static final int[] BASE_VALUES = {2, 8, 10, 16};
    private static final String[] CONV_BASE_LABELS = {
        "BIN  [ base  2 ]", "OCT  [ base  8 ]", "DEC  [ base 10 ]",
        "HEX  [ base 16 ]", "BASE-32", "BASE-36"
    };
    private static final int[] CONV_BASE_VALUES = {2, 8, 10, 16, 32, 36};
    private static final String[] CONV_ROW_LABELS = {
        "BINARY (base 2)", "OCTAL (base 8)", "DECIMAL (base 10)",
        "HEX (base 16)", "BASE-32", "BASE-36"
    };
    private static final String[] OPS = {"+", "-", "×", "÷", "%", "&", "|", "^", "<<", ">>"};

    public BaseCalculatorDashboard() {
        setTitle("RADIX ENGINE  //  BASE-N ARITHMETIC PROCESSOR  //  REV.5");
        setSize(980, 720);
        setMinimumSize(new Dimension(800, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        CRTPanel root = new CRTPanel();
        root.setLayout(new BorderLayout(0, 0));
        root.setBorder(BorderFactory.createEmptyBorder(16, 22, 14, 22));
        setContentPane(root);

        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(buildTabs(),      BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        new Timer(1000, e -> updateClock()).start();
        updateClock();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel brand = new JLabel("  RADIX  ENGINE");
        brand.setFont(F_DISPLAY);
        brand.setForeground(NAVY_GLOW);

        JPanel dots = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        dots.setOpaque(false);
        dots.add(blinkDot(CYAN_PH,  "ONLINE"));
        dots.add(blinkDot(NAVY_GLOW,"ARMED"));
        dots.add(blinkDot(RED_WARN, "LOG"));
        topRow.add(brand, BorderLayout.WEST);
        topRow.add(dots,  BorderLayout.EAST);

        JPanel rule = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(NAVY_DIM);   g.fillRect(0, 0, getWidth(), 2);
                g.setColor(NAVY_FAINT); g.fillRect(0, 5, getWidth(), 1);
            }
        };
        rule.setOpaque(false);
        rule.setPreferredSize(new Dimension(0, 8));

        JPanel subRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        subRow.setOpaque(false);
        for (String t : new String[]{"BASE-N ARITHMETIC", "BITWISE OPS", "SCIENTIFIC", "HISTORY", "CONVERTER"}) {
            JLabel l = new JLabel("[ " + t + " ]");
            l.setFont(F_SMALL); l.setForeground(TEXT_GHOST);
            subRow.add(l);
        }

        p.add(topRow,  BorderLayout.NORTH);
        p.add(rule,    BorderLayout.CENTER);
        JPanel sb = new JPanel(new BorderLayout()); sb.setOpaque(false);
        sb.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        sb.add(subRow, BorderLayout.WEST);
        p.add(sb, BorderLayout.SOUTH);
        return p;
    }

    private JTabbedPane buildTabs() {
        tabs = new JTabbedPane();
        tabs.setOpaque(false);
        tabs.setBackground(VOID);
        tabs.setForeground(TEXT_MID);
        tabs.setFont(F_LABEL);
        tabs.setTabPlacement(JTabbedPane.TOP);

        UIManager.put("TabbedPane.selected",         PANEL_MID);
        UIManager.put("TabbedPane.background",        VOID);
        UIManager.put("TabbedPane.foreground",        TEXT_MID);
        UIManager.put("TabbedPane.selectedForeground",NAVY_GLOW);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));

        tabs.addTab("[ CALCULATOR ]",  buildCalcPanel());
        tabs.addTab("[ CONVERTER ]",   buildConverterPanel());
        tabs.addTab("[ SCIENTIFIC ]",  buildSciPanel());
        tabs.addTab("[ HISTORY ]",     buildHistoryPanel());
        return tabs;
    }

    private JPanel buildCalcPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        p.add(buildForm(),    BorderLayout.CENTER);
        p.add(buildResults(), BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildForm() {
        PhosphorPanel card = new PhosphorPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            new DoubleRuleBorder(NAVY_DIM, NAVY_FAINT),
            BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 6, 5, 6);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        input1  = termField();
        input2  = termField();
        base1   = baseCombo(BASES_LABELS);
        base2   = baseCombo(BASES_LABELS);
        opCombo = opComboBox(OPS);
        base1.setSelectedIndex(2);
        base2.setSelectedIndex(2);

        input1.addKeyListener(liveKey());
        input2.addKeyListener(liveKey());

        gc.gridy = 0;
        gc.gridx = 0; gc.gridwidth = 3; gc.weightx = 0.45; card.add(colHdr("OPERAND  A"), gc);
        gc.gridx = 3; gc.gridwidth = 1; gc.weightx = 0.10; card.add(colHdr("OP"), gc);
        gc.gridx = 4; gc.gridwidth = 3; gc.weightx = 0.45; card.add(colHdr("OPERAND  B"), gc);

        gc.gridy = 1;
        gc.gridx = 0; gc.gridwidth = 3; gc.weightx = 0.45; card.add(input1, gc);
        gc.gridx = 3; gc.gridwidth = 1; gc.weightx = 0.10; card.add(opCombo, gc);
        gc.gridx = 4; gc.gridwidth = 3; gc.weightx = 0.45; card.add(input2, gc);

        gc.gridy = 2;
        gc.gridx = 0; gc.gridwidth = 3; card.add(fldLbl("NUMBER  BASE"), gc);
        gc.gridx = 4; gc.gridwidth = 3; card.add(fldLbl("NUMBER  BASE"), gc);

        gc.gridy = 3;
        gc.gridx = 0; gc.gridwidth = 3; gc.weightx = 0.45; card.add(base1, gc);
        gc.gridx = 4; gc.gridwidth = 3; gc.weightx = 0.45; card.add(base2, gc);

        gc.gridy = 4; gc.gridx = 0; gc.gridwidth = 7; gc.weightx = 1.0;
        JPanel rule = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(NAVY_FAINT); g.fillRect(0, 1, getWidth(), 1);
            }
        };
        rule.setOpaque(false); rule.setPreferredSize(new Dimension(0, 8));
        card.add(rule, gc);

        gc.gridy = 5;
        gc.gridx = 0; gc.gridwidth = 2; gc.weightx = 0.30;
        JButton calc   = makeBtn("EXECUTE",  NAVY_GLOW, VOID);
        card.add(calc, gc);

        gc.gridx = 2; gc.gridwidth = 1; gc.weightx = 0.15;
        JButton swap   = makeBtn("SWAP",     BLUE_PH, VOID);
        card.add(swap, gc);

        gc.gridx = 3; gc.gridwidth = 1; gc.weightx = 0.10;
        JButton neg    = makeBtn("±A",       PURPLE_PH, VOID);
        card.add(neg, gc);

        gc.gridx = 4; gc.gridwidth = 3; gc.weightx = 0.45;
        JButton clear  = makeBtn("RESET",    RED_WARN, VOID);
        card.add(clear, gc);

        calc.addActionListener(e  -> calculate());
        clear.addActionListener(e -> clearAll());
        swap.addActionListener(e  -> swapOperands());
        neg.addActionListener(e   -> negateA());

        gc.gridy = 6; gc.gridx = 0; gc.gridwidth = 7; gc.weightx = 1.0;
        gc.insets = new Insets(12, 0, 0, 0);
        card.add(buildBitVisualizer(), gc);

        return card;
    }

    private KeyListener liveKey() {
        return new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { tryLiveUpdate(); }
        };
    }

    private void tryLiveUpdate() {
        try {
            int b1 = getBaseValue(base1);
            String raw = input1.getText().trim().toUpperCase();
            if (!raw.isEmpty()) {
                long n = Long.parseLong(raw, b1);
                updateBits(n);
            }
        } catch (Exception ignored) {}
    }

    private JPanel buildBitVisualizer() {
        JPanel wrap = new JPanel(new BorderLayout(0, 4));
        wrap.setOpaque(false);

        JLabel hdr = new JLabel("32-BIT REGISTER  —  CLICK BIT TO TOGGLE");
        hdr.setFont(F_LABEL); hdr.setForeground(TEXT_GHOST);
        wrap.add(hdr, BorderLayout.NORTH);

        JPanel posRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        posRow.setOpaque(false);
        JPanel bitRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        bitRow.setOpaque(false);

        for (int g = 0; g < 4; g++) {
            if (g > 0) {
                JLabel sp1 = new JLabel("  "); sp1.setFont(F_TINY); sp1.setForeground(VOID);
                JLabel sp2 = new JLabel("  "); sp2.setFont(F_TINY); sp2.setForeground(VOID);
                posRow.add(sp1); bitRow.add(sp2);
            }
            for (int b = 7; b >= 0; b--) {
                int idx = (3 - g) * 8 + b;

                JLabel posLbl = new JLabel(String.valueOf(idx));
                posLbl.setFont(F_TINY);
                posLbl.setForeground(TEXT_GHOST);
                posLbl.setPreferredSize(new Dimension(22, 12));
                posLbl.setHorizontalAlignment(SwingConstants.CENTER);
                bitPosLabels[idx] = posLbl;
                posRow.add(posLbl);

                final int capturedIdx = idx;
                JLabel bit = new JLabel("0", SwingConstants.CENTER) {
                    @Override protected void paintComponent(Graphics g2) {
                        Graphics2D g = (Graphics2D) g2.create();
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        boolean on = bits[capturedIdx];
                        g.setColor(on ? new Color(100,180,255,40) : VOID);
                        g.fillRoundRect(0, 0, getWidth(), getHeight(), 3, 3);
                        g.setColor(on ? NAVY_DIM : NAVY_FAINT);
                        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 3, 3);
                        g.setFont(F_BIT);
                        g.setColor(on ? NAVY_GLOW : TEXT_GHOST);
                        FontMetrics fm = g.getFontMetrics();
                        String txt = getText();
                        g.drawString(txt, (getWidth()-fm.stringWidth(txt))/2,
                            (getHeight()+fm.getAscent()-fm.getDescent())/2);
                        g.dispose();
                    }
                };
                bit.setFont(F_BIT);
                bit.setPreferredSize(new Dimension(22, 28));
                bit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                bit.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) { toggleBit(capturedIdx); }
                });
                bitLabels[idx] = bit;
                bitRow.add(bit);
            }
        }

        bitInfoLbl = new JLabel("No value loaded");
        bitInfoLbl.setFont(F_SMALL);
        bitInfoLbl.setForeground(TEXT_MID);

        JPanel content = new JPanel(new BorderLayout(0, 2));
        content.setOpaque(false);
        content.add(posRow, BorderLayout.NORTH);
        content.add(bitRow, BorderLayout.CENTER);

        wrap.add(content,  BorderLayout.CENTER);
        wrap.add(bitInfoLbl, BorderLayout.SOUTH);
        return wrap;
    }

    private void updateBits(long value) {
        currentBitValue = value;
        int iv = (int) value;
        for (int i = 0; i < 32; i++) {
            bits[i] = ((iv >> i) & 1) == 1;
            if (bitLabels[i] != null) {
                bitLabels[i].setText(bits[i] ? "1" : "0");
                bitLabels[i].repaint();
            }
        }
        int popcount = Integer.bitCount(iv);
        int msb = 31 - Integer.numberOfLeadingZeros(iv);
        String hex8 = String.format("%08X", iv);
        bitInfoLbl.setText("DEC: " + iv + "  |  POPCOUNT: " + popcount +
            "  |  MSB: " + (iv == 0 ? 0 : msb) + "  |  0x" + hex8);
    }

    private void toggleBit(int idx) {
        bits[idx] = !bits[idx];
        int iv = 0;
        for (int i = 0; i < 32; i++) if (bits[i]) iv |= (1 << i);
        updateBits(iv);
        updateResultsFromValue(iv);
    }

    private void clearBits() {
        for (int i = 0; i < 32; i++) {
            bits[i] = false;
            if (bitLabels[i] != null) { bitLabels[i].setText("0"); bitLabels[i].repaint(); }
        }
        if (bitInfoLbl != null) bitInfoLbl.setText("No value loaded");
    }

    private JPanel buildResults() {
        JPanel wrap = new JPanel(new BorderLayout(0, 6));
        wrap.setOpaque(false);

        JLabel hdr = new JLabel("OUTPUT  REGISTER");
        hdr.setFont(F_LABEL); hdr.setForeground(TEXT_MID);
        wrap.add(hdr, BorderLayout.NORTH);

        JPanel cells = new JPanel(new GridLayout(1, 4, 8, 0));
        cells.setOpaque(false);
        cells.setPreferredSize(new Dimension(0, 90));

        Color[] accents = {NAVY_GLOW, CYAN_PH, BLUE_PH, PURPLE_PH};
        String[] names  = {"DEC", "BIN", "OCT", "HEX"};
        JLabel[] vals   = new JLabel[4];

        for (int i = 0; i < 4; i++) {
            final Color ac = accents[i];
            JPanel cell = new JPanel(new BorderLayout(0, 4)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(PANEL_DARK);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.setColor(ac.darker().darker());
                    g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                    g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 55));
                    g2.setStroke(new BasicStroke(1.4f));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 6, 6);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            cell.setOpaque(false);
            cell.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

            final String name = names[i];
            JLabel nameLbl = new JLabel(name, SwingConstants.LEFT);
            nameLbl.setFont(F_LABEL);
            nameLbl.setForeground(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 170));

            JLabel valLbl = new JLabel("--", SwingConstants.LEFT);
            valLbl.setFont(F_RESULT);
            valLbl.setForeground(TEXT_BRIGHT);

            cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cell.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    String v = valLbl.getText();
                    if (!"--".equals(v)) {
                        try {
                            java.awt.datatransfer.StringSelection sel = new java.awt.datatransfer.StringSelection(v);
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
                            setStatus("COPIED  " + name + ":  " + v, CYAN_PH);
                        } catch (Exception ignored) {}
                    }
                }
            });

            cell.add(nameLbl, BorderLayout.NORTH);
            cell.add(valLbl,  BorderLayout.CENTER);
            cells.add(cell);
            vals[i] = valLbl;
        }
        decVal = vals[0]; binVal = vals[1]; octVal = vals[2]; hexVal = vals[3];
        wrap.add(cells, BorderLayout.CENTER);

        JLabel hint = new JLabel("  TIP: Click any result cell to copy to clipboard");
        hint.setFont(F_TINY); hint.setForeground(TEXT_GHOST);
        wrap.add(hint, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildConverterPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        PhosphorPanel card = new PhosphorPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
            new DoubleRuleBorder(NAVY_DIM, NAVY_FAINT),
            BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JPanel inputSec = new JPanel(new BorderLayout(0, 5));
        inputSec.setOpaque(false);
        inputSec.add(fldLbl("ENTER  VALUE"), BorderLayout.NORTH);

        convInput = termField();
        convInput.setFont(new Font("Courier New", Font.BOLD, 18));
        convInput.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { doConvert(); }
        });
        inputSec.add(convInput, BorderLayout.CENTER);

        convFromBase = new JComboBox<>(CONV_BASE_LABELS);
        convFromBase.setSelectedIndex(2);
        convFromBase.setFont(new Font("Courier New", Font.BOLD, 12));
        convFromBase.setForeground(TEXT_BRIGHT);
        convFromBase.setBackground(PANEL_MID);
        convFromBase.setBorder(new DoubleRuleBorder(NAVY_FAINT, VOID));
        convFromBase.addActionListener(e -> doConvert());

        JPanel baseSel = new JPanel(new BorderLayout(6, 0));
        baseSel.setOpaque(false);
        baseSel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        baseSel.add(fldLbl("SOURCE  BASE"), BorderLayout.NORTH);
        baseSel.add(convFromBase, BorderLayout.CENTER);
        inputSec.add(baseSel, BorderLayout.SOUTH);

        card.add(inputSec, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        gridPanel.setOpaque(false);
        convOutputs = new JLabel[6];
        for (int i = 0; i < 6; i++) {
            final int idx = i;
            JPanel cell = new JPanel(new BorderLayout(0, 3)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(PANEL_DARK);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                    g2.setColor(NAVY_FAINT);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 5, 5);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            cell.setOpaque(false);
            cell.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel lbl = new JLabel(CONV_ROW_LABELS[i]);
            lbl.setFont(F_TINY); lbl.setForeground(TEXT_GHOST);

            JLabel val = new JLabel("—");
            val.setFont(new Font("Courier New", Font.BOLD, 14));
            val.setForeground(TEXT_BRIGHT);
            convOutputs[i] = val;

            cell.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    String v = convOutputs[idx].getText();
                    if (!"—".equals(v) && !"INVALID".equals(v)) {
                        try {
                            java.awt.datatransfer.StringSelection sel = new java.awt.datatransfer.StringSelection(v);
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
                            setStatus("COPIED:  " + v, CYAN_PH);
                        } catch (Exception ignored) {}
                    }
                }
            });

            cell.add(lbl, BorderLayout.NORTH);
            cell.add(val, BorderLayout.CENTER);
            gridPanel.add(cell);
        }

        JPanel centerSec = new JPanel(new BorderLayout(0, 8));
        centerSec.setOpaque(false);
        centerSec.add(fldLbl("CONVERSIONS  (click to copy)"), BorderLayout.NORTH);
        centerSec.add(gridPanel, BorderLayout.CENTER);

        asciiLbl = new JLabel("Enter a decimal value (0–127) to see ASCII character.");
        asciiLbl.setFont(F_SMALL); asciiLbl.setForeground(TEXT_MID);

        JPanel asciiSec = new JPanel(new BorderLayout(0, 4));
        asciiSec.setOpaque(false);
        asciiSec.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        asciiSec.add(fldLbl("ASCII  /  CHAR  LOOKUP"), BorderLayout.NORTH);
        asciiSec.add(asciiLbl, BorderLayout.CENTER);

        JPanel southSec = new JPanel(new BorderLayout(0, 0));
        southSec.setOpaque(false);
        southSec.add(centerSec, BorderLayout.CENTER);
        southSec.add(asciiSec,  BorderLayout.SOUTH);

        card.add(southSec, BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private void doConvert() {
        String raw = convInput.getText().trim().toUpperCase();
        int fromBase = CONV_BASE_VALUES[convFromBase.getSelectedIndex()];

        if (raw.isEmpty()) {
            for (JLabel l : convOutputs) l.setText("—");
            asciiLbl.setText("Enter a decimal value (0–127) to see ASCII character.");
            return;
        }

        long n;
        try { n = Long.parseLong(raw, fromBase); }
        catch (NumberFormatException ex) {
            for (JLabel l : convOutputs) l.setText("INVALID");
            asciiLbl.setText("Invalid input for selected base.");
            return;
        }

        long abs = n < 0 ? -n : n;
        String pfx = n < 0 ? "-" : "";
        String[] vals = {
            pfx + Long.toBinaryString(abs),
            pfx + Long.toOctalString(abs),
            String.valueOf(n),
            (pfx + Long.toHexString(abs)).toUpperCase(),
            pfx + Long.toString(abs, 32).toUpperCase(),
            pfx + Long.toString(abs, 36).toUpperCase()
        };
        for (int i = 0; i < convOutputs.length; i++) convOutputs[i].setText(vals[i]);

        if (n >= 0 && n <= 127) {
            char ch = (char) n;
            boolean ctrl = n < 32 || n == 127;
            asciiLbl.setText("ASCII " + n + " = \"" + (ctrl ? "(control char)" : String.valueOf(ch)) +
                "\"  (" + (ctrl ? "control" : "printable") + ")");
        } else {
            asciiLbl.setText("Value out of standard ASCII range (0–127).");
        }
    }

    private JPanel buildSciPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        PhosphorPanel card = new PhosphorPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
            new DoubleRuleBorder(NAVY_DIM, NAVY_FAINT),
            BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JPanel topSec = new JPanel(new BorderLayout(0, 5));
        topSec.setOpaque(false);
        topSec.add(fldLbl("INPUT  VALUE"), BorderLayout.NORTH);
        sciInput = termField();
        sciInput.setFont(new Font("Courier New", Font.BOLD, 18));
        topSec.add(sciInput, BorderLayout.CENTER);

        JPanel resSec = new JPanel(new BorderLayout(0, 5));
        resSec.setOpaque(false);
        resSec.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        resSec.add(fldLbl("RESULT"), BorderLayout.NORTH);

        sciResultLbl = new JLabel("—", SwingConstants.LEFT);
        sciResultLbl.setFont(new Font("Courier New", Font.BOLD, 16));
        sciResultLbl.setForeground(NAVY_GLOW);
        sciResultLbl.setBorder(BorderFactory.createCompoundBorder(
            new DoubleRuleBorder(NAVY_FAINT, VOID),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        sciResultLbl.setPreferredSize(new Dimension(0, 44));
        resSec.add(sciResultLbl, BorderLayout.CENTER);

        sciPropsLbl = new JLabel(" ");
        sciPropsLbl.setFont(F_TINY);
        sciPropsLbl.setForeground(TEXT_MID);
        resSec.add(sciPropsLbl, BorderLayout.SOUTH);

        JPanel topAll = new JPanel(new BorderLayout(0, 0));
        topAll.setOpaque(false);
        topAll.add(topSec, BorderLayout.NORTH);
        topAll.add(resSec, BorderLayout.CENTER);
        card.add(topAll, BorderLayout.NORTH);

        String[][] sciOps = {
            {"√ SQRT","sqrt"},{"x² SQ","sq"},{"x³ CUBE","cube"},{"1/x INV","inv"},
            {"LOG₂","log2"},{"LOG₁₀","log10"},{"LN","ln"},{"eˣ EXP","exp"},
            {"|x| ABS","abs"},{"± NEG","neg"},{"n! FACT","fact"},{"POPCOUNT","popcount"},
            {"⌊x⌋ FLOOR","floor"},{"⌈x⌉ CEIL","ceil"},{"SIN (deg)","sin"},{"COS (deg)","cos"},
            {"TAN (deg)","tan"},{"π  PI","pi"},{"e EULER","euler"},{"CLZ","clz"}
        };

        JPanel grid = new JPanel(new GridLayout(5, 4, 5, 5));
        grid.setOpaque(false);
        for (String[] op : sciOps) {
            final String opId = op[1];
            JButton btn = new JButton(op[0]) {
                private float hover = 0f;
                {
                    addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) { animHover(true);  }
                        public void mouseExited(MouseEvent e)  { animHover(false); }
                        void animHover(boolean in) {
                            Timer t = new Timer(16, ev -> {
                                hover = in ? Math.min(hover+0.12f,1f) : Math.max(hover-0.12f,0f);
                                repaint();
                                if ((in&&hover>=1f)||(!in&&hover<=0f)) ((Timer)ev.getSource()).stop();
                            });
                            t.start();
                        }
                    });
                }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color fill = new Color(
                        (int)(PANEL_MID.getRed()  +(NAVY_GLOW.getRed()  -PANEL_MID.getRed())  *hover*0.25f),
                        (int)(PANEL_MID.getGreen()+(NAVY_GLOW.getGreen()-PANEL_MID.getGreen())*hover*0.25f),
                        (int)(PANEL_MID.getBlue() +(NAVY_GLOW.getBlue() -PANEL_MID.getBlue()) *hover*0.25f)
                    );
                    g2.setColor(fill); g2.fillRoundRect(0,0,getWidth(),getHeight(),4,4);
                    g2.setColor(new Color(NAVY_DIM.getRed(),NAVY_DIM.getGreen(),NAVY_DIM.getBlue(),(int)(80+hover*120)));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,4,4);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setFont(getFont());
                    g2.setColor(new Color(TEXT_MID.getRed(),TEXT_MID.getGreen(),TEXT_MID.getBlue(),(int)(180+hover*75)));
                    g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                    g2.dispose();
                }
            };
            btn.setFont(new Font("Courier New", Font.BOLD, 10));
            btn.setOpaque(false); btn.setContentAreaFilled(false);
            btn.setBorderPainted(false); btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(0, 36));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> doSciOp(opId));
            grid.add(btn);
        }

        JPanel gridSec = new JPanel(new BorderLayout(0, 6));
        gridSec.setOpaque(false);
        gridSec.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        gridSec.add(fldLbl("FUNCTIONS"), BorderLayout.NORTH);
        gridSec.add(grid, BorderLayout.CENTER);
        card.add(gridSec, BorderLayout.CENTER);

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private void doSciOp(String op) {
        String raw = sciInput.getText().trim();
        double n;

        if ("pi".equals(op))    { sciResultLbl.setText("π = 3.14159265358979323846..."); sciPropsLbl.setText(" "); return; }
        if ("euler".equals(op)) { sciResultLbl.setText("e = 2.71828182845904523536..."); sciPropsLbl.setText(" "); return; }

        try { n = Double.parseDouble(raw); }
        catch (NumberFormatException ex) { sciResultLbl.setText("ERROR:  INVALID INPUT"); sciPropsLbl.setText(" "); return; }

        String out;
        switch (op) {
            case "sqrt":    out = n < 0 ? "ERROR: IMAGINARY" : String.format("%.8f", Math.sqrt(n)); break;
            case "sq":      out = String.format("%.4f", n * n); break;
            case "cube":    out = String.format("%.4f", n * n * n); break;
            case "inv":     out = n == 0 ? "ERROR: DIV/0" : String.format("%.10f", 1.0/n); break;
            case "log2":    out = n <= 0 ? "ERROR: DOMAIN" : String.format("%.8f", Math.log(n)/Math.log(2)); break;
            case "log10":   out = n <= 0 ? "ERROR: DOMAIN" : String.format("%.8f", Math.log10(n)); break;
            case "ln":      out = n <= 0 ? "ERROR: DOMAIN" : String.format("%.8f", Math.log(n)); break;
            case "exp":     out = String.format("%.6f", Math.exp(n)); break;
            case "abs":     out = String.valueOf(Math.abs(n)); break;
            case "neg":     out = String.valueOf(-n); break;
            case "fact": {
                long ni = (long) n;
                if (n < 0 || n != ni) { out = "ERROR: NON-NEG INT ONLY"; break; }
                long f = 1; for (long i = 2; i <= ni; i++) f *= i;
                out = f > 1_000_000_000_000_000L ? String.format("%.6e",(double)f) : String.valueOf(f);
                break;
            }
            case "popcount": {
                int iv = (int) n;
                out = Integer.bitCount(iv) + " bits set in " + iv; break;
            }
            case "floor":   out = String.valueOf((long) Math.floor(n)); break;
            case "ceil":    out = String.valueOf((long) Math.ceil(n));  break;
            case "sin":     out = String.format("%.8f (deg)", Math.sin(Math.toRadians(n))); break;
            case "cos":     out = String.format("%.8f (deg)", Math.cos(Math.toRadians(n))); break;
            case "tan":     out = String.format("%.8f (deg)", Math.tan(Math.toRadians(n))); break;
            case "clz": {
                int iv = (int) n;
                out = "CLZ(" + iv + ") = " + Integer.numberOfLeadingZeros(iv); break;
            }
            default: out = "?";
        }
        sciResultLbl.setText(op.toUpperCase() + " (" + (long)n + ") = " + out);

        if (n == Math.floor(n) && !Double.isInfinite(n)) {
            long ni = (long) n;
            boolean isPrime = ni > 1 && java.math.BigInteger.valueOf(ni).isProbablePrime(20);
            int bitsNeeded = ni <= 0 ? 1 : (64 - Long.numberOfLeadingZeros(ni));
            sciPropsLbl.setText("PRIME: " + (isPrime ? "YES" : "NO") +
                "  |  EVEN: " + (ni % 2 == 0 ? "YES" : "NO") +
                "  |  BITS NEEDED: " + bitsNeeded);
        } else {
            sciPropsLbl.setText(" ");
        }
    }

    private JPanel buildHistoryPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(fldLbl("CALCULATION  LOG  (click entry to reload)"), BorderLayout.WEST);

        JButton clearBtn = makeBtn("[ CLEAR ALL ]", RED_WARN, VOID);
        clearBtn.setPreferredSize(new Dimension(140, 28));
        clearBtn.addActionListener(e -> { history.clear(); refreshHistory(); });
        topBar.add(clearBtn, BorderLayout.EAST);
        p.add(topBar, BorderLayout.NORTH);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(F_SMALL);
        historyArea.setForeground(TEXT_BRIGHT);
        historyArea.setBackground(PANEL_DARK);
        historyArea.setCaretColor(NAVY_GLOW);
        historyArea.setLineWrap(true);
        historyArea.setText("  NO CALCULATIONS LOGGED YET.");
        historyArea.setForeground(TEXT_GHOST);

        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBackground(PANEL_DARK);
        scroll.getViewport().setBackground(PANEL_DARK);
        scroll.setBorder(new DoubleRuleBorder(NAVY_FAINT, VOID));
        scroll.getVerticalScrollBar().setBackground(PANEL_MID);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private void addToHistory(String expr) {
        String time = java.time.LocalTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        history.add(0, "[" + time + "]  " + expr);
        if (history.size() > 100) history.remove(history.size() - 1);
        refreshHistory();
    }

    private void refreshHistory() {
        if (history.isEmpty()) {
            historyArea.setForeground(TEXT_GHOST);
            historyArea.setText("  NO CALCULATIONS LOGGED YET.");
        } else {
            historyArea.setForeground(TEXT_BRIGHT);
            historyArea.setText(String.join("\n", history));
            historyArea.setCaretPosition(0);
        }
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(PANEL_DARK); g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(NAVY_FAINT); g.fillRect(0, 0, getWidth(), 1);
                super.paintComponent(g);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
        bar.setPreferredSize(new Dimension(0, 28));

        statusMsg = new JLabel("SYSTEM  READY.  AWAITING  INPUT.");
        statusMsg.setFont(F_SMALL); statusMsg.setForeground(TEXT_MID);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        calcCountLbl = new JLabel("OPS: 0");
        calcCountLbl.setFont(F_SMALL); calcCountLbl.setForeground(NAVY_FAINT);

        clockLbl = new JLabel();
        clockLbl.setFont(F_SMALL); clockLbl.setForeground(TEXT_GHOST);

        right.add(calcCountLbl);
        right.add(clockLbl);

        bar.add(statusMsg, BorderLayout.WEST);
        bar.add(right,     BorderLayout.EAST);
        return bar;
    }

    private void updateClock() {
        clockLbl.setText(java.time.LocalTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private void calculate() {
        try {
            int b1 = getBaseValue(base1);
            int b2 = getBaseValue(base2);
            String raw1 = input1.getText().trim().toUpperCase();
            String raw2 = input2.getText().trim().toUpperCase();

            if (raw1.isEmpty() || raw2.isEmpty()) {
                setStatus("ERROR:  OPERAND  FIELD  IS  EMPTY.", RED_WARN); return;
            }

            long n1 = Long.parseLong(raw1, b1);
            long n2 = Long.parseLong(raw2, b2);
            String op = (String) opCombo.getSelectedItem();

            long result;
            switch (op) {
                case "+":  result = n1 + n2; break;
                case "-":  result = n1 - n2; break;
                case "×":  result = n1 * n2; break;
                case "÷":
                    if (n2 == 0) { setStatus("ERROR:  DIVISION  BY  ZERO.", RED_WARN); return; }
                    result = n1 / n2; break;
                case "%":
                    if (n2 == 0) { setStatus("ERROR:  MODULO  BY  ZERO.", RED_WARN); return; }
                    result = n1 % n2; break;
                case "&":  result = n1 & n2; break;
                case "|":  result = n1 | n2; break;
                case "^":  result = n1 ^ n2; break;
                case "<<": result = n1 << n2; break;
                case ">>": result = n1 >> n2; break;
                default:   result = 0;
            }

            updateResultsFromValue(result);
            String expr = raw1 + "(b"+b1+") " + op + " " + raw2 + "(b"+b2+") = " + result;
            calcCount++;
            calcCountLbl.setText("OPS: " + calcCount);
            setStatus("CALC #" + calcCount + "  OK.  " + expr, CYAN_PH);
            addToHistory(expr);

        } catch (NumberFormatException ex) {
            setStatus("ERROR:  INVALID  INPUT  FOR  SELECTED  BASE.", RED_WARN);
        }
    }

    private void updateResultsFromValue(long result) {
        boolean neg = result < 0;
        long abs = neg ? -result : result;
        String pfx = neg ? "-" : "";

        String dec = String.valueOf(result);
        String bin = pfx + Long.toBinaryString(abs);
        String oct = pfx + Long.toOctalString(abs);
        String hex = (pfx + Long.toHexString(abs)).toUpperCase();

        decVal.setText(dec);
        binVal.setText(bin.length() > 28 ? bin.substring(0, 25) + "..." : bin);
        octVal.setText(oct);
        hexVal.setText(hex);
        flashResults();
        updateBits(result);
    }

    private void clearAll() {
        input1.setText(""); input2.setText("");
        decVal.setText("--"); binVal.setText("--"); octVal.setText("--"); hexVal.setText("--");
        clearBits();
        setStatus("REGISTERS  CLEARED.  READY.", NAVY_DIM);
    }

    private void swapOperands() {
        String v1 = input1.getText(), v2 = input2.getText();
        int b1 = base1.getSelectedIndex(), b2 = base2.getSelectedIndex();
        input1.setText(v2); input2.setText(v1);
        base1.setSelectedIndex(b2); base2.setSelectedIndex(b1);
    }

    private void negateA() {
        String v = input1.getText().trim();
        if (v.isEmpty()) return;
        input1.setText(v.startsWith("-") ? v.substring(1) : "-" + v);
    }

    private int getBaseValue(JComboBox<String> combo) {
        return BASE_VALUES[combo.getSelectedIndex()];
    }

    private void setStatus(String msg, Color color) {
        statusMsg.setText(msg);
        statusMsg.setForeground(color);
        Timer t = new Timer(5000, e -> {
            statusMsg.setText("SYSTEM  READY.");
            statusMsg.setForeground(TEXT_MID);
        });
        t.setRepeats(false); t.start();
    }

    private void flashResults() {
        JLabel[] lbls = {decVal, binVal, octVal, hexVal};
        for (JLabel l : lbls) l.setForeground(NAVY_GLOW);
        Timer t = new Timer(400, e -> { for (JLabel l : lbls) l.setForeground(TEXT_BRIGHT); });
        t.setRepeats(false); t.start();
    }

    private JTextField termField() {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(VOID); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setFont(F_INPUT);
        tf.setForeground(NAVY_GLOW);
        tf.setCaretColor(NAVY_GLOW);
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new DoubleRuleBorder(NAVY_DIM, NAVY_FAINT),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        tf.setPreferredSize(new Dimension(0, 50));
        tf.addActionListener(e -> calculate());
        return tf;
    }

    private JComboBox<String> baseCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Courier New", Font.BOLD, 12));
        cb.setForeground(TEXT_BRIGHT); cb.setBackground(PANEL_MID);
        cb.setBorder(new DoubleRuleBorder(NAVY_FAINT, VOID));
        cb.setPreferredSize(new Dimension(0, 38));
        styleCombo(cb);
        return cb;
    }

    private JComboBox<String> opComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Courier New", Font.BOLD, 22));
        cb.setForeground(NAVY_GLOW); cb.setBackground(PANEL_MID);
        cb.setBorder(new DoubleRuleBorder(NAVY_DIM, VOID));
        cb.setPreferredSize(new Dimension(62, 50));
        styleCombo(cb);
        return cb;
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                setBackground(sel ? PANEL_LIGHT : PANEL_MID);
                setForeground(sel ? NAVY_GLOW : TEXT_BRIGHT);
                setFont(cb.getFont());
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }

    private JButton makeBtn(String text, Color accent, Color bg) {
        JButton btn = new JButton(text) {
            private float hover = 0f;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { anim(true);  }
                    public void mouseExited(MouseEvent e)  { anim(false); }
                    void anim(boolean in) {
                        Timer t = new Timer(16, ev -> {
                            hover = in ? Math.min(hover+0.10f,1f) : Math.max(hover-0.10f,0f);
                            repaint();
                            if ((in&&hover>=1f)||(!in&&hover<=0f)) ((Timer)ev.getSource()).stop();
                        });
                        t.start();
                    }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = new Color(
                    (int)(VOID.getRed()  +(accent.getRed()  -VOID.getRed())  *hover*0.28f),
                    (int)(VOID.getGreen()+(accent.getGreen()-VOID.getGreen())*hover*0.28f),
                    (int)(VOID.getBlue() +(accent.getBlue() -VOID.getBlue()) *hover*0.28f)
                );
                g2.setColor(fill); g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),(int)(120+hover*135)));
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,6,6);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),(int)(180+hover*75)));
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setFont(F_BTN);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 40));
        return btn;
    }

    private JLabel colHdr(String t) {
        JLabel l = new JLabel(t);
        l.setFont(F_LABEL); l.setForeground(TEXT_GHOST);
        l.setBorder(BorderFactory.createEmptyBorder(0,0,2,0));
        return l;
    }

    private JLabel fldLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(F_LABEL); l.setForeground(NAVY_FAINT);
        return l;
    }

    private JPanel blinkDot(Color color, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel() {
            boolean on = true;
            {
                setPreferredSize(new Dimension(9, 9));
                setOpaque(false);
                new Timer(800, e -> { on = !on; repaint(); }).start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(on ? color : color.darker().darker());
                g2.fillOval(0, 0, 8, 8);
                g2.dispose();
            }
        };
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_LABEL);
        lbl.setForeground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
        p.add(dot); p.add(lbl);
        return p;
    }

    // ─── CUSTOM PAINT CLASSES ────────────────────────────────────────────────────
    static class CRTPanel extends JPanel {
        private BufferedImage scanlines;
        CRTPanel() { setOpaque(true); }

        private BufferedImage getScans(int w, int h) {
            if (scanlines == null || scanlines.getWidth() != w || scanlines.getHeight() != h) {
                scanlines = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = scanlines.createGraphics();
                g2.setColor(new Color(0, 0, 0, 28));
                for (int y = 0; y < h; y += 3) g2.fillRect(0, y, w, 1);
                g2.dispose();
            }
            return scanlines;
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(VOID); g2.fillRect(0, 0, getWidth(), getHeight());
            RadialGradientPaint rp = new RadialGradientPaint(
                getWidth()/2f, getHeight()/2f, Math.max(getWidth(), getHeight()) * 0.65f,
                new float[]{0f, 1f},
                new Color[]{new Color(30, 22, 3, 30), new Color(0,0,0,100)}
            );
            g2.setPaint(rp); g2.fillRect(0, 0, getWidth(), getHeight());
            g2.drawImage(getScans(getWidth(), getHeight()), 0, 0, null);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class PhosphorPanel extends JPanel {
        PhosphorPanel() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(PANEL_MID);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            GradientPaint gp = new GradientPaint(0, 0, new Color(100,180,255,14), 0, 36, new Color(0,0,0,0));
            g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth(), 36, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class DoubleRuleBorder extends AbstractBorder {
        private final Color outer, inner;
        DoubleRuleBorder(Color outer, Color inner) { this.outer = outer; this.inner = inner; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(outer); g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w-1, h-1, 8, 8);
            g2.setColor(inner); g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x+3, y+3, w-7, h-7, 5, 5);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4,4,4,4); }
    }

    // ─── MAIN ────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new BaseCalculatorDashboard().setVisible(true));
    }
}
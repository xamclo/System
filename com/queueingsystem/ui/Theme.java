package com.queueingsystem.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Theme {
    public static final Color BG = new Color(11, 31, 26);          // #0B1F1A
    public static final Color BG_2 = new Color(15, 42, 36);        // gradient pair
    public static final Color TOP_BAR = new Color(12, 46, 38);

    public static final Color CARD = new Color(19, 46, 40, 235);
    public static final Color CARD_BORDER = new Color(255, 255, 255, 18);
    public static final Color CARD_STRONG = new Color(26, 56, 49, 245);

    public static final Color INPUT_BG = new Color(11, 31, 26);
    public static final Color INPUT_BORDER = new Color(31, 61, 54);
    public static final Color INPUT_FOCUS = new Color(29, 185, 84);

    public static final Color BUTTON = new Color(21, 143, 67);
    public static final Color BUTTON_HOVER = new Color(29, 185, 84);

    public static final Color TEXT = new Color(230, 244, 241);      // #E6F4F1
    public static final Color TEXT_SOFT = new Color(160, 181, 176); // #A0B5B0
    public static final Color MUTED = TEXT_SOFT;
    public static final Color GOLD = new Color(194, 166, 62);

    public static Font font(int size, boolean bold) {
        return new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size);
    }

    public static JLabel label(String text, int size, boolean bold, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font(size, bold));
        label.setForeground(color);
        return label;
    }

    public static JTextField glassTextField(int fontSize) {
        JTextField field = new JTextField();
        field.setFont(font(fontSize, false));
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setOpaque(false);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, INPUT_BORDER),
                new EmptyBorder(13, 16, 13, 16)
        ));
        return field;
    }

    public static JPasswordField glassPasswordField(int fontSize) {
        JPasswordField field = new JPasswordField();
        field.setFont(font(fontSize, false));
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setOpaque(false);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, INPUT_BORDER),
                new EmptyBorder(13, 16, 13, 16)
        ));
        return field;
    }

    // Backward compatibility for older panels
    public static JTextField textField(int size) {
        return glassTextField(size);
    }

    public static JPasswordField passwordField(int size) {
        return glassPasswordField(size);
    }

    public static JButton button(String text, int fontSize) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(TEXT);
        button.setFont(font(fontSize, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setUI(new RoundedButtonUI(BUTTON, 16));
        return button;
    }

    public static JButton subtleButton(String text, int fontSize) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(TEXT);
        button.setFont(font(fontSize, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setUI(new RoundedButtonUI(new Color(18, 92, 52), 16));
        return button;
    }

    public static JButton iconButton(String text, int fontSize) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(new Color(230, 244, 241, 210));
        button.setFont(font(fontSize, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setUI(new RoundedButtonUI(new Color(255, 255, 255, 16), 14));
        return button;
    }

    public static JComboBox<String> comboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(font(18, false));
        box.setForeground(TEXT);
        box.setBackground(new Color(255, 255, 255, 10));
        box.setFocusable(false);
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
            ) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );
                lbl.setFont(font(18, false));
                lbl.setForeground(TEXT);
                lbl.setOpaque(true);
                lbl.setBorder(new EmptyBorder(8, 12, 8, 12));

                if (index == -1) {
                    lbl.setBackground(new Color(0, 0, 0, 0));
                } else {
                    lbl.setBackground(isSelected ? new Color(12, 92, 44) : new Color(8, 35, 20));
                }

                return lbl;
            }
        });

        box.setUI(new BasicComboBoxUI() {
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                // remove default background paint
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▾");
                btn.setForeground(TEXT);
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setFont(font(16, true));
                return btn;
            }
        });

        return box;
    }

    public static ImageIcon scaledIcon(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(path);
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static class RoundedPanel extends JPanel {
        private final Color bg;
        private final int radius;

        public RoundedPanel(Color bg, int radius) {
            this.bg = bg;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 95));
            g2.fillRoundRect(0, 10, getWidth(), getHeight() - 4, radius, radius);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight() - 8, radius, radius);

            g2.setColor(CARD_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 9, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 50));
            g2.drawRoundRect(x, y + 1, width - 1, height - 2, radius, radius);

            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 2, radius, radius);

            g2.dispose();
        }
    }

    public static class RoundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
        private final Color bg;
        private final int radius;

        public RoundedButtonUI(Color bg, int radius) {
            this.bg = bg;
            this.radius = radius;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 65));
            g2.fillRoundRect(0, 8, c.getWidth(), c.getHeight() - 2, radius, radius);

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0, 0, c.getWidth(), c.getHeight() - 8, radius, radius));

            FontMetrics fm = g2.getFontMetrics(b.getFont());
            int textX = (c.getWidth() - fm.stringWidth(b.getText())) / 2;
            int textY = ((c.getHeight() - 8 - fm.getHeight()) / 2) + fm.getAscent();

            g2.setColor(b.getForeground());
            g2.setFont(b.getFont());
            g2.drawString(b.getText(), textX, textY);
            g2.dispose();
        }
    }
}
package com.queueingsystem.ui;

import com.queueingsystem.enumtypes.OfficeType;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class AdminLoginPanel extends JPanel {
    private final MainFrame frame;

    private OfficeType officeType;
    private final JLabel titleLabel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public AdminLoginPanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        topBar.setBackground(Theme.TOP_BAR);
        topBar.setBorder(new EmptyBorder(10, 24, 10, 24));

        JLabel logo = new JLabel(Theme.scaledIcon("src/com/queueingsystem/ui/assets/logo.png", 46, 46));
        JLabel deptPrefix = Theme.label("DEPARTMENT:", 20, false, Theme.TEXT_SOFT);
        titleLabel = Theme.label("FINANCE", 22, true, Theme.GOLD);

        topBar.add(logo);
        topBar.add(deptPrefix);
        topBar.add(titleLabel);

        add(topBar, BorderLayout.NORTH);

        GradientPanel center = new GradientPanel();
        center.setLayout(new BorderLayout());
        center.setBorder(new EmptyBorder(12, 18, 18, 18));

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backPanel.setOpaque(false);

        JButton backBtn = Theme.iconButton("←", 18);
        backBtn.setPreferredSize(new Dimension(44, 44));
        backPanel.add(backBtn);

        center.add(backPanel, BorderLayout.NORTH);

        JPanel contentCenter = new JPanel(new GridBagLayout());
        contentCenter.setOpaque(false);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

        Theme.RoundedPanel card = new Theme.RoundedPanel(Theme.CARD, 22);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(46, 70, 46, 70));
        card.setPreferredSize(new Dimension(860, 460));
        card.setMaximumSize(new Dimension(860, 460));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel formTitle = Theme.label("STAFF LOG IN", 32, true, Theme.TEXT);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = Theme.label("Username", 21, false, Theme.TEXT_SOFT);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = Theme.glassTextField(19);
        ((AbstractDocument) usernameField.getDocument()).setDocumentFilter(new LimitFilter(15));
        usernameField.setMaximumSize(new Dimension(650, 58));
        usernameField.setPreferredSize(new Dimension(650, 58));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = Theme.label("Password", 21, false, Theme.TEXT_SOFT);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField();
        ((AbstractDocument) passwordField.getDocument()).setDocumentFilter(new LimitFilter(15));
        passwordField.setFont(Theme.font(19, false));
        passwordField.setForeground(Theme.TEXT);
        passwordField.setCaretColor(Theme.TEXT);
        passwordField.setOpaque(false);
        passwordField.setBackground(new Color(0, 0, 0, 0));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedBorder(16, Theme.INPUT_BORDER),
                new EmptyBorder(13, 16, 13, 16)
        ));
        passwordField.setMaximumSize(new Dimension(650, 58));
        passwordField.setPreferredSize(new Dimension(650, 58));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        installFocusStyle(usernameField);
        installFocusStyle(passwordField);

        card.add(Box.createVerticalGlue());
        card.add(formTitle);
        card.add(Box.createRigidArea(new Dimension(0, 28)));
        card.add(usernameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(usernameField);
        card.add(Box.createRigidArea(new Dimension(0, 26)));
        card.add(passwordLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(passwordField);
        card.add(Box.createVerticalGlue());

        JButton loginBtn = createGlassButton("Log In");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setPreferredSize(new Dimension(310, 62));
        loginBtn.setMaximumSize(new Dimension(310, 62));

        stack.add(card);
        stack.add(Box.createRigidArea(new Dimension(0, 18)));
        stack.add(loginBtn);

        contentCenter.add(stack);
        center.add(contentCenter, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        backBtn.addActionListener(e -> frame.showHome());

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter your username and password.",
                        "Missing Fields",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            boolean success = frame.loginAdmin(username, password, officeType);

            if (!success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid login credentials or wrong office selected.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    public void setOfficeType(OfficeType officeType) {
        this.officeType = officeType;
        titleLabel.setText(officeType.name());
        usernameField.setText("");
        passwordField.setText("");
    }

    private void installFocusStyle(JTextField field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new Theme.RoundedBorder(16, Theme.INPUT_FOCUS),
                        new EmptyBorder(13, 16, 13, 16)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new Theme.RoundedBorder(16, Theme.INPUT_BORDER),
                        new EmptyBorder(13, 16, 13, 16)
                ));
            }
        });
    }

    private JButton createGlassButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(Theme.TEXT);
        button.setFont(Theme.font(22, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setUI(new Theme.RoundedButtonUI(new Color(255, 255, 255, 20), 18));
        return button;
    }

    private static class LimitFilter extends DocumentFilter {
        private final int maxLength;

        public LimitFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;

            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String candidate = current.substring(0, offset) + text + current.substring(offset + length);

            // letters + numbers only
            candidate = candidate.replaceAll("[^a-zA-Z0-9]", "");

            if (candidate.length() <= maxLength) {
                fb.replace(0, fb.getDocument().getLength(), candidate, attrs);
            }
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(
                    0, 0, Theme.BG,
                    getWidth(), getHeight(), Theme.BG_2
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();
        }
    }
}
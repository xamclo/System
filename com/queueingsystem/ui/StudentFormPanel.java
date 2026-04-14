package com.queueingsystem.ui;

import com.queueingsystem.enumtypes.OfficeType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class StudentFormPanel extends JPanel {
    private OfficeType officeType;
    private final JLabel titleLabel;
    private final JTextField nameField;
    private final JTextField studentIdField;

    public StudentFormPanel(MainFrame frame) {
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

        JLabel formTitle = Theme.label("STUDENT FORM", 32, true, Theme.TEXT);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = Theme.label("Name", 21, false, Theme.TEXT_SOFT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = Theme.glassTextField(19);
        nameField.setMaximumSize(new Dimension(650, 58));
        nameField.setPreferredSize(new Dimension(650, 58));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = Theme.label("Student Number", 21, false, Theme.TEXT_SOFT);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        studentIdField = Theme.glassTextField(19);
        studentIdField.setMaximumSize(new Dimension(650, 58));
        studentIdField.setPreferredSize(new Dimension(650, 58));
        studentIdField.setAlignmentX(Component.CENTER_ALIGNMENT);

        installFocusStyle(nameField);
        installFocusStyle(studentIdField);

        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new NameFilter(40));
        ((AbstractDocument) studentIdField.getDocument()).setDocumentFilter(new StudentNumberFilter());

        card.add(Box.createVerticalGlue());
        card.add(formTitle);
        card.add(Box.createRigidArea(new Dimension(0, 28)));
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(nameField);
        card.add(Box.createRigidArea(new Dimension(0, 26)));
        card.add(idLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(studentIdField);
        card.add(Box.createVerticalGlue());

        JButton getNumberBtn = createGlassButton("Get a Number");
        getNumberBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        getNumberBtn.setPreferredSize(new Dimension(310, 62));
        getNumberBtn.setMaximumSize(new Dimension(310, 62));

        stack.add(card);
        stack.add(Box.createRigidArea(new Dimension(0, 18)));
        stack.add(getNumberBtn);

        contentCenter.add(stack);
        center.add(contentCenter, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        backBtn.addActionListener(e -> frame.showHome());

        getNumberBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String studentId = studentIdField.getText().trim();

            if (name.isEmpty() || studentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please complete all fields.");
                return;
            }

            if (!isValidFullName(name)) {
                JOptionPane.showMessageDialog(
                        this,
                        "The name you entered is not valid.\nPlease enter your full name using letters only (e.g., Alex Vause).",
                        "Invalid Name",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!isValidStudentId(studentId)) {
                JOptionPane.showMessageDialog(
                        this,
                        "The Student ID you entered is not valid.\nPlease enter a valid ID (e.g., 03-2232-032671)",
                        "Invalid Student ID",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            frame.submitStudent(name, studentId, officeType);
            nameField.setText("");
            studentIdField.setText("");
        });
    }

    public void setOfficeType(OfficeType officeType) {
        this.officeType = officeType;
        titleLabel.setText(officeType.name());
    }

    private boolean isValidFullName(String name) {
        return name.matches("[A-Za-z]+(?: [A-Za-z]+)+");
    }

    private boolean isValidStudentId(String studentId) {
        return studentId.matches("\\d{2}-\\d{2}-\\d{4}-\\d{6}")
                || studentId.matches("\\d{2}-\\d{4}-\\d{6}");
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

    private static class NameFilter extends DocumentFilter {
        private final int maxLength;

        public NameFilter(int maxLength) {
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
            candidate = candidate.replaceAll("[^a-zA-Z\\s]", "");

            if (candidate.length() <= maxLength) {
                fb.replace(0, fb.getDocument().getLength(), candidate, attrs);
            }
        }
    }

    private static class StudentNumberFilter extends DocumentFilter {
        private static final int MAX_LENGTH = 17;

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;

            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String candidate = current.substring(0, offset) + text + current.substring(offset + length);
            candidate = candidate.replaceAll("[^0-9-]", "");

            if (candidate.length() <= MAX_LENGTH) {
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
package com.queueingsystem.ui;

import com.queueingsystem.enumtypes.OfficeType;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomePanel extends JPanel {

    public HomePanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel content = new JPanel(new GridLayout(1, 2, 110, 0));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(1250, 560));

        content.add(buildLeftSection());
        content.add(buildRightSection(frame));

        add(content);
    }

    private JPanel buildLeftSection() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);

        JPanel leftInner = new JPanel();
        leftInner.setOpaque(false);
        leftInner.setLayout(new BoxLayout(leftInner, BoxLayout.Y_AXIS));
        leftInner.setPreferredSize(new Dimension(560, 390));

        JLabel logo = new JLabel(Theme.scaledIcon("src/com/queueingsystem/ui/assets/logo.png", 220, 220));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = Theme.label("PilaLESS SYSTEM", 30, true, Theme.GOLD);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLine1 = Theme.label(
                "No more long lines — join and monitor queues digitally for a",
                15,
                false,
                new Color(255, 255, 255, 205)
        );
        descLine1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLine2 = Theme.label(
                "faster and more convenient experience.",
                15,
                false,
                new Color(255, 255, 255, 205)
        );
        descLine2.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftInner.add(Box.createVerticalGlue());
        leftInner.add(logo);
        leftInner.add(Box.createRigidArea(new Dimension(0, 22)));
        leftInner.add(title);
        leftInner.add(Box.createRigidArea(new Dimension(0, 18)));
        leftInner.add(descLine1);
        leftInner.add(Box.createRigidArea(new Dimension(0, 2)));
        leftInner.add(descLine2);
        leftInner.add(Box.createVerticalGlue());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, -150, 60, 0);

        left.add(leftInner, gbc);

        return left;
    }



    private JPanel buildRightSection(MainFrame frame) {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(70, 20, 70, 20));

        JLabel roleLabel = Theme.label("Please select your role:", 24, true, new Color(255, 255, 255, 220));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> roleBox = createModernRoleBox();
        roleBox.setMaximumSize(new Dimension(450, 48));
        roleBox.setPreferredSize(new Dimension(450, 48));
        roleBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel transactionLabel = Theme.label("Select Your Transaction", 28, true, new Color(255, 255, 255, 220));
        transactionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton financeBtn = createModernButton("Finance Office");
        JButton registrarBtn = createModernButton("Office of the Registrar");

        financeBtn.addActionListener(e -> {
            String role = (String) roleBox.getSelectedItem();
            if ("Admin".equals(role)) {
                frame.showAdminLogin(OfficeType.FINANCE);
            } else {
                frame.showStudentForm(OfficeType.FINANCE);
            }
        });

        registrarBtn.addActionListener(e -> {
            String role = (String) roleBox.getSelectedItem();
            if ("Admin".equals(role)) {
                frame.showAdminLogin(OfficeType.REGISTRAR);
            } else {
                frame.showStudentForm(OfficeType.REGISTRAR);
            }
        });

        right.add(Box.createVerticalGlue());
        right.add(roleLabel);
        right.add(Box.createRigidArea(new Dimension(0, 16)));
        right.add(roleBox);
        right.add(Box.createRigidArea(new Dimension(0, 54)));
        right.add(transactionLabel);
        right.add(Box.createRigidArea(new Dimension(0, 24)));
        right.add(financeBtn);
        right.add(Box.createRigidArea(new Dimension(0, 18)));
        right.add(registrarBtn);
        right.add(Box.createVerticalGlue());

        return right;
    }

    private JComboBox<String> createModernRoleBox() {
        JComboBox<String> box = Theme.comboBox(new String[]{"Student", "Admin"});
        ((JLabel) box.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        box.setSelectedIndex(0);
        box.setFont(Theme.font(19, false));
        box.setForeground(new Color(255, 255, 255, 220));
        box.setOpaque(false);
        box.setBackground(new Color(0, 0, 0, 0));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        return box;
    }

    private JButton createModernButton(String text) {
        JButton button = Theme.button(text, 21);
        button.setMaximumSize(new Dimension(460, 72));
        button.setPreferredSize(new Dimension(460, 72));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        addHoverEffect(button);
        return button;
    }

    private void addHoverEffect(JButton button) {
        final Border originalBorder = button.getBorder();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(255, 255, 255, 235));
                button.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Theme.TEXT);
                button.setBorder(originalBorder);
                button.repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(2, 18, 10),
                0, getHeight(), new Color(7, 52, 25)
        );

        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
    }
}
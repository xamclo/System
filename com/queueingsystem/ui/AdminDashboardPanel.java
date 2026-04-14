package com.queueingsystem.ui;

import com.queueingsystem.enumtypes.QueueState;
import com.queueingsystem.model.AdminAccount;
import com.queueingsystem.model.QueueTicket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardPanel extends JPanel {
    private final MainFrame frame;

    private AdminAccount account;
    private QueueTicket selectedTicket;

    private final JLabel officeValueLabel;
    private final JLabel rightTitleLabel;
    private final JLabel activeNumberLabel;
    private final JLabel activeStudentLabel;
    private final JLabel timerLabel;

    private final JButton serveBtn;
    private final JButton startTimerBtn;
    private final JButton doneBtn;
    private final JButton skipBtn;
    private final JButton logoutBtn;

    private final JPanel queueRowsPanel;
    private final JPanel servingInfoCard;

    private Timer refreshTimer;
    private Timer serveCountdownTimer;
    private int remainingSeconds = 0;

    private boolean manualTimerActive = false;
    private boolean countdownFinished = false;

    private Clip alarmClip;

    public AdminDashboardPanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        topBar.setBackground(Theme.TOP_BAR);
        topBar.setBorder(new EmptyBorder(10, 24, 10, 24));

        JLabel logo = new JLabel(Theme.scaledIcon("src/com/queueingsystem/ui/assets/logo.png", 46, 46));
        JLabel departmentLabel = Theme.label("DEPARTMENT:", 20, false, Theme.TEXT);
        officeValueLabel = Theme.label("FINANCE", 22, true, Theme.GOLD);

        topBar.add(logo);
        topBar.add(departmentLabel);
        topBar.add(officeValueLabel);
        add(topBar, BorderLayout.NORTH);

        GradientPanel content = new GradientPanel();
        content.setLayout(new BorderLayout(18, 0));
        content.setBorder(new EmptyBorder(20, 26, 20, 26));
        add(content, BorderLayout.CENTER);

        Theme.RoundedPanel leftCard = new Theme.RoundedPanel(new Color(255, 255, 255, 4), 24);
        leftCard.setLayout(new BorderLayout());
        leftCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        leftCard.setPreferredSize(new Dimension(980, 540));
        leftCard.setMaximumSize(new Dimension(980, 540));
        leftCard.setMinimumSize(new Dimension(980, 540));

        JPanel header = new JPanel(new GridLayout(1, 2, 18, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 6, 10, 6));

        JLabel queueHeader = Theme.label("QUEUE LIST", 18, true, Theme.TEXT);
        JLabel statusHeader = Theme.label("STATUS", 18, true, Theme.TEXT);
        queueHeader.setHorizontalAlignment(SwingConstants.CENTER);
        statusHeader.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(queueHeader);
        header.add(statusHeader);

        JPanel divider = new JPanel();
        divider.setBackground(new Color(255, 255, 255, 20));
        divider.setPreferredSize(new Dimension(10, 1));

        queueRowsPanel = new JPanel(new GridBagLayout());
        queueRowsPanel.setOpaque(false);
        queueRowsPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(queueRowsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(new Color(0, 0, 0, 0));
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        JPanel leftCenter = new JPanel(new BorderLayout());
        leftCenter.setOpaque(false);
        leftCenter.add(divider, BorderLayout.NORTH);
        leftCenter.add(scrollPane, BorderLayout.CENTER);

        leftCard.add(header, BorderLayout.NORTH);
        leftCard.add(leftCenter, BorderLayout.CENTER);

        JPanel rightCard = new JPanel(new BorderLayout());
        rightCard.setOpaque(false);
        rightCard.setBorder(new EmptyBorder(8, 8, 8, 8));
        rightCard.setPreferredSize(new Dimension(300, 540));

        JPanel rightContent = new JPanel();
        rightContent.setOpaque(false);
        rightContent.setLayout(new BorderLayout());
        rightContent.setBorder(new EmptyBorder(8, 8, 8, 8));

        rightTitleLabel = Theme.label("FINANCE : Window 1", 18, true, Theme.TEXT);
        rightTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel servingChipWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        servingChipWrap.setOpaque(false);

        ShadowlessRoundedPanel servingChip = new ShadowlessRoundedPanel(new Color(255, 255, 255, 12), 14);
        servingChip.setLayout(new GridBagLayout());
        servingChip.setPreferredSize(new Dimension(120, 34));
        servingChip.setMaximumSize(new Dimension(120, 34));
        servingChip.setMinimumSize(new Dimension(120, 34));

        JLabel servingChipLabel = Theme.label("SERVING", 14, true, Theme.TEXT);
        servingChip.add(servingChipLabel);
        servingChipWrap.add(servingChip);

        servingInfoCard = new ShadowlessRoundedPanel(new Color(255, 255, 255, 8), 18);
        servingInfoCard.setLayout(new BoxLayout(servingInfoCard, BoxLayout.Y_AXIS));
        servingInfoCard.setPreferredSize(new Dimension(220, 180));
        servingInfoCard.setMaximumSize(new Dimension(220, 180));
        servingInfoCard.setMinimumSize(new Dimension(220, 180));
        servingInfoCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        servingInfoCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        activeNumberLabel = Theme.label("", 34, true, Theme.GOLD);
        activeNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        activeStudentLabel = Theme.label("", 13, false, Theme.TEXT_SOFT);
        activeStudentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        doneBtn = buildMiniActionButton("DONE");
        skipBtn = buildMiniActionButton("SKIP");

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actionRow.setOpaque(false);
        actionRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionRow.setMaximumSize(new Dimension(190, 40));
        actionRow.add(doneBtn);
        actionRow.add(skipBtn);

        servingInfoCard.add(Box.createVerticalGlue());
        servingInfoCard.add(activeNumberLabel);
        servingInfoCard.add(Box.createRigidArea(new Dimension(0, 8)));
        servingInfoCard.add(activeStudentLabel);
        servingInfoCard.add(Box.createRigidArea(new Dimension(0, 16)));
        servingInfoCard.add(actionRow);
        servingInfoCard.add(Box.createVerticalGlue());

        serveBtn = buildGlassButton("SERVE", 150, 38);
        startTimerBtn = buildGlassButton("SET 1 MIN TIMER", 180, 38);

        logoutBtn = buildTextButton("LOGOUT");
        logoutBtn.setForeground(new Color(255, 255, 255, 150));

        timerLabel = Theme.label("1:00", 20, true, Theme.TEXT);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setVisible(false);

        serveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startTimerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel rightCenterWrap = new JPanel();
        rightCenterWrap.setOpaque(false);
        rightCenterWrap.setLayout(new BoxLayout(rightCenterWrap, BoxLayout.Y_AXIS));
        rightCenterWrap.setBorder(new EmptyBorder(70, 0, 0, 0));

        rightCenterWrap.add(rightTitleLabel);
        rightCenterWrap.add(Box.createRigidArea(new Dimension(0, 14)));
        rightCenterWrap.add(servingChipWrap);
        rightCenterWrap.add(Box.createRigidArea(new Dimension(0, 12)));
        rightCenterWrap.add(servingInfoCard);
        rightCenterWrap.add(Box.createRigidArea(new Dimension(0, 16)));
        rightCenterWrap.add(serveBtn);
        rightCenterWrap.add(Box.createRigidArea(new Dimension(0, 10)));
        rightCenterWrap.add(startTimerBtn);
        rightCenterWrap.add(Box.createRigidArea(new Dimension(0, 10)));
        rightCenterWrap.add(timerLabel);
        rightCenterWrap.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel centeredWrap = new JPanel(new GridBagLayout());
        centeredWrap.setOpaque(false);
        centeredWrap.add(rightCenterWrap);
        centeredWrap.setBorder(new EmptyBorder(0, 40, 100, 100));

        JPanel logoutWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoutWrap.setOpaque(false);
        logoutWrap.setBorder(new EmptyBorder(4, 0, 6, 0));
        logoutWrap.add(logoutBtn);

        rightContent.add(centeredWrap, BorderLayout.CENTER);
        rightCard.add(rightContent, BorderLayout.CENTER);
        rightCard.add(logoutWrap, BorderLayout.SOUTH);

        JPanel leftWrapper = new JPanel(new GridBagLayout());
        leftWrapper.setOpaque(false);
        leftWrapper.add(leftCard);

        content.add(leftWrapper, BorderLayout.CENTER);
        content.add(rightCard, BorderLayout.EAST);

        serveBtn.addActionListener(e -> handleServeSelected());
        startTimerBtn.addActionListener(e -> startManualOneMinuteTimer());
        doneBtn.addActionListener(e -> handleDone());
        skipBtn.addActionListener(e -> handleSkip());

        logoutBtn.addActionListener(e -> {
            stopRefreshTimer();
            stopServeTimer();
            stopAlarmSound();
            selectedTicket = null;
            manualTimerActive = false;
            countdownFinished = false;
            frame.showHome();
        });

        setRightPanelEmpty();
    }

    public void loadAdmin(AdminAccount account) {
        this.account = account;
        this.selectedTicket = null;
        this.manualTimerActive = false;
        this.countdownFinished = false;
        this.remainingSeconds = 0;
        stopServeTimer();
        stopAlarmSound();

        officeValueLabel.setText(account.getOfficeType().name());
        rightTitleLabel.setText(account.getOfficeType().name() + " : Window " + account.getWindowNumber());

        refreshView();
        startRefreshTimer();
    }

    private void refreshView() {
        if (account == null) return;

        QueueTicket current = frame.getAdminController().current(account.getOfficeType(), account.getWindowNumber());

        serveBtn.setVisible(false);
        startTimerBtn.setVisible(false);
        doneBtn.setVisible(false);
        skipBtn.setVisible(false);
        timerLabel.setVisible(false);

        if (current != null) {
            activeNumberLabel.setText(current.getNumber());
            activeStudentLabel.setText("Student ID: " + current.getStudent().getStudentId());

            doneBtn.setVisible(true);
            skipBtn.setVisible(true);

            if (manualTimerActive) {
                timerLabel.setVisible(true);
                timerLabel.setText(formatTime(remainingSeconds));
            } else if (!countdownFinished) {
                startTimerBtn.setVisible(true);
            }

        } else {
            QueueTicket latestSelected = null;

            if (selectedTicket != null) {
                latestSelected = frame.getQueueService().findTicket(selectedTicket.getNumber());

                if (latestSelected == null || latestSelected.getState() != QueueState.WAITING) {
                    selectedTicket = null;
                    latestSelected = null;
                }
            }

            if (latestSelected != null) {
                activeNumberLabel.setText(latestSelected.getNumber());
                activeStudentLabel.setText("Student ID: " + latestSelected.getStudent().getStudentId());

                doneBtn.setVisible(true);
                skipBtn.setVisible(true);
                serveBtn.setVisible(true);
            } else {
                setRightPanelEmpty();
            }

            if (manualTimerActive || countdownFinished) {
                manualTimerActive = false;
                countdownFinished = false;
                stopServeTimer();
                stopAlarmSound();
            }
        }

        rebuildQueueRows();
    }

    private void setRightPanelEmpty() {
        activeNumberLabel.setText("");
        activeStudentLabel.setText("");
        serveBtn.setVisible(false);
        startTimerBtn.setVisible(false);
        doneBtn.setVisible(false);
        skipBtn.setVisible(false);
        timerLabel.setVisible(false);
    }

    private void rebuildQueueRows() {
        queueRowsPanel.removeAll();

        List<QueueTicket> officeTickets = new ArrayList<>();
        officeTickets.addAll(frame.getQueueService().getServingTickets(account.getOfficeType()));
        officeTickets.addAll(frame.getQueueService().getWaitingTickets(account.getOfficeType()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;

        int maxSlots = 10;

        for (int i = 0; i < maxSlots; i++) {
            gbc.gridx = i % 2;
            gbc.gridy = i / 2;

            if (i < officeTickets.size()) {
                queueRowsPanel.add(createCompactRow(officeTickets.get(i)), gbc);
            } else {
                queueRowsPanel.add(createEmptyRow(), gbc);
            }
        }

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = 5;
        filler.gridwidth = 2;
        filler.weighty = 1.0;
        filler.fill = GridBagConstraints.VERTICAL;
        filler.anchor = GridBagConstraints.NORTH;
        queueRowsPanel.add(Box.createVerticalGlue(), filler);

        queueRowsPanel.revalidate();
        queueRowsPanel.repaint();
    }

    private JPanel createCompactRow(QueueTicket ticket) {
        boolean isSelected = selectedTicket != null
                && selectedTicket.getNumber().equalsIgnoreCase(ticket.getNumber());

        boolean isWaiting = ticket.getState() == QueueState.WAITING;

        Color outerBg = isSelected
                ? new Color(151, 122, 24, 22)
                : new Color(255, 255, 255, 3);

        Color innerBg = isSelected
                ? new Color(255, 255, 255, 18)
                : new Color(255, 255, 255, 10);

        JPanel row = new ShadowlessRoundedPanel(outerBg, 18);
        row.setLayout(new BorderLayout());
        row.setBorder(new EmptyBorder(5, 5, 5, 5));
        row.setPreferredSize(new Dimension(420, 96));
        row.setMaximumSize(new Dimension(420, 96));
        row.setMinimumSize(new Dimension(420, 96));
        row.setCursor(isWaiting ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        row.setOpaque(false);

        JPanel inner = new ShadowlessRoundedPanel(innerBg, 16);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(10, 12, 10, 12));
        inner.setOpaque(false);

        JLabel numberLabel = Theme.label(ticket.getNumber(), 18, true, Theme.GOLD);
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel studentIdLabel = Theme.label(ticket.getStudent().getStudentId(), 13, false, Theme.TEXT_SOFT);
        studentIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String statusText;
        if (ticket.getState() == QueueState.SERVING) {
            statusText = "NOW SERVING";
        } else if (ticket.getState() == QueueState.WAITING) {
            statusText = "WAITING";
        } else {
            statusText = ticket.getState().name();
        }

        JLabel statusLabel = Theme.label(statusText, 15, true, Theme.TEXT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(Box.createVerticalGlue());
        inner.add(numberLabel);
        inner.add(Box.createRigidArea(new Dimension(0, 4)));
        inner.add(studentIdLabel);
        inner.add(Box.createRigidArea(new Dimension(0, 4)));
        inner.add(statusLabel);
        inner.add(Box.createVerticalGlue());

        row.add(inner, BorderLayout.CENTER);

        row.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!isWaiting) return;

                QueueTicket latest = frame.getQueueService().findTicket(ticket.getNumber());
                if (latest == null || latest.getState() != QueueState.WAITING) {
                    selectedTicket = null;
                    refreshView();
                    return;
                }

                selectedTicket = latest;
                manualTimerActive = false;
                countdownFinished = false;
                stopServeTimer();
                stopAlarmSound();

                refreshView();
            }
        });

        return row;
    }

    private JPanel createEmptyRow() {
        JPanel row = new ShadowlessRoundedPanel(new Color(255, 255, 255, 3), 18);
        row.setLayout(new BorderLayout());
        row.setBorder(new EmptyBorder(5, 5, 5, 5));
        row.setPreferredSize(new Dimension(420, 96));
        row.setMaximumSize(new Dimension(420, 96));
        row.setMinimumSize(new Dimension(420, 96));
        row.setOpaque(false);

        JPanel inner = new ShadowlessRoundedPanel(new Color(255, 255, 255, 8), 16);
        inner.setLayout(new GridBagLayout());
        inner.setOpaque(false);

        JLabel emptyLabel = Theme.label("---", 22, false, Theme.TEXT_SOFT);
        inner.add(emptyLabel);

        row.add(inner, BorderLayout.CENTER);

        return row;
    }

    private void handleServeSelected() {
        if (account == null || selectedTicket == null) return;

        QueueTicket latestSelected = frame.getQueueService().findTicket(selectedTicket.getNumber());
        if (latestSelected == null || latestSelected.getState() != QueueState.WAITING) {
            selectedTicket = null;
            refreshView();
            return;
        }

        QueueTicket served = frame.getAdminController().serveSelected(
                account.getOfficeType(),
                account.getWindowNumber(),
                latestSelected.getNumber()
        );

        if (served != null) {
            selectedTicket = served;
            manualTimerActive = false;
            countdownFinished = false;
            stopServeTimer();
            stopAlarmSound();
            refreshView();
        }
    }

    private void startManualOneMinuteTimer() {
        if (account == null) return;

        QueueTicket current = frame.getAdminController().current(account.getOfficeType(), account.getWindowNumber());
        if (current == null) return;

        playAlarmSound();
        manualTimerActive = true;
        countdownFinished = false;

        updateServeTimer(60);
        refreshView();
    }

    private void handleDone() {
        if (account == null) return;

        QueueTicket current = frame.getAdminController().current(account.getOfficeType(), account.getWindowNumber());

        if (current != null) {
            frame.getAdminController().done(account.getOfficeType(), account.getWindowNumber());
        }

        selectedTicket = null;
        manualTimerActive = false;
        countdownFinished = false;
        stopServeTimer();
        stopAlarmSound();
        setRightPanelEmpty();
        rebuildQueueRows();
    }

    private void handleSkip() {
        if (account == null) return;

        QueueTicket current = frame.getAdminController().current(account.getOfficeType(), account.getWindowNumber());

        if (current != null) {
            frame.getAdminController().skip(account.getOfficeType(), account.getWindowNumber());
        }

        selectedTicket = null;
        manualTimerActive = false;
        countdownFinished = false;
        stopServeTimer();
        stopAlarmSound();
        setRightPanelEmpty();
        rebuildQueueRows();
    }

    private void updateServeTimer(int seconds) {
        if (seconds <= 0) {
            stopServeTimer();
            manualTimerActive = false;
            countdownFinished = true;
            stopAlarmSound();
            refreshView();
            return;
        }

        remainingSeconds = seconds;
        timerLabel.setVisible(true);
        timerLabel.setText(formatTime(remainingSeconds));

        if (serveCountdownTimer != null && serveCountdownTimer.isRunning()) {
            serveCountdownTimer.stop();
        }

        serveCountdownTimer = new Timer(1000, e -> {
            remainingSeconds--;

            if (remainingSeconds <= 0) {
                stopServeTimer();
                manualTimerActive = false;
                countdownFinished = true;
                stopAlarmSound();
                refreshView();
                return;
            }

            timerLabel.setText(formatTime(remainingSeconds));
        });

        serveCountdownTimer.start();
    }

    private void stopServeTimer() {
        if (serveCountdownTimer != null && serveCountdownTimer.isRunning()) {
            serveCountdownTimer.stop();
        }
        remainingSeconds = 0;
        timerLabel.setVisible(false);
    }

    private void startRefreshTimer() {
        stopRefreshTimer();
        refreshTimer = new Timer(1000, e -> refreshView());
        refreshTimer.start();
    }

    private void stopRefreshTimer() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }

    private JButton buildGlassButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(Theme.TEXT);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(width, height));
        button.setPreferredSize(new Dimension(width, height));
        button.setUI(new Theme.RoundedButtonUI(new Color(255, 255, 255, 20), 16));
        return button;
    }

    private JButton buildMiniActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(Theme.GOLD);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(78, 34));
        button.setMaximumSize(new Dimension(78, 34));
        button.setMinimumSize(new Dimension(78, 34));
        button.setUI(new Theme.RoundedButtonUI(new Color(255, 255, 255, 10), 14));
        return button;
    }

    private JButton buildTextButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%d:%02d", mins, secs);
    }

    private static class ShadowlessRoundedPanel extends JPanel {
        private final Color fillColor;
        private final int arc;

        public ShadowlessRoundedPanel(Color fillColor, int arc) {
            this.fillColor = fillColor;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
        }
    }

    private void playAlarmSound() {
        try {
            if (alarmClip != null) {
                if (alarmClip.isRunning()) {
                    alarmClip.stop();
                }
                alarmClip.close();
            }

            File soundFile = new File("src/com/queueingsystem/ui/assets/alarm.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            alarmClip = AudioSystem.getClip();
            alarmClip.open(audioStream);
            alarmClip.setFramePosition(0);
            alarmClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAlarmSound() {
        try {
            if (alarmClip != null) {
                if (alarmClip.isRunning()) {
                    alarmClip.stop();
                }
                alarmClip.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
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
package com.queueingsystem.ui;

import com.queueingsystem.enumtypes.QueueState;
import com.queueingsystem.model.QueueTicket;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class StudentQueuePanel extends JPanel {

    private static final int WINDOW_CAPACITY = 10;

    private final JLabel deptValue;
    private final JLabel yourNumberValue;
    private final JLabel nowServingValue;
    private final JLabel nextValue;
    private final JLabel statusValue;
    private final JLabel timerValue;
    private final JLabel windowLabel;

    private final JPanel queueGrid;
    private final JPanel timerBanner;

    private final MainFrame frame;
    private QueueTicket currentTicket;
    private Timer countdownTimer;
    private int countdownSeconds;
    private boolean alreadyAlerted = false;

    private volatile boolean alarmRunning = false;
    private Thread alarmThread;

    public StudentQueuePanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        topBar.setBackground(Theme.TOP_BAR);
        topBar.setBorder(new EmptyBorder(10, 24, 10, 24));

        JLabel logo = new JLabel(Theme.scaledIcon("src/com/queueingsystem/ui/assets/logo.png", 46, 46));
        JLabel deptPrefix = Theme.label("DEPARTMENT:", 20, false, Theme.TEXT_SOFT);
        deptValue = Theme.label("FINANCE", 22, true, Theme.GOLD);

        topBar.add(logo);
        topBar.add(deptPrefix);
        topBar.add(deptValue);

        add(topBar, BorderLayout.NORTH);

        GradientPanel center = new GradientPanel();
        center.setLayout(new BorderLayout());
        center.setBorder(new EmptyBorder(14, 22, 22, 22));

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backPanel.setOpaque(false);

        JButton backBtn = Theme.iconButton("←", 18);
        backBtn.setPreferredSize(new Dimension(44, 44));
        backPanel.add(backBtn);

        center.add(backPanel, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(24, 0));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(6, 100, 150, 0));

        JPanel leftSide = new JPanel();
        leftSide.setOpaque(false);
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));

        JPanel liveHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        liveHeader.setOpaque(false);
        liveHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel liveQueueLabel = Theme.label("LIVE QUEUE", 24, true, Theme.TEXT);
        JLabel dateLabel = Theme.label(
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                18, false, Theme.TEXT_SOFT
        );

        liveHeader.add(liveQueueLabel);
        liveHeader.add(dateLabel);

        JPanel topCards = new JPanel(new GridLayout(1, 2, 18, 0));
        topCards.setOpaque(false);
        topCards.setMaximumSize(new Dimension(900, 145));
        topCards.setPreferredSize(new Dimension(900, 145));
        topCards.setAlignmentX(Component.LEFT_ALIGNMENT);

        Theme.RoundedPanel nowCard = new Theme.RoundedPanel(Theme.CARD, 22);
        nowCard.setLayout(new BoxLayout(nowCard, BoxLayout.Y_AXIS));
        nowCard.setBorder(new EmptyBorder(20, 26, 18, 26));

        JLabel nowLabel = Theme.label("NOW SERVING", 16, true, Theme.TEXT);
        nowServingValue = Theme.label("---", 42, true, Theme.GOLD);
        JLabel nowSub = Theme.label("Please proceed to the counter.", 14, false, Theme.TEXT_SOFT);

        nowLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nowServingValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        nowSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        nowCard.add(nowLabel);
        nowCard.add(Box.createRigidArea(new Dimension(0, 10)));
        nowCard.add(nowServingValue);
        nowCard.add(Box.createRigidArea(new Dimension(0, 8)));
        nowCard.add(nowSub);

        Theme.RoundedPanel nextCardTop = new Theme.RoundedPanel(Theme.CARD, 22);
        nextCardTop.setLayout(new BoxLayout(nextCardTop, BoxLayout.Y_AXIS));
        nextCardTop.setBorder(new EmptyBorder(20, 26, 18, 26));

        JLabel nextLabelTop = Theme.label("NEXT", 16, true, Theme.TEXT);
        nextValue = Theme.label("---", 42, true, Theme.GOLD);
        JLabel nextSubTop = Theme.label("Please get ready.", 14, false, Theme.TEXT_SOFT);

        nextLabelTop.setAlignmentX(Component.LEFT_ALIGNMENT);
        nextValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        nextSubTop.setAlignmentX(Component.LEFT_ALIGNMENT);

        nextCardTop.add(nextLabelTop);
        nextCardTop.add(Box.createRigidArea(new Dimension(0, 10)));
        nextCardTop.add(nextValue);
        nextCardTop.add(Box.createRigidArea(new Dimension(0, 8)));
        nextCardTop.add(nextSubTop);

        topCards.add(nowCard);
        topCards.add(nextCardTop);

        Theme.RoundedPanel queuePanel = new Theme.RoundedPanel(Theme.CARD, 24);
        queuePanel.setLayout(new BorderLayout());
        queuePanel.setBorder(new EmptyBorder(18, 18, 20, 18));
        queuePanel.setPreferredSize(new Dimension(900, 405));
        queuePanel.setMaximumSize(new Dimension(900, 405));
        queuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel queueTitle = Theme.label("Queue Dashboard", 16, false, Theme.TEXT);
        queuePanel.add(queueTitle, BorderLayout.NORTH);

        queueGrid = new JPanel(new GridLayout(5, 2, 18, 12));
        queueGrid.setOpaque(false);
        queueGrid.setBorder(new EmptyBorder(14, 0, 0, 0));

        for (int i = 0; i < WINDOW_CAPACITY; i++) {
            queueGrid.add(createQueueBox("---"));
        }

        queuePanel.add(queueGrid, BorderLayout.CENTER);

        leftSide.add(liveHeader);
        leftSide.add(Box.createRigidArea(new Dimension(0, 18)));
        leftSide.add(topCards);
        leftSide.add(Box.createRigidArea(new Dimension(0, 18)));
        leftSide.add(queuePanel);

        JPanel rightSide = new JPanel();
        rightSide.setOpaque(false);
        rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));
        rightSide.setPreferredSize(new Dimension(360, 520));
        rightSide.setMaximumSize(new Dimension(360, 520));

        windowLabel = Theme.label("FINANCE: Window 1", 16, false, Theme.TEXT);
        windowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Theme.RoundedPanel yourNumberCard = new Theme.RoundedPanel(Theme.CARD, 22);
        yourNumberCard.setLayout(new BoxLayout(yourNumberCard, BoxLayout.Y_AXIS));
        yourNumberCard.setBorder(new EmptyBorder(28, 28, 28, 28));
        yourNumberCard.setPreferredSize(new Dimension(340, 285));
        yourNumberCard.setMaximumSize(new Dimension(340, 285));
        yourNumberCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel yourNumberTitle = Theme.label("YOUR NUMBER", 18, true, Theme.TEXT);
        yourNumberTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        yourNumberValue = Theme.label("---", 60, true, Theme.GOLD);
        yourNumberValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusValue = Theme.label("Please wait to be called.", 15, false, Theme.TEXT_SOFT);
        statusValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        yourNumberCard.add(Box.createVerticalGlue());
        yourNumberCard.add(yourNumberTitle);
        yourNumberCard.add(Box.createRigidArea(new Dimension(0, 18)));
        yourNumberCard.add(yourNumberValue);
        yourNumberCard.add(Box.createRigidArea(new Dimension(0, 18)));
        yourNumberCard.add(statusValue);
        yourNumberCard.add(Box.createVerticalGlue());

        timerBanner = new Theme.RoundedPanel(new Color(120, 104, 24, 210), 18);
        timerBanner.setLayout(new BorderLayout(12, 0));
        timerBanner.setBorder(new EmptyBorder(16, 18, 16, 18));
        timerBanner.setMaximumSize(new Dimension(340, 70));
        timerBanner.setPreferredSize(new Dimension(340, 70));
        timerBanner.setVisible(false);

        JLabel bell = Theme.label("⏰", 22, true, Theme.TEXT);
        JLabel notifText = Theme.label("Please proceed now!", 15, false, Theme.TEXT);
        timerValue = Theme.label("1:00", 18, true, Theme.GOLD);

        JPanel notifLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        notifLeft.setOpaque(false);
        notifLeft.add(bell);
        notifLeft.add(notifText);

        timerBanner.add(notifLeft, BorderLayout.WEST);
        timerBanner.add(timerValue, BorderLayout.EAST);

        JButton exitBtn = Theme.iconButton("Exit Queue", 16);
        exitBtn.setPreferredSize(new Dimension(140, 46));
        exitBtn.setMaximumSize(new Dimension(140, 46));
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightSide.add(Box.createVerticalStrut(110));
        rightSide.add(windowLabel);
        rightSide.add(Box.createRigidArea(new Dimension(0, 18)));
        rightSide.add(yourNumberCard);
        rightSide.add(Box.createRigidArea(new Dimension(0, 18)));
        rightSide.add(timerBanner);
        rightSide.add(Box.createRigidArea(new Dimension(0, 16)));
        rightSide.add(exitBtn);

        JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rightWrapper.setOpaque(false);
        rightWrapper.setBorder(new EmptyBorder(0, 40, 0, 60));
        rightWrapper.add(rightSide);

        content.add(leftSide, BorderLayout.CENTER);
        content.add(rightWrapper, BorderLayout.EAST);
        center.add(content, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        backBtn.addActionListener(e -> frame.showHome());

        exitBtn.addActionListener(e -> {
            if (currentTicket != null) {
                frame.cancelStudentTicket(currentTicket);
                currentTicket = null;
                yourNumberValue.setText("---");
                statusValue.setText("Queue exited.");
                stopCountdown();
                stopAlarmLoop();
                timerBanner.setVisible(false);
                alreadyAlerted = false;
                frame.showHome();
            }
        });
    }

    private JPanel createQueueBox(String text) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(220, 58));

        JPanel inner = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel label;
        if ("---".equals(text)) {
            label = Theme.label(text, 18, false, Theme.TEXT_SOFT);
        } else {
            label = Theme.label(text, 18, true, Theme.TEXT);
        }

        inner.add(label);
        wrapper.add(inner, BorderLayout.CENTER);

        return wrapper;
    }

    public void loadTicket(QueueTicket ticket) {
        this.currentTicket = ticket;
        if (ticket == null) {
            yourNumberValue.setText("---");
            statusValue.setText("Please wait to be called.");
            return;
        }

        yourNumberValue.setText(ticket.getNumber());
        deptValue.setText(ticket.getOfficeType().name());

        String assignedText = ticket.getAssignedWindow() == null
                ? ticket.getOfficeType().name() + ": Waiting"
                : ticket.getOfficeType().name() + ": Window " + ticket.getAssignedWindow();
        windowLabel.setText(assignedText);

        frame.refreshStudentQueueView(ticket);
    }

    public void updateLiveQueue(String departmentName,
                                String windowText,
                                String nowServing,
                                String next,
                                List<String> queueNumbers,
                                boolean isCurrentUserNext,
                                boolean isCurrentUserCalled,
                                int remainingSeconds) {

        deptValue.setText(departmentName == null ? "FINANCE" : departmentName);
        windowLabel.setText(windowText == null ? "FINANCE: Window 1" : windowText);

        nowServingValue.setText(nowServing == null || nowServing.isBlank() ? "---" : nowServing);
        nextValue.setText(next == null || next.isBlank() ? "---" : next);

        queueGrid.removeAll();

        java.util.List<String> displayList = new java.util.ArrayList<>();

// 1. Add fetched queue numbers
        if (queueNumbers != null) {
            displayList.addAll(queueNumbers);
        }

// 2. Ensure current user's number is included
        if (currentTicket != null && currentTicket.getNumber() != null) {
            if (!displayList.contains(currentTicket.getNumber())) {
                displayList.add(0, currentTicket.getNumber()); // ilagay sa unahan
            }
        }

// 3. Fill grid
        for (int i = 0; i < WINDOW_CAPACITY; i++) {
            String value = (i < displayList.size()) ? displayList.get(i) : "---";
            queueGrid.add(createQueueBox(value));
        }

        if (currentTicket != null && currentTicket.getState() == QueueState.SERVING) {
            statusValue.setText("Please proceed to your office now.");
        } else if (isCurrentUserNext) {
            statusValue.setText("You are next. Please be ready.");
        } else {
            statusValue.setText("Please wait to be called.");
        }

        if (isCurrentUserCalled && remainingSeconds > 0) {
            if (!alreadyAlerted) {
                startAlarmLoop();
                alreadyAlerted = true;
            }
            startCountdown(remainingSeconds);
        } else {
            alreadyAlerted = false;
            stopCountdown();
            stopAlarmLoop();
            timerBanner.setVisible(false);
        }

        queueGrid.revalidate();
        queueGrid.repaint();
        revalidate();
        repaint();
    }

    private void startCountdown(int seconds) {
        countdownSeconds = seconds;
        timerBanner.setVisible(true);
        timerValue.setText(formatTime(countdownSeconds));

        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        countdownTimer = new Timer(1000, e -> {
            countdownSeconds--;

            if (countdownSeconds <= 0) {
                timerValue.setText("0:00");
                stopCountdown();
                stopAlarmLoop();
                alreadyAlerted = false;
                timerBanner.setVisible(false);
                return;
            }

            timerValue.setText(formatTime(countdownSeconds));
        });

        countdownTimer.start();
    }

    private void stopCountdown() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }

    private void startAlarmLoop() {
        if (alarmRunning) return;

        alarmRunning = true;
        alarmThread = new Thread(() -> {
            while (alarmRunning) {
                playJavaAlarm();
                sleepSilently(250);
            }
        });
        alarmThread.setDaemon(true);
        alarmThread.start();
    }

    private void stopAlarmLoop() {
        alarmRunning = false;
    }

    private void playJavaAlarm() {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();

            MidiChannel[] channels = synth.getChannels();
            if (channels != null && channels.length > 0) {
                MidiChannel channel = channels[0];

                channel.noteOn(84, 120);
                Thread.sleep(180);
                channel.noteOff(84);

                channel.noteOn(76, 120);
                Thread.sleep(180);
                channel.noteOff(76);

                channel.noteOn(84, 120);
                Thread.sleep(180);
                channel.noteOff(84);
            }

            synth.close();
        } catch (Exception e) {
            Toolkit.getDefaultToolkit().beep();
            Toolkit.getDefaultToolkit().beep();
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void sleepSilently(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
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
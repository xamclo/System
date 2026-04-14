package com.queueingsystem.ui;

import com.queueingsystem.controller.AdminController;
import com.queueingsystem.controller.StudentController;
import com.queueingsystem.enumtypes.OfficeType;
import com.queueingsystem.enumtypes.QueueState;
import com.queueingsystem.model.AdminAccount;
import com.queueingsystem.model.QueueTicket;
import com.queueingsystem.service.AuthService;
import com.queueingsystem.service.QueueService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(cardLayout);

    private final QueueService queueService = new QueueService();
    private final AuthService authService = new AuthService();
    private final StudentController studentController = new StudentController(queueService);
    private final AdminController adminController = new AdminController(queueService);

    private final HomePanel homePanel;
    private final StudentFormPanel studentFormPanel;
    private final StudentQueuePanel studentQueuePanel;
    private final AdminLoginPanel adminLoginPanel;
    private final AdminDashboardPanel adminDashboardPanel;


    private Timer studentRefreshTimer;
    private QueueTicket activeStudentTicket;

    public MainFrame() {
        setTitle("PilaLESS System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1365, 768);
        setMinimumSize(new Dimension(1280, 720));
        setLocationRelativeTo(null);
        setResizable(true);

        homePanel = new HomePanel(this);
        studentFormPanel = new StudentFormPanel(this);
        studentQueuePanel = new StudentQueuePanel(this);
        adminLoginPanel = new AdminLoginPanel(this);
        adminDashboardPanel = new AdminDashboardPanel(this);

        rootPanel.add(homePanel, "HOME");
        rootPanel.add(studentFormPanel, "STUDENT_FORM");
        rootPanel.add(studentQueuePanel, "STUDENT_QUEUE");
        rootPanel.add(adminLoginPanel, "ADMIN_LOGIN");
        rootPanel.add(adminDashboardPanel, "ADMIN_DASHBOARD");


        setContentPane(rootPanel);
        showHome();
    }

    public void showHome() {
        cardLayout.show(rootPanel, "HOME");
    }

    public void showStudentForm(OfficeType officeType) {
        studentFormPanel.setOfficeType(officeType);
        cardLayout.show(rootPanel, "STUDENT_FORM");
    }

    public void submitStudent(String name, String studentId, OfficeType officeType) {
        QueueTicket ticket = studentController.createTicket(name, studentId, officeType);

        if (ticket == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to create queue ticket.",
                    "Queue Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        activeStudentTicket = ticket;
        studentQueuePanel.loadTicket(ticket);
        refreshStudentQueueView(ticket);
        startStudentRefreshTimer();
        cardLayout.show(rootPanel, "STUDENT_QUEUE");
    }

    public boolean reopenStudentSession(String studentId) {
        QueueTicket ticket = queueService.findLatestTicketByStudentId(studentId);

        if (ticket == null) {
            return false;
        }

        activeStudentTicket = ticket;
        studentQueuePanel.loadTicket(ticket);
        refreshStudentQueueView(ticket);
        startStudentRefreshTimer();
        cardLayout.show(rootPanel, "STUDENT_QUEUE");
        return true;
    }

    public void showAdminLogin(OfficeType officeType) {
        adminLoginPanel.setOfficeType(officeType);
        cardLayout.show(rootPanel, "ADMIN_LOGIN");
    }

    public boolean loginAdmin(String username, String password, OfficeType selectedOffice) {
        AdminAccount account = authService.login(username, password);

        if (account == null) {
            return false;
        }

        if (account.getOfficeType() != selectedOffice) {
            return false;
        }

        adminDashboardPanel.loadAdmin(account);
        cardLayout.show(rootPanel, "ADMIN_DASHBOARD");
        return true;
    }


    public void cancelStudentTicket(QueueTicket ticket) {
        if (ticket == null) return;

        queueService.removeTicket(ticket);

        if (activeStudentTicket != null
                && activeStudentTicket.getNumber().equals(ticket.getNumber())) {
            activeStudentTicket = null;
            stopStudentRefreshTimer();
        }
    }

    public void refreshStudentQueueView(QueueTicket ticket) {
        if (ticket == null) return;

        QueueTicket latest = queueService.findTicket(ticket.getNumber());
        if (latest == null) {
            stopStudentRefreshTimer();
            activeStudentTicket = null;
            return;
        }

        activeStudentTicket = latest;

        OfficeType office = latest.getOfficeType();
        int assignedWindow = latest.getAssignedWindow() == null ? 1 : latest.getAssignedWindow();

        String departmentName = office.name();
        String nowServingDisplay = queueService.getNowServingDisplay(office);
        String next = queueService.getNextNumber(office);
        List<String> waitingNumbers = queueService.getWaitingNumbers(office);

        boolean isNext = next != null && next.equalsIgnoreCase(latest.getNumber());
        boolean isCalled = latest.getState() == QueueState.SERVING;

        int remainingSeconds = 0;
        if (isCalled && latest.getCalledAt() > 0) {
            long elapsed = (System.currentTimeMillis() - latest.getCalledAt()) / 1000;
            remainingSeconds = Math.max(0, 60 - (int) elapsed);
        }

        String windowText = departmentName + ": Window " + assignedWindow;

        studentQueuePanel.updateLiveQueue(
                departmentName,
                windowText,
                nowServingDisplay,
                next,
                waitingNumbers,
                isNext,
                isCalled,
                remainingSeconds
        );
    }

    private void startStudentRefreshTimer() {
        stopStudentRefreshTimer();

        studentRefreshTimer = new Timer(1000, e -> {
            if (activeStudentTicket != null) {
                refreshStudentQueueView(activeStudentTicket);
            }
        });

        studentRefreshTimer.start();
    }

    private void stopStudentRefreshTimer() {
        if (studentRefreshTimer != null && studentRefreshTimer.isRunning()) {
            studentRefreshTimer.stop();
        }
    }

    public QueueService getQueueService() {
        return queueService;
    }

    public AdminController getAdminController() {
        return adminController;
    }
}
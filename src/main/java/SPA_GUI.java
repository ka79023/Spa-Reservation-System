

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SPA_GUI {

    /* مكونات الإطار الرئيسي والتخطيط العام للشاشات */
    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;

    /* متغيرات حالة المستخدم الحالية */
    private static String currentUsername = "";
    private static String selectedService = "";
    private static boolean isUserRegistered = false;

    /* مكونات واجهة الحجز */
    private static JComboBox<String> dayComboBox;
    private static JComboBox<String> therapistComboBox;
    private static JLabel slotIdLabel;
    private static JLabel reservationMessage;
    private static JPanel reservationPanel;
    private static BackgroundPanel reservationBackgroundPanel;

    /* مكونات واجهة اختيار الخدمات */
    private static JComboBox<String> serviceDropdown;

    /* إعدادات التصميم والألوان */
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final Color PRIMARY_BG = new Color(255, 230, 240);
    private static final Color DARK_PURPLE = new Color(130, 0, 130);
    // بنفسجي غامق للثيم
    private static final Color ACCENT_PURPLE = new Color(110, 0, 110);
    private static final Color BUTTON_EXIT_COLOR = new Color(110, 0, 110);
    private static final Color TEXT_COLOR = new Color(80, 0, 80);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 36);
    private static final Font SUB_HEADER_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 14);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SPA_GUI::initializeGUI);
    }

    public static void initializeGUI() {
        frame = new JFrame("SPA Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createWelcomePanel(), "Welcome");
        mainPanel.add(createServiceSelectionPanel(), "Services");
        reservationPanel = createReservationPanel();
        mainPanel.add(reservationPanel, "Reservation");
        mainPanel.add(createMyReservationsPanel(), "MyReservations");

        frame.add(mainPanel);
        frame.setVisible(true);
        cardLayout.show(mainPanel, "Welcome");
    }

    private static JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;
            {
                try {
                    backgroundImage = new ImageIcon(
                            SPA_GUI.class.getResource("/LLL.jpg")
                    ).getImage();
                } catch (Exception e) {
                    System.out.println("Welcome background image not found: " + e.getMessage());
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        panel.setBackground(PRIMARY_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Welcome Back SPA KARLN", SwingConstants.CENTER);
        title.setFont(HEADER_FONT);
        title.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        JLabel welcomeMsg = new JLabel("Please choose your user type:", SwingConstants.CENTER);
        welcomeMsg.setFont(SUB_HEADER_FONT);
        welcomeMsg.setForeground(TEXT_COLOR);
        gbc.gridy = 1;
        panel.add(welcomeMsg, gbc);

        // زر New User - أبيض + نص بنفسجي
        JButton newUserBtn = new JButton("1. New User");
        newUserBtn.setPreferredSize(new Dimension(250, 50));
        newUserBtn.setFont(SUB_HEADER_FONT);
        newUserBtn.setBackground(Color.WHITE);
        newUserBtn.setForeground(TEXT_COLOR);
        newUserBtn.setFocusPainted(false);
        newUserBtn.addActionListener(e -> handleNewUserRegistration());
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.gridx = 0;
        panel.add(newUserBtn, gbc);

        // زر Existing User - نفس شكل New User
        JButton existingUserBtn = new JButton("2. Existing User");
        existingUserBtn.setPreferredSize(new Dimension(250, 50));
        existingUserBtn.setFont(SUB_HEADER_FONT);
        existingUserBtn.setBackground(Color.WHITE);
        existingUserBtn.setForeground(TEXT_COLOR);
        existingUserBtn.setFocusPainted(false);
        existingUserBtn.setEnabled(true);
        existingUserBtn.addActionListener(e -> handleExistingUserLogin());
        gbc.gridx = 1;
        panel.add(existingUserBtn, gbc);

        // زر Exit - بنفسجي غامق + نص أبيض
        JButton exitBtn = new JButton("3. Exit");
        exitBtn.setBackground(BUTTON_EXIT_COLOR);
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.addActionListener(e -> System.exit(0));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(exitBtn, gbc);

        return panel;
    }

    private static void handleNewUserRegistration() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(
                frame,
                message,
                "New User Registration",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter both username and password.",
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String result = ClientTest.Register(username, password);

            if (result.contains("successfuly registered")) {
                currentUsername = username;
                isUserRegistered = true;
                JOptionPane.showMessageDialog(frame,
                        "Welcome " + username + "! Registration was successful.",
                        "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "Services");
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Registration failed: " + result,
                        "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void handleExistingUserLogin() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(
                frame,
                message,
                "Existing User Login",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter both username and password.",
                        "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String result = ClientTest.login(username, password);

            if (result.toLowerCase().contains("login successful")) {
                currentUsername = username;
                isUserRegistered = true;
                JOptionPane.showMessageDialog(frame,
                        "Welcome back " + username + "!",
                        "Login Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "Services");
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Login failed: " + result,
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static JPanel createServiceSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;
            {
                try {
                    backgroundImage = new ImageIcon(
                            SPA_GUI.class.getResource("/wating.jpg")
                    ).getImage();
                } catch (Exception e) {
                    System.out.println("Services background image not found: " + e.getMessage());
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridwidth = 2;

        JLabel instruction = new JLabel("Find Your Bliss! Select a Service:", SwingConstants.CENTER);
        instruction.setFont(HEADER_FONT);
        instruction.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(instruction, gbc);

        String[] services = {"Manicure", "Pedicure", "Massage", "Facial", "Reflexology"};
        serviceDropdown = new JComboBox<>(services);
        serviceDropdown.setPreferredSize(new Dimension(250, 40));
        serviceDropdown.setFont(SUB_HEADER_FONT);
        gbc.gridy = 1;
        panel.add(serviceDropdown, gbc);

        JButton viewSlotsBtn = new JButton("View Available Slots");
        viewSlotsBtn.setPreferredSize(new Dimension(300, 50));
        viewSlotsBtn.setFont(SUB_HEADER_FONT);
        viewSlotsBtn.setBackground(ACCENT_PURPLE);
        viewSlotsBtn.setForeground(Color.WHITE);
        viewSlotsBtn.setFocusPainted(false);
        gbc.gridy = 2;
        panel.add(viewSlotsBtn, gbc);

        JButton viewMyReservationsBtn = new JButton("View My Reservations");
        viewMyReservationsBtn.setPreferredSize(new Dimension(300, 50));
        viewMyReservationsBtn.setFont(SUB_HEADER_FONT);
        viewMyReservationsBtn.setBackground(ACCENT_PURPLE);
        viewMyReservationsBtn.setForeground(Color.WHITE);
        viewMyReservationsBtn.setFocusPainted(false);
        gbc.gridy = 3;
        panel.add(viewMyReservationsBtn, gbc);

        viewMyReservationsBtn.addActionListener(e -> {
            if (!isUserRegistered) {
                JOptionPane.showMessageDialog(frame, "Please login first");
                return;
            }

            JPanel myResPanel = (JPanel) mainPanel.getComponent(3);
            JComboBox<String> reservationsComboBox = (JComboBox<String>) myResPanel.getComponent(1);
            updateReservationsComboBox(reservationsComboBox);

            cardLayout.show(mainPanel, "MyReservations");
        });

        JButton backToWelcomeBtn = new JButton("Back to Welcome");
        backToWelcomeBtn.addActionListener(e -> cardLayout.show(mainPanel, "Welcome"));
        backToWelcomeBtn.setBackground(BUTTON_EXIT_COLOR);
        backToWelcomeBtn.setForeground(Color.WHITE);
        backToWelcomeBtn.setFocusPainted(false);
        gbc.gridy = 4;
        panel.add(backToWelcomeBtn, gbc);

        viewSlotsBtn.addActionListener(e -> {
            selectedService = ((String) serviceDropdown.getSelectedItem()).trim();
            updateReservationPanel(selectedService);
            cardLayout.show(mainPanel, "Reservation");
        });

        return panel;
    }

    private static JPanel createReservationPanel() {
        reservationBackgroundPanel = new BackgroundPanel();
        reservationBackgroundPanel.setLayout(new BorderLayout(15, 15));

        reservationBackgroundPanel.setBackground(PRIMARY_BG);
        reservationBackgroundPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel innerReservationPanel = new JPanel(new BorderLayout(15, 15));
        innerReservationPanel.setOpaque(false);
        innerReservationPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_PURPLE, 2),
                "Book Your Time",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                SUB_HEADER_FONT,
                TEXT_COLOR
        ));

        // لوحة اختيار اليوم والمعالج
        JPanel selectionPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        selectionPanel.setOpaque(false);

        JLabel dayLabel = new JLabel("Select Day:");
        dayLabel.setFont(SMALL_FONT);
        dayLabel.setForeground(TEXT_COLOR);
        dayComboBox = new JComboBox<>();
        dayComboBox.setFont(SMALL_FONT);

        JLabel therapistLabel = new JLabel("Select Therapist:");
        therapistLabel.setFont(SMALL_FONT);
        therapistLabel.setForeground(TEXT_COLOR);
        therapistComboBox = new JComboBox<>();
        therapistComboBox.setFont(SMALL_FONT);

        JLabel slotLabel = new JLabel("Slot ID:");
        slotLabel.setFont(SMALL_FONT);
        slotLabel.setForeground(TEXT_COLOR);
        slotIdLabel = new JLabel("Not selected");
        slotIdLabel.setFont(SMALL_FONT);
        slotIdLabel.setForeground(DARK_PURPLE);

        selectionPanel.add(dayLabel);
        selectionPanel.add(dayComboBox);
        selectionPanel.add(therapistLabel);
        selectionPanel.add(therapistComboBox);
        selectionPanel.add(slotLabel);
        selectionPanel.add(slotIdLabel);

        // إضافة الـ Listeners
        dayComboBox.addActionListener(e -> updateTherapists());
        therapistComboBox.addActionListener(e -> updateSlotId());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel slotsLabel = new JLabel("Available Slots:", SwingConstants.CENTER);
        slotsLabel.setFont(SUB_HEADER_FONT);
        slotsLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(slotsLabel, gbc);

        gbc.gridy = 1;
        centerPanel.add(selectionPanel, gbc);

        innerReservationPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bookingControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bookingControls.setOpaque(false);

        JButton confirmBtn = new JButton("Book Selected Slot");
        confirmBtn.setBackground(ACCENT_PURPLE);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);

        reservationMessage = new JLabel("Select a day and therapist to see available slots.", SwingConstants.CENTER);
        reservationMessage.setForeground(TEXT_COLOR);
        reservationMessage.setFont(SMALL_FONT);

        confirmBtn.addActionListener(e -> {
            String slotId = slotIdLabel.getText();
            if (slotId.equals("Not selected") || slotId.equals("Not available")) {
                reservationMessage.setText("Please select a valid day and therapist.");
                reservationMessage.setForeground(Color.RED);
                return;
            }

            reservationMessage.setText("Attempting to book selected slot...");
            String result = ClientTest.attemptReservation(slotId, currentUsername, selectedService);

            if (result.toLowerCase().startsWith("reservation was successful")) {
                JOptionPane.showMessageDialog(frame, result, "Reservation Success", JOptionPane.INFORMATION_MESSAGE);
                reservationMessage.setText("Booking successful! Slot: " + slotId);
                reservationMessage.setForeground(new Color(0, 150, 0));
                updateReservationPanel(selectedService);
            } else {
                JOptionPane.showMessageDialog(frame, result, "Reservation Failed", JOptionPane.ERROR_MESSAGE);
                reservationMessage.setText("Booking failed: " + result);
                reservationMessage.setForeground(Color.RED);
            }
        });

        bookingControls.add(confirmBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(bookingControls, BorderLayout.NORTH);
        southPanel.add(reservationMessage, BorderLayout.CENTER);

        JButton backToServicesBtn = new JButton("Choose Another Service");
        backToServicesBtn.addActionListener(e -> cardLayout.show(mainPanel, "Services"));
        backToServicesBtn.setBackground(BUTTON_EXIT_COLOR);
        backToServicesBtn.setForeground(Color.WHITE);
        backToServicesBtn.setFocusPainted(false);
        southPanel.add(backToServicesBtn, BorderLayout.SOUTH);

        innerReservationPanel.add(southPanel, BorderLayout.SOUTH);
        reservationBackgroundPanel.add(innerReservationPanel, BorderLayout.CENTER);

        return reservationBackgroundPanel;
    }

    private static JPanel createMyReservationsPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;
            {
                try {
                    backgroundImage = new ImageIcon(
                            SPA_GUI.class.getResource("/cancel.jpg")
                    ).getImage();
                } catch (Exception e) {
                    System.out.println("My Reservations background image not found: " + e.getMessage());
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridwidth = 2;

        JLabel title = new JLabel("My Reservations - " + currentUsername, SwingConstants.CENTER);
        title.setFont(HEADER_FONT);
        title.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(title, gbc);

        // ComboBox للحجوزات
        JComboBox<String> reservationsComboBox = new JComboBox<>();
        reservationsComboBox.setPreferredSize(new Dimension(500, 35));
        reservationsComboBox.setMaximumRowCount(8);
        reservationsComboBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        gbc.gridy = 1;
        panel.add(reservationsComboBox, gbc);

        // تحديث القائمة عند فتح الشاشة
        updateReservationsComboBox(reservationsComboBox);

        JButton cancelBtn = new JButton("Cancel Selected Reservation");
        cancelBtn.setBackground(BUTTON_EXIT_COLOR);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        gbc.gridy = 2;
        panel.add(cancelBtn, gbc);

        JButton backBtn = new JButton("Back to Services");
        backBtn.setBackground(ACCENT_PURPLE);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Services"));
        gbc.gridy = 3;
        panel.add(backBtn, gbc);

        cancelBtn.addActionListener(e -> {
            String selected = (String) reservationsComboBox.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(frame, "Please select a reservation to cancel");
                return;
            }

            String slotId = extractSlotIdFromLine(selected);
            if (slotId == null) {
                JOptionPane.showMessageDialog(frame, "Could not find Slot ID in selection");
                return;
            }

            String result = ClientTest.cancelReservation(slotId, currentUsername);

            if (result.toLowerCase().contains("cancelled successfully")) {
                JOptionPane.showMessageDialog(frame, result, "Cancellation Success", JOptionPane.INFORMATION_MESSAGE);
                updateReservationsComboBox(reservationsComboBox);
            } else {
                JOptionPane.showMessageDialog(frame, result, "Cancellation Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private static void updateReservationsComboBox(JComboBox<String> comboBox) {
        String reservations = ClientTest.getMyReservations(currentUsername);
        comboBox.removeAllItems();
        String[] reservationLines = reservations.split("\n");
        for (String line : reservationLines) {
            if (!line.trim().isEmpty() && !line.contains("Your Reservations")) {
                comboBox.addItem(line.trim());
            }
        }
    }

    private static void updateReservationPanel(String serviceName) {
        String titleMessage = "Book Your Time for a Relaxing " + serviceName + " Session";
        TitledBorder border = (TitledBorder) ((JPanel)reservationPanel.getComponent(0)).getBorder();
        border.setTitle(titleMessage);

        String imageName = getImageForService(serviceName);
        reservationBackgroundPanel.setBackgroundImage(imageName);

        reservationPanel.repaint();
        frame.setTitle("SPA - Booking " + serviceName);

        // تحميل البيانات من الخادم
        String slots = ClientTest.getAvailableSlots(serviceName.trim());

        // استخراج الأيام والمعالجين من النتيجة
        java.util.Set<String> days = new java.util.TreeSet<>();
        java.util.Set<String> therapists = new java.util.TreeSet<>();

        String[] lines = slots.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty() && !line.contains("Available Time Slots") && !line.contains("No slots")) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String day = parts[0].replaceAll("^\\d+\\.\\s*", "").trim();
                    days.add(day);

                    String therapist = parts[2].replaceAll("\\(ID:.*", "").trim();
                    therapists.add(therapist);
                }
            }
        }

        dayComboBox.removeAllItems();
        for (String day : days) {
            dayComboBox.addItem(day);
        }

        therapistComboBox.removeAllItems();
        slotIdLabel.setText("Not selected");

        reservationMessage.setText("Available slots loaded. Select your preferred day and therapist.");
        reservationMessage.setForeground(TEXT_COLOR);
    }

    private static void updateTherapists() {
        String selectedDay = (String) dayComboBox.getSelectedItem();
        if (selectedDay == null) return;

        // تحميل البيانات من الخادم
        String slots = ClientTest.getAvailableSlots(selectedService.trim());

        // استخراج المعالجين المتاحين لليوم المحدد
        java.util.Set<String> availableTherapists = new java.util.TreeSet<>();

        String[] lines = slots.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty() && !line.contains("Available Time Slots") && !line.contains("No slots")) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String day = parts[0].replaceAll("^\\d+\\.\\s*", "").trim();
                    if (day.equals(selectedDay)) {
                        String therapist = parts[2].replaceAll("\\(ID:.*", "").trim();
                        availableTherapists.add(therapist);
                    }
                }
            }
        }

        therapistComboBox.removeAllItems();
        for (String therapist : availableTherapists) {
            therapistComboBox.addItem(therapist);
        }
        slotIdLabel.setText("Not selected");
    }

    private static void updateSlotId() {
        String selectedDay = (String) dayComboBox.getSelectedItem();
        String selectedTherapist = (String) therapistComboBox.getSelectedItem();

        if (selectedDay == null || selectedTherapist == null) return;

        // البحث عن الـ Slot ID المناسب
        String slotId = findSlotId(selectedService, selectedDay, selectedTherapist);
        if (slotId != null) {
            slotIdLabel.setText(slotId);
        } else {
            slotIdLabel.setText("Not available");
        }
    }

    private static String findSlotId(String serviceName, String day, String therapist) {
        String slots = ClientTest.getAvailableSlots(serviceName.trim());
        String[] lines = slots.split("\n");

        for (String line : lines) {
            if (line.contains(day) && line.contains(therapist)) {
                // استخراج الـ ID من النص
                int start = line.indexOf("(ID: ");
                if (start != -1) {
                    int end = line.indexOf(")", start);
                    return line.substring(start + 5, end);
                }
            }
        }
        return null;
    }

    private static String getImageForService(String serviceName) {
        if ("Manicure".equals(serviceName)) {
            return "mana.jpg";
        } else if ("Pedicure".equals(serviceName)) {
            return "pade.jpg";
        } else if ("Massage".equals(serviceName)) {
            return "mesa.jpg";
        } else if ("Facial".equals(serviceName)) {
            return "faci.jpg";
        } else if ("Reflexology".equals(serviceName)) {
            return "phis.jpg";
        } else {
            return "wating.jpg";
        }
    }

    private static String extractSlotIdFromLine(String line) {
        int idx = line.toUpperCase().indexOf("SLOT-");
        if (idx == -1) return null;
        int end = idx + 5;
        while (end < line.length() && (Character.isDigit(line.charAt(end)) || line.charAt(end) == '-')) {
            end++;
        }
        return line.substring(idx, end).trim();
    }

    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            super();
            setOpaque(false);
        }

        public void setBackgroundImage(String imageName) {
            try {
                java.net.URL imageURL = SPA_GUI.class.getResource("/" + imageName);
                if (imageURL != null) {
                    backgroundImage = new ImageIcon(imageURL).getImage();
                } else {
                    backgroundImage = null;
                }
            } catch (Exception e) {
                backgroundImage = null;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}

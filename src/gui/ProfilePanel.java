package gui;

import db.Database;
import java.awt.*;
import java.awt.event.*; // to handle file selection
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*; // to filter image file types
import javax.swing.filechooser.FileNameExtensionFilter;
import main.Main;

public class ProfilePanel extends JPanel {
    private JTextField nameField, courseField, emailField, idField;
    private JTextArea bioArea; // Changed to class level to access in save method
    private JLabel imageLabel;
    private String selectedImagePath = "";
    private String currentUsername; // Store the username to know WHO to update

    private DashboardPanel dashRef;

    public ProfilePanel(myFrame frameObject, String username, DashboardPanel dashboard) {
        this.currentUsername = username;
        this.dashRef = dashboard;
        setLayout(null);
        setBackground(Main.BG_COLOR);

        // HEADER
        JLabel title = new JLabel("User Profile Settings");
        title.setBounds(40, 40, 400, 50);
        title.setFont(new Font("Helvetica", Font.BOLD, 28));
        title.setForeground(Main.TEXT_COLOR);
        add(title);

        JPanel underline = new JPanel();
        underline.setBounds(40, 85, 80, 4);
        underline.setBackground(Main.ACCENT_COLOR);
        add(underline);

        // PROFILE IMAGE UI
        setupImageUI(frameObject);

        // PERSONAL DETAILS UI
        int x = 40, y = 120, w = 380, h = 35;
        add(createSectionHeader("Personal Information", x, y));
        
        add(createLabel("Full Name", x, y + 40));
        nameField = createField(x, y + 65, w, h); 
        add(nameField);

        add(createLabel("Student ID Number", x, y + 110));
        idField = createField(x, y + 135, w, h); 
        add(idField);

        add(createSectionHeader("Academic & Contact", x, y + 200));

        add(createLabel("Course & Year Level", x, y + 240));
        courseField = createField(x, y + 265, w, h); 
        add(courseField);

        add(createLabel("Official Email", x, y + 310));
        emailField = createField(x, y + 335, w, h); 
        add(emailField);

        add(createLabel("Short Bio", x, y + 375));
        bioArea = new JTextArea();
        bioArea.setBounds(x, y + 400, w, 120); 
        bioArea.setBackground(Color.WHITE);
        bioArea.setForeground(Main.TEXT_COLOR);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setCaretColor(Main.ACCENT_COLOR); // Cursor color
        bioArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1), // The visible gray line
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        add(bioArea);

        // SUBMIT BUTTON
        JButton saveBtn = createSolidButton("FINALIZE PROFILE", 700, 600, 200, 55);
        saveBtn.addActionListener(e -> saveProfileChanges(frameObject));
        add(saveBtn);

        // Load existing user data
        loadUserData();
    }

    private void loadUserData() {
        System.out.println("Attempting to load data for: " + currentUsername);
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT full_name, student_id, course_year, email, bio FROM users WHERE username = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, currentUsername);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("User found! Setting fields...");
                nameField.setText(rs.getString("full_name"));
                idField.setText(rs.getString("student_id"));
                courseField.setText(rs.getString("course_year"));
                emailField.setText(rs.getString("email"));

                String bio = rs.getString("bio");
                bioArea.setText(bio != null ? bio : "");
            } else {
                System.out.println("No user found in DB for username: " + currentUsername);
            }
        } catch (Exception e) {
            System.out.println("Error loading profile data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // helper to create section headers
    private JLabel createSectionHeader(String text, int x, int y) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setBounds(x, y, 300, 25);
        l.setForeground(Main.ACCENT_COLOR);
        l.setFont(new Font("Helvetica", Font.BOLD, 13));
        return l;
    }
    
    private void saveProfileChanges(myFrame frameObject) {
        String newName = nameField.getText();
        String newCourse = courseField.getText();

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE users SET full_name=?, student_id=?, course_year=?, email=?, bio=? WHERE username=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newName);
            pst.setString(2, idField.getText());
            pst.setString(3, newCourse);
            pst.setString(4, emailField.getText());
            pst.setString(5, bioArea.getText());
            pst.setString(6, currentUsername);

            int updated = pst.executeUpdate();
            if (updated > 0) {
                dashRef.refreshDashboardInfo(newName, newCourse);
                if (!selectedImagePath.isEmpty()) {
                    updateSidebarImage(frameObject, selectedImagePath);
                }
                CustomDialog.show(frameObject.getFrame(), "Student Profile Synced!", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show(frameObject.getFrame(), "Error saving profile.", false);
        }
    }

    private void setupImageUI(myFrame frameObject) {
        imageLabel = new JLabel("No Photo", SwingConstants.CENTER);
        imageLabel.setBounds(700, 100, 180, 180);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setForeground(new Color(0x95a5a6));
        imageLabel.setFont(new Font("Helvetica", Font.ITALIC, 12));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 2));
        add(imageLabel);

        JButton uploadBtn = createOutlineButton("UPDATE AVATAR", 700, 300, 180, 35);
        uploadBtn.addActionListener(e -> chooseImage());
        add(uploadBtn);
    }

    // to open file explorer once the button is clicked
    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();

        // Filter to accept only image files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();
            
            ImageIcon icon = new ImageIcon(selectedImagePath);
            Image img = icon.getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
            imageLabel.setText(""); 
        }
    }

    // Helper to update the sidebar icon in the frame
    private void updateSidebarImage(myFrame frame, String path) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        frame.sidebarProfileImg.setIcon(new ImageIcon(img));
    }

    private JLabel createLabel(String txt, int x, int y) {
        JLabel l = new JLabel(txt);
        l.setBounds(x, y, 200, 20);
        l.setForeground(Main.TEXT_COLOR);
        l.setFont(new Font("Helvetica", Font.BOLD, 13));
        return l;
    }

    private JTextField createField(int x, int y, int w, int h) {
        JTextField f = new JTextField(); //
        f.setBounds(x, y, w, h);
        f.setBackground(Color.WHITE);
        f.setForeground(Main.TEXT_COLOR);
        f.setCaretColor(Main.ACCENT_COLOR);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        return f;
    }

    // For the big "FINALIZE" button
    private JButton createSolidButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setBackground(Main.ACCENT_COLOR); // Indigo
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Helvetica", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0x3F48CC)); } // Darker Indigo
            public void mouseExited(MouseEvent e) { btn.setBackground(Main.ACCENT_COLOR); }
        });
        return btn;
    }

    // For the "UPDATE AVATAR" button
    private JButton createOutlineButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Main.ACCENT_COLOR);
        btn.setFont(new Font("Helvetica", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 1));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0xF1F2F6)); } // Light grey hover
            public void mouseExited(MouseEvent e) { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }
}

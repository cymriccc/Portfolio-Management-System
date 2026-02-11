package gui;

import db.Database;
import java.awt.*;
import java.awt.event.*; // to handle file selection
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*; // to filter image file types
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import main.Main;

public class ProfilePanel extends JPanel {
    private JTextField nameField, emailField, idField, userField;
    private JComboBox<String> courseComboBox;
    private JTextArea bioArea;
    private JLabel imageLabel;
    private myFrame frameRef;
    private String selectedImagePath = "";
    private String currentUsername;

    private DashboardPanel dashRef;

    public ProfilePanel(myFrame frameObject, String username, DashboardPanel dashboard) {
        this.frameRef = frameObject;
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
        nameField.setEditable(false);
        nameField.setFocusable(false);
        nameField.setBackground(new Color(0xEEEEEE)); 
        add(nameField); 

        add(createLabel("Username", x, y + 110));
        userField = createField(x, y + 135, w, h);
        add(userField);

        add(createLabel("Student ID Number", x, y + 180));
        idField = createField(x, y + 205, w, h); 
        idField.setEditable(false);
        idField.setFocusable(false);
        idField.setBackground(new Color(0xEEEEEE)); 
        add(idField);

        add(createSectionHeader("Academic & Contact", x, y + 260));

        add(createLabel("Course & Year Level", x, y + 300));
        courseComboBox = new JComboBox<>();
        courseComboBox.setBounds(x, y + 325, w, h);
        courseComboBox.setFocusable(false);
        courseComboBox.setEnabled(true);
        courseComboBox.setEditable(false);
        courseComboBox.setBackground(Color.WHITE);
        courseComboBox.setForeground(Color.BLACK);
        courseComboBox.setFont(new Font("Helvetica", Font.PLAIN, 12));
        courseComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(0, 10, 0, 0));
                label.setFont(new Font("Helvetica", Font.PLAIN, 12));
                label.setForeground(Color.BLACK); 
                return label;
            }
        });
        add(courseComboBox);

        add(createLabel("Email Address", x, y + 370));
        emailField = createField(x, y + 395, w, h); 
        emailField.setFocusable(false);
        emailField.setEnabled(true);
        emailField.setEditable(false);
        emailField.setBackground(new Color(0xEEEEEE)); 
        add(emailField);

        add(createLabel("Short Bio", x, y + 440));
        bioArea = new JTextArea();
        bioArea.setBounds(x, y + 465, w, 100); 
        bioArea.setBackground(Color.WHITE);
        bioArea.setForeground(Main.TEXT_COLOR);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setCaretColor(Main.ACCENT_COLOR);
        bioArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1), // The visible gray line
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        add(bioArea);

        // SUBMIT BUTTON
        JButton saveBtn = createSolidButton("FINALIZE PROFILE", 700, 600, 200, 55);
        saveBtn.addActionListener(e -> saveProfileChanges(frameObject));
        add(saveBtn);

        JButton passBtn = createOutlineButton("CHANGE PASSWORD", 700, 550, 200, 35);
        passBtn.addActionListener(e -> showChangePasswordDialog());
        add(passBtn);

        loadUserData();
    }

    private void loadUserData() {
        try (Connection conn = Database.getConnection()) {
        String sql = "SELECT full_name, username, student_id, course_year, email, bio, profile_picture FROM users WHERE username = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, currentUsername);
        ResultSet rs = pst.executeQuery();

       if (rs.next()) {
            nameField.setText(rs.getString("full_name"));
            userField.setText(rs.getString("username"));
            idField.setText(rs.getString("student_id"));
            emailField.setText(rs.getString("email"));

            String fullCourseYear = rs.getString("course_year"); // e.g., "BS Information Technology - 1"
            courseComboBox.removeAllItems();
            if (fullCourseYear != null && fullCourseYear.contains(" - ")) {
                String[] parts = fullCourseYear.split(" - ");
                String baseCourse = parts[0]; // This is "BS Information Technology"
                for (int i = 1; i <= 4; i++) {
                    courseComboBox.addItem(baseCourse + " - " + i);
                }
                courseComboBox.setSelectedItem(fullCourseYear);
            } else {
                courseComboBox.addItem(fullCourseYear);
            }
            String bio = rs.getString("bio");
            bioArea.setText(bio != null ? bio : "");

            byte[] imgBytes = rs.getBytes("profile_picture");
            if (imgBytes != null) {
                ImageIcon icon = new ImageIcon(imgBytes);
                Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
                imageLabel.setText("");
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
}
    private JLabel createSectionHeader(String text, int x, int y) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setBounds(x, y, 300, 25);
        l.setForeground(Main.ACCENT_COLOR);
        l.setFont(new Font("Helvetica", Font.BOLD, 13));
        return l;
    }
    
    private void saveProfileChanges(myFrame frameObject) {
    String newUsername = userField.getText().trim();
    String newStudentID = idField.getText().trim();

    if (!newUsername.matches("^[a-zA-Z0-9_-]{3,}$")) {
            CustomDialog.show(frameObject.getFrame(), "Invalid Username (3+ chars, _ or - only).", false);
            return;
        }
        if (!newStudentID.matches("\\d{4}-\\d{4}")) {
            CustomDialog.show(frameObject.getFrame(), "Student ID must be 0000-0000 format.", false);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            // Check if username changed and if the new one is taken
            if (!newUsername.equals(currentUsername)) {
                String checkSql = "SELECT id FROM users WHERE username = ?";
                PreparedStatement checkPst = conn.prepareStatement(checkSql);
                checkPst.setString(1, newUsername);
                if (checkPst.executeQuery().next()) {
                    CustomDialog.show(frameObject.getFrame(), "Username already taken.", false);
                    return;
                }
            }

        String sql = "UPDATE users SET username=?, student_id=?, email=?, bio=?, course_year=? WHERE username=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, newUsername);
        pst.setString(2, newStudentID);
        pst.setString(3, emailField.getText());
        pst.setString(4, bioArea.getText());
        pst.setString(5, (String) courseComboBox.getSelectedItem()); 
        pst.setString(6, currentUsername);

        if (pst.executeUpdate() > 0) {  
            this.currentUsername = newUsername; 
            
            if (dashRef != null) dashRef.refreshData();
            if (dashRef != null) dashRef.refreshData();
            if (frameRef != null) {
                frameRef.loadExistingAvatar(newUsername);
            }

            CustomDialog.show(frameObject.getFrame(), "Profile Synced Successfully!", true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show(frameObject.getFrame(), "Database Error: " + e.getMessage(), false);
        }
    }
    private void showChangePasswordDialog() {
       JDialog dialog = new JDialog(frameRef.getFrame(), "Change Password", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(frameRef.getFrame());
        
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 2));
        dialog.add(p);

        JLabel t = new JLabel("CHANGE PASSWORD", SwingConstants.CENTER);
        t.setBounds(0, 20, 400, 30);
        t.setFont(new Font("Helvetica", Font.BOLD, 18));
        p.add(t);

        JPasswordField curP = new JPasswordField(); 
        JPasswordField newP = new JPasswordField();
        JPasswordField conP = new JPasswordField();

        addDialogField(p, "Current Password", curP, 70);
        addDialogField(p, "New Password", newP, 150);
        addDialogField(p, "Confirm New Password", conP, 230);

        JButton save = createSolidButton("UPDATE", 50, 320, 140, 40);
        save.addActionListener(e -> {
            String cp = new String(curP.getPassword());
            String np = new String(newP.getPassword());
            String cnp = new String(conP.getPassword());

            if (!np.equals(cnp)) {
                CustomDialog.show(dialog, "New passwords do not match.", false);
                return;
            }
            if (np.equals(cp)) {
                CustomDialog.show(dialog, "New password cannot be the same as the current one.", false);
                return;
            }

            if (!np.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                CustomDialog.show(dialog, "Password must be 8+ chars (1 Upper, 1 Lower, 1 Num).", false);
                return;
            }

            try (Connection conn = Database.getConnection()) {
                String checkSql = "SELECT password FROM users WHERE username = ? AND password = ?";
                PreparedStatement checkPst = conn.prepareStatement(checkSql);
                checkPst.setString(1, currentUsername);
                checkPst.setString(2, cp);
                
                if (checkPst.executeQuery().next()) {
                    String updateSql = "UPDATE users SET password = ? WHERE username = ?";
                    PreparedStatement upPst = conn.prepareStatement(updateSql);
                    upPst.setString(1, np);
                    upPst.setString(2, currentUsername);
                    upPst.executeUpdate();
                    dialog.dispose();
                    CustomDialog.show(this, "Password Changed!", true);
                } else {
                    CustomDialog.show(dialog, "Current password incorrect.", false);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    
        JButton cancel = new JButton("CANCEL");
        cancel.setBounds(210, 320, 140, 40);
        cancel.setForeground(Main.TEXT_COLOR);
        cancel.setContentAreaFilled(false);
        cancel.setBorderPainted(false);
        cancel.setFocusable(false);
        cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancel.addActionListener(e -> dialog.dispose());
        
        p.add(save);
        p.add(cancel);
        dialog.setVisible(true);
    }

    private void addDialogField(JPanel p, String label, JPasswordField f, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(50, y, 300, 20);
        f.setBounds(50, y + 25, 300, 35);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        p.add(l);
        p.add(f);
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

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();

        // 1. Update ProfilePanel Preview
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
        imageLabel.setText(""); 

        // 2. Update Sidebar Real-time (using the reference to myFrame)
        if (frameRef != null) {
                frameRef.updateSidebarProfile(path);
            }

        // 3. Save to Database
        saveAvatarToDatabase(selectedFile);
    }
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

    private JButton createSolidButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setBackground(Main.ACCENT_COLOR);
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

    private void saveAvatarToDatabase(File file) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement("UPDATE users SET profile_picture = ? WHERE username = ?");
             FileInputStream fis = new FileInputStream(file)) {
            pst.setBinaryStream(1, fis, (int) file.length());
            pst.setString(2, currentUsername);
            pst.executeUpdate();
        } catch (Exception e) { 
            e.printStackTrace(); }
    }
}

package gui;

import db.Database;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import main.Main;

public class Register extends JFrame {
    private JTextField txtFullName, txtUsername, txtStudentID, txtCourseYear, txtEmail;
    private JPasswordField txtPassword;
    private JButton btnRegister, btnLogin;

    public Register() {
        setTitle("Student Portfolio - Sign Up");
        setSize(600, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setBackground(Main.BG_COLOR);

        int centerX = 100;
        int fieldWidth = 400;

        // --- Title ---
        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setBounds(centerX, 40, fieldWidth, 40); 
        title.setFont(new Font("Helvetica", Font.BOLD, 32));
        title.setForeground(Main.TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // --- Fields ---
        addLabelAndField("Full Name", 110, txtFullName = new JTextField());
        addLabelAndField("Student ID", 190, txtStudentID = new JTextField());
        addLabelAndField("Course & Year", 270, txtCourseYear = new JTextField());
        addLabelAndField("Email Address", 350, txtEmail = new JTextField());
        addLabelAndField("Username", 430, txtUsername = new JTextField());
        
        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(centerX, 510, 100, 20);
        lblPass.setForeground(Main.TEXT_COLOR);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(centerX, 535, fieldWidth, 40);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(Main.TEXT_COLOR);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        add(txtPassword);

        // --- Buttons ---
        btnRegister = new JButton("SIGN UP");
        btnRegister.setBounds(centerX, 600, fieldWidth, 50);
        btnRegister.setBackground(Main.ACCENT_COLOR);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            btnRegister.setBackground(Main.ACCENT_COLOR.darker());
        }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            btnRegister.setBackground(Main.ACCENT_COLOR);
        }
        });
        btnRegister.addActionListener(e -> handleRegistration());
        add(btnRegister);

        btnLogin = new JButton("Already have an account? Login");
        btnLogin.setBounds(centerX, 660, fieldWidth, 30);
        btnLogin.setForeground(Main.TEXT_COLOR);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
        add(btnLogin);

        addWindowControls();
        revalidate();
        repaint();
    }

    private void addLabelAndField(String labelText, int yPos, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setBounds(100, yPos, 200, 20);
        label.setForeground(Main.TEXT_COLOR);
        add(label);

        field.setBounds(100, yPos + 25, 400, 40);
        field.setBackground(Color.WHITE);
        field.setForeground(Main.TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        add(field);
    }

    private void handleRegistration() {
        String fullName = txtFullName.getText();
        String studentID = txtStudentID.getText();
        String courseYear = txtCourseYear.getText();
        String email = txtEmail.getText();
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        // Check for empty fields
        if (fullName.isEmpty() || studentID.isEmpty() || courseYear.isEmpty() || email.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            CustomDialog.show(this, "Please fill in all required fields!");
            return;
        }

        // No spaces allowed in username, password, student ID
        if (user.contains(" ") || pass.contains(" ") || studentID.contains(" ")) {
            CustomDialog.show(this, "Username, Password, and Student ID cannot contain spaces!");
            return;
        }

        // Strong Email Validation (Regex)
        // Ensures format like: something@domain.com
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            CustomDialog.show(this, "Please enter a valid professional email address!");
            return;
        }

        // Student ID Validation (Only numbers and dashes)
        if (!studentID.matches("[0-9-]+")) {
        CustomDialog.show(this, "Student ID should only contain numbers and dashes!");
        return;
        }

        // Password Strength (Minimum 8 characters)
        if (pass.length() < 8) {
            CustomDialog.show(this, "Password must be at least 8 characters long!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO users (full_name, student_id, course_year, email, username, password) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, fullName.trim());
            pst.setString(2, studentID.trim());
            pst.setString(3, courseYear.trim());
            pst.setString(4, email.trim());
            pst.setString(5, user.trim());
            pst.setString(6, pass);

            pst.executeUpdate();
            CustomDialog.show(this, "Registration Successful!");
            new LoginForm().setVisible(true);
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            CustomDialog.show(this, "Error: " + ex.getMessage());
        }
    }

    private void addWindowControls() {
        JLayeredPane lp = this.getLayeredPane();

        Color idleColor = Main.BG_COLOR; 
        Color minHover = new Color(0xD1D8E0); 
        Color closeHover = new Color(0xE74C3C);

        // -- CLOSE BUTTON --
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(545, 10, 45, 30); // Positioned for 600 width
        closeBtn.setBackground(idleColor);
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusable(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));
    
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeBtn.setBackground(closeHover);
                closeBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeBtn.setBackground(idleColor);
                closeBtn.setForeground(Color.BLACK);
            }
        });

        // -- MINIMIZE BUTTON --
        JButton minBtn = new JButton("-");
        minBtn.setBounds(495, 10, 45, 30); // Positioned to the left of Close
        minBtn.setBackground(idleColor);
        minBtn.setForeground(Color.BLACK);
        minBtn.setBorderPainted(false);
        minBtn.setFocusable(false);
        minBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        minBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        
        minBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                minBtn.setBackground(minHover);
                minBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                minBtn.setBackground(idleColor);
                minBtn.setForeground(Color.BLACK);
            }
        });

        lp.add(closeBtn, JLayeredPane.PALETTE_LAYER);
        lp.add(minBtn, JLayeredPane.PALETTE_LAYER);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Register().setVisible(true);
        });
    }
}
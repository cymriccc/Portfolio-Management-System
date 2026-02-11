package gui;

import db.Database;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import main.Main;

public class PasswordRecovery extends JDialog {
    private JTextField txtUser, txtEmail;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;
    private JButton btnVerify, btnReset;
    private JTextField txtAnswer;
    private final int centerX = 40;
    private final int fieldWidth = 370;
    private int sessionAttempts = 0;
    private final int MAX_ATTEMPTS = 3;
    private static java.util.Set<String> blacklistedUsers = new java.util.HashSet<>(); // In-memory blacklist for demonstration

    public PasswordRecovery(JFrame owner) {
        super(owner, "Recover Account", true);
        setSize(450, 480);
        setLocationRelativeTo(owner);
        setLayout(null);
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 2)); 
        getContentPane().setBackground(Main.BG_COLOR);

        JLabel title = new JLabel("RECOVER ACCOUNT");
        title.setBounds(0, 40, 450, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 24));
        title.setForeground(Main.TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // Username or Student ID field 
        JLabel lblUser = new JLabel("Username or Student ID");
        lblUser.setBounds(centerX, 100, 200, 20);
        lblUser.setFont(new Font("Helvetica", Font.BOLD, 12));
        lblUser.setForeground(Main.TEXT_COLOR);
        add(lblUser);

        txtUser = createStyledField();
        txtUser.setBounds(centerX, 125, fieldWidth, 45);
        add(txtUser);

        // Email Field 
        JLabel lblEmail = new JLabel("Registered Email");
        lblEmail.setBounds(centerX, 185, 150, 20);
        lblEmail.setFont(new Font("Helvetica", Font.BOLD, 12));
        lblEmail.setForeground(Main.TEXT_COLOR);
        add(lblEmail);

        txtEmail = createStyledField();
        txtEmail.setBounds(centerX, 210, fieldWidth, 45);
        add(txtEmail);

        // Buttons
        btnVerify = new JButton("VERIFY ACCOUNT");
        btnVerify.setBounds(centerX, 290, fieldWidth, 45);
        btnVerify.setBackground(Main.ACCENT_COLOR);
        btnVerify.setForeground(Color.WHITE);
        btnVerify.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnVerify.setFocusPainted(false);
        btnVerify.setBorderPainted(false);
        btnVerify.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerify.addActionListener(e -> handleVerification());
        add(btnVerify);

        JButton btnClose = new JButton("Back to Login");
        btnClose.setBounds(centerX, 350, fieldWidth, 30);
        btnClose.setFont(new Font("Helvetica", Font.BOLD, 12));
        btnClose.setForeground(Main.TEXT_COLOR);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        add(btnClose);

    }

    // Helper to create consistently styled text fields
    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            new EmptyBorder(0, 15, 0, 15) 
        ));
        field.setFont(new Font("Helvetica", Font.PLAIN, 14));
        return field; 
    }

    // Core logic for verifying user identity through database lookup and security questions
    private void handleVerification() {
        String identifier = txtUser.getText().trim();
        String email = txtEmail.getText().trim();

        if (identifier.isEmpty() || email.isEmpty()) {
            CustomDialog.show(this, "Please fill in all fields.", false);
            return;
        }

        if (blacklistedUsers.contains(identifier.toLowerCase())) {
            CustomDialog.show(this, "This account is temporarily locked.", false);
            return;
        }

        String sql = "SELECT username, q1, a1, q2, a2 FROM users " +
             "WHERE (BINARY username = ? OR BINARY student_id = ?) AND BINARY email = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, identifier);
            pst.setString(2, identifier);
            pst.setString(3, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String actualUsername = rs.getString("username");

                List<String[]> questions = new ArrayList<>();
                questions.add(new String[]{rs.getString("q1"), rs.getString("a1")});
                questions.add(new String[]{rs.getString("q2"), rs.getString("a2")});
                Collections.shuffle(questions);
                showSecurityChallenge(actualUsername, questions, 0);

            } else {
                CustomDialog.show(this, "Invalid account details provided.", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSecurityChallenge(String user, List<String[]> questions, int index) {
        getContentPane().removeAll();
        String currentQuestion = questions.get(index)[0];
        String correctAnswer = questions.get(index)[1];
        int step = index + 1;

        JLabel title = new JLabel("SECURITY STEP " + step + "/2");
        title.setBounds(0, 40, 450, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 22));
        title.setForeground(Main.TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        JLabel lblQ = new JLabel("<html><center>" + currentQuestion + "</center></html>");
        lblQ.setBounds(centerX, 100, fieldWidth, 60);
        lblQ.setForeground(Main.TEXT_COLOR);
        lblQ.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblQ);

        txtAnswer = createStyledField();
        txtAnswer.setBounds(centerX, 170, fieldWidth, 45);
        add(txtAnswer);

        int btnWidth = (fieldWidth - 10) / 2; 

        JButton btnCancel = new JButton("CANCEL");
        btnCancel.setBounds(centerX, 240, btnWidth, 45);
        btnCancel.setBackground(new Color(0x7f8c8d));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Helvetica", Font.BOLD, 12));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose()); 
        add(btnCancel);

        btnVerify = new JButton(step == 1 ? "NEXT" : "FINISH"); // Change text based on step
        btnVerify.setBounds(centerX + btnWidth + 10, 240, btnWidth, 45);
        btnVerify.setBackground(Main.ACCENT_COLOR);
        btnVerify.setForeground(Color.WHITE);
        btnVerify.setFont(new Font("Helvetica", Font.BOLD, 12));
        btnVerify.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnVerify.addActionListener(e -> {
            String input = txtAnswer.getText().trim().toLowerCase();
            String actual = correctAnswer.trim().toLowerCase();

            if (input.equals(actual)) {
                if (step == 1) {
                    showSecurityChallenge(user, questions, 1);
                } else {
                    showResetUI(user);
                }
            } else {
                sessionAttempts++;
                int remaining = MAX_ATTEMPTS - sessionAttempts;
                
                if (sessionAttempts >= MAX_ATTEMPTS) {
                    CustomDialog.show(this, "Too many failures. Security lockout initiated.", false);
                    blacklistedUsers.add(user.toLowerCase());
                    dispose(); 
                } else {
                    CustomDialog.show(this, "Incorrect answer. " + remaining + " attempts left.", false);
                    txtAnswer.setText("");
                    txtAnswer.requestFocus();
                }
            }
        });
        add(btnVerify);

        repaint();
        revalidate();
    }

    // UI for resetting the password after successful verification
    private void showResetUI(String username) {
        getContentPane().removeAll();
        JLabel title = new JLabel("RESET PASSWORD");
        title.setBounds(0, 40, 450, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 24));
        title.setForeground(Main.TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        JLabel lblPass = new JLabel("New Password");
        lblPass.setBounds(centerX, 85, 150, 20);
        lblPass.setForeground(Main.TEXT_COLOR);
        add(lblPass);

        txtNewPass = createStyledPasswordField();
        txtNewPass.setBounds(centerX, 110, fieldWidth, 45);
        add(txtNewPass);

        JLabel lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setBounds(centerX, 170, 150, 20);
        lblConfirm.setForeground(Main.TEXT_COLOR);
        add(lblConfirm);

        txtConfirmPass = createStyledPasswordField();
        txtConfirmPass.setBounds(centerX, 195, fieldWidth, 45);
        add(txtConfirmPass);

        btnReset = new JButton("UPDATE PASSWORD");
        btnReset.setBounds(centerX, 270, fieldWidth, 45);
        btnReset.setBackground(new Color(0x27ae60));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Helvetica", Font.BOLD, 14));
        
        btnReset.addActionListener(e -> {
            String newPass = new String(txtNewPass.getPassword());
            String confirmPass = new String(txtConfirmPass.getPassword());

            if (!newPass.equals(confirmPass)) {
            CustomDialog.show(this, "Passwords do not match!", false);
            return;
        }
            if (!newPass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            CustomDialog.show(this, "Must be 8+ chars with Upper, Lower, and Number.", false);
            return;
        }

        updatePassword(username, newPass);
        });
        add(btnReset);

        repaint();
        revalidate();
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            new EmptyBorder(0, 15, 0, 15)
        ));
        return field;
    }

    private void updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, newPassword);
            pst.setString(2, username);
            pst.executeUpdate();
            CustomDialog.show(this, "Password updated. Please log in.", true);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package gui;

import db.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import main.Main;

public class PasswordRecovery extends JDialog {
    private JTextField txtUser, txtEmail;
    private JPasswordField txtNewPass, txtConfirmPass;
    private JButton btnVerify, btnReset;
    private JTextField txtAnswer;

    private int centerX = 40;
    private int fieldWidth = 370;

    public PasswordRecovery(JFrame owner) {
        super(owner, "Recover Account", true);
        setSize(450, 480);
        setLocationRelativeTo(owner);
        setLayout(null);
        setUndecorated(true);
        getContentPane().setBackground(Main.BG_COLOR);

        JLabel title = new JLabel("RECOVER ACCOUNT");
        title.setBounds(0, 40, 450, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 24));
        title.setForeground(Main.TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // --- Username Field ---
        JLabel lblUser = new JLabel("Username");
        lblUser.setBounds(centerX, 100, 100, 20);
        lblUser.setFont(new Font("Helvetica", Font.BOLD, 12));
        lblUser.setForeground(Main.TEXT_COLOR);
        add(lblUser);

        txtUser = createStyledField();
        txtUser.setBounds(centerX, 125, fieldWidth, 45);
        add(txtUser);

        // --- Email Field ---
        JLabel lblEmail = new JLabel("Registered Email");
        lblEmail.setBounds(centerX, 185, 150, 20);
        lblEmail.setFont(new Font("Helvetica", Font.BOLD, 12));
        lblEmail.setForeground(Main.TEXT_COLOR);
        add(lblEmail);

        txtEmail = createStyledField();
        txtEmail.setBounds(centerX, 210, fieldWidth, 45);
        add(txtEmail);

        // --- Buttons ---
        btnVerify = new JButton("VERIFY ACCOUNT");
        btnVerify.setBounds(centerX, 290, fieldWidth, 45);
        btnVerify.setBackground(Main.ACCENT_COLOR);
        btnVerify.setForeground(Color.WHITE);
        btnVerify.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnVerify.setFocusPainted(false);
        btnVerify.setBorderPainted(false);
        btnVerify.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnVerify);

        JButton btnClose = new JButton("Back to Login");
        btnClose.setBounds(centerX, 350, fieldWidth, 30);
        btnClose.setFont(new Font("Helvetica", Font.PLAIN, 12));
        btnClose.setForeground(Main.TEXT_COLOR);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        add(btnClose);

        btnVerify.addActionListener(e -> handleVerification());
    }

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

    // Verify Account and Fetch Questions
    private void handleVerification() {
        String user = txtUser.getText().trim();
        String email = txtEmail.getText().trim();

        if (user.isEmpty() || email.isEmpty()) {
            CustomDialog.show(this, "Please fill in all fields.", false);
            return;
        }

        // --- STEP 1: Verify the user exists first ---
        String sql = "SELECT q1, a1, q2, a2, lockout_time FROM users WHERE username = ? AND email = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, user);
            pst.setString(2, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                
                Timestamp lockout = rs.getTimestamp("lockout_time");
                if (lockout != null) {
                    long diff = System.currentTimeMillis() - lockout.getTime();
                    long oneHourInMillis = 60 * 60 * 1000;

                    if (diff < oneHourInMillis) {
                        long remainingMins = (oneHourInMillis - diff) / (60 * 1000);
                        CustomDialog.show(this, "ACCOUNT LOCKED. Try again in " + remainingMins + " mins.", false);
                        txtUser.setEnabled(false);
                        txtEmail.setEnabled(false);
                        btnVerify.setEnabled(false);
                        btnVerify.setText("LOCKED");
                        btnVerify.setBackground(Color.GRAY);
                        return;

                    } else {
                        // Time has passed, clear it so they can try again
                        resetAttempts(user);
                    }
                }
                
                // If not locked, proceed to questions
                String q1 = rs.getString("q1");
                String a1 = rs.getString("a1");
                String q2 = rs.getString("q2");
                String a2 = rs.getString("a2");
                showSecurityChallenge(user, q1, a1, q2, a2, 1);
                
            } else {
                CustomDialog.show(this, "Account details not found.", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSecurityChallenge(String user, String q1, String a1, String q2, String a2, int step) {
        // 1. Check for existing lockout in Database
        if (isUserLockedOut(user)) {
            CustomDialog.show(this, "Account locked. Please try again after 24 hours.", false);
            dispose();
            return;
        }

        getContentPane().removeAll();
        String currentQuestion = (step == 1) ? q1 : q2;
        String correctAnswer = (step == 1) ? a1 : a2;

        JLabel title = new JLabel("VERIFICATION STEP " + step + "/2");
        title.setBounds(0, 40, 450, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        JLabel lblQ = new JLabel("<html><center>" + currentQuestion + "</center></html>");
        lblQ.setBounds(centerX, 100, fieldWidth, 60);
        lblQ.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblQ);

        txtAnswer = createStyledField();
        txtAnswer.setBounds(centerX, 170, fieldWidth, 45);
        add(txtAnswer);

        btnVerify = new JButton(step == 1 ? "NEXT QUESTION" : "VERIFY & PROCEED");
        btnVerify.setBounds(centerX, 240, fieldWidth, 45);
        btnVerify.setBackground(Main.ACCENT_COLOR);
        btnVerify.setForeground(Color.WHITE);
        
        btnVerify.addActionListener(e -> {
            if (txtAnswer.getText().trim().equalsIgnoreCase(correctAnswer)) {
                if (step == 1) {
                    showSecurityChallenge(user, q1, a1, q2, a2, 2);
                } else {
                    resetAttempts(user); // Reset counter on success
                    showResetUI(user);
                }
            } else {
                handleFailedAttempt(user);
            }
        });
        add(btnVerify);

        repaint();
        revalidate();
    }

    // --- Lockout Logic Methods ---

    private boolean isUserLockedOut(String username) {
        String sql = "SELECT lockout_time FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Timestamp lockout = rs.getTimestamp("lockout_time");
                if (lockout != null) {
                    long diff = System.currentTimeMillis() - lockout.getTime();
                    long oneHourInMillis = 60 * 60 * 1000; // 1 Hour

                    if (diff < oneHourInMillis) {
                        long remainingMins = (oneHourInMillis - diff) / (60 * 1000);
                        String timeMsg = (remainingMins > 0) ? remainingMins + " minutes" : "less than a minute";
                        CustomDialog.show(this, "Account locked. Try again in " + timeMsg + ".", false);
                        return true; 
                    } else {
                        // Time passed! Reset and let them in
                        resetAttempts(username);
                        return false;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private void handleFailedAttempt(String username) {
        String sql = "UPDATE users SET recovery_attempts = recovery_attempts + 1 WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.executeUpdate();
            int rowsUpdated = pst.executeUpdate();
            
            if (rowsUpdated > 0) {
                // Now check if they hit the limit
                checkLockoutStatus(username);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
    private void setLockout(String username) {
        String sql = "UPDATE users SET lockout_time = NOW() WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.executeUpdate();
            System.out.println("Lockout timestamp set for user: " + username);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void checkLockoutStatus(String username) {
        String sql = "SELECT recovery_attempts FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int attempts = rs.getInt("recovery_attempts");
                if (attempts >= 3) {
                    setLockout(username);
                    CustomDialog.show(this, "Too many failed attempts. Locked for 1 hour.", false);
                    dispose(); // Close the recovery window entirely
                } else {
                    CustomDialog.show(this, "Incorrect. " + (3 - attempts) + " attempts remaining.", false);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void resetAttempts(String username) {
        String sql = "UPDATE users SET recovery_attempts = 0, lockout_time = NULL WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 3. Step 3: Reset UI with Added Validation
    private void showResetUI(String username) {
        getContentPane().removeAll();
        
        JLabel title = new JLabel("SET NEW PASSWORD");
        title.setBounds(0, 40, 450, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        JLabel lblPass = new JLabel("New Password");
        lblPass.setBounds(centerX, 120, 150, 20);
        add(lblPass);

        txtNewPass = new JPasswordField();
        txtNewPass.setBounds(centerX, 145, fieldWidth, 45);
        txtNewPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            new EmptyBorder(0, 15, 0, 15)
        ));
        add(txtNewPass);

        JLabel passGuide = new JLabel("Min 8 chars, 1 Upper, 1 Lower, 1 Number");
        passGuide.setFont(new Font("Helvetica", Font.PLAIN, 10));
        passGuide.setForeground(Color.GRAY);
        passGuide.setBounds(centerX, 195, fieldWidth, 15);
        add(passGuide);

        btnReset = new JButton("UPDATE PASSWORD");
        btnReset.setBounds(centerX, 230, fieldWidth, 45);
        btnReset.setBackground(new Color(0x27ae60));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.setBorderPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnReset.addActionListener(e -> {
            String newPass = new String(txtNewPass.getPassword());
            
            // Reusing your exact regex from Register.java
            if (!newPass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                CustomDialog.show(this, "Password must have 8 chars, 1 Upper, 1 Lower, and 1 Number.", false);
                return;
            }
            updatePassword(username, newPass);
        });
        add(btnReset);

        repaint();
        revalidate();
    }

    private void updatePassword(String username, String newPassword) {
        if (newPassword.isEmpty()) {
            CustomDialog.show(this, "Password cannot be empty.", false);
            return;
        }
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, newPassword);
            pst.setString(2, username);
            pst.executeUpdate();
            CustomDialog.show(this, "Password reset successfully!", true);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
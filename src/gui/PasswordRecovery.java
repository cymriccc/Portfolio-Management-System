package gui;

import db.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import main.Main;

public class PasswordRecovery extends JDialog {
    private JTextField txtUser, txtEmail;
    private JPasswordField txtNewPass;
    private JButton btnVerify, btnReset;
    private int centerX = 40;
    private int fieldWidth = 370;

    public PasswordRecovery(JFrame owner) {
        super(owner, "Recover Account", true);
        setSize(450, 450);
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

    // Helper to create that consistent "Login Tab" look with padding
    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            new EmptyBorder(0, 15, 0, 15) // This is the padding you wanted!
        ));
        field.setFont(new Font("Helvetica", Font.PLAIN, 14));
        return field;
    }

    private void handleVerification() {
        String user = txtUser.getText();
        String email = txtEmail.getText();

        String sql = "SELECT * FROM users WHERE username = ? AND email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, user);
            pst.setString(2, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                showResetUI(user);
            } else {
                CustomDialog.show(this, "Account details not found.", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResetUI(String username) {
        getContentPane().removeAll();
        
        JLabel title = new JLabel("RESET PASSWORD");
        title.setBounds(0, 40, 450, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        JLabel lblPass = new JLabel("New Password");
        lblPass.setBounds(centerX, 120, 150, 20);
        add(lblPass);

        txtNewPass = new JPasswordField();
        txtNewPass.setBounds(centerX, 145, fieldWidth, 45);
        txtNewPass.setBackground(Color.WHITE);
        txtNewPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            new EmptyBorder(0, 15, 0, 15)
        ));
        add(txtNewPass);

        btnReset = new JButton("UPDATE PASSWORD");
        btnReset.setBounds(centerX, 220, fieldWidth, 45);
        btnReset.setBackground(new Color(0x27ae60));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Helvetica", Font.BOLD, 14));
        add(btnReset);

        btnReset.addActionListener(e -> updatePassword(username, new String(txtNewPass.getPassword())));
        
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
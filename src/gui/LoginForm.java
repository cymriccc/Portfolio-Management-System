package gui;

import db.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import main.Main;

public class LoginForm extends JFrame {
    private JTextField txtUsername; 
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    private int loginAttempts = 0;
    private final int MAX_ATTEMPTS = 5;

    public LoginForm() {
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setUndecorated(true);
        getContentPane().setBackground(Main.BG_COLOR);

        int centerX = 100; 
        int fieldWidth = 400;

        JLabel title = new JLabel("WELCOME BACK");
        title.setBounds(centerX, 100, fieldWidth, 50);
        title.setFont(new Font("Helvetica", Font.BOLD, 36));
        title.setForeground(Main.TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title); 

        // Username Field
        JLabel lblUser = new JLabel("Username");
        lblUser.setBounds(centerX, 180, 100, 20);
        lblUser.setForeground(Main.TEXT_COLOR);
        add(lblUser);

        txtUsername = new JTextField(); 
        txtUsername.setBounds(centerX, 205, fieldWidth, 45);
        txtUsername.setBackground(Color.WHITE);
        txtUsername.setForeground(Main.TEXT_COLOR);
        txtUsername.setBorder(BorderFactory.createCompoundBorder( 
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(txtUsername);

        // Password Field
        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(centerX, 270, 100, 20);
        lblPass.setForeground(Main.TEXT_COLOR);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(centerX, 295, fieldWidth, 45);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(Main.TEXT_COLOR);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(txtPassword);

        // Login Button
        btnLogin = new JButton("SIGN IN");
        btnLogin.setBounds(centerX, 380, fieldWidth, 55);
        btnLogin.setBackground(Main.ACCENT_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            btnLogin.setBackground(Main.ACCENT_COLOR.darker());
        }   
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            btnLogin.setBackground(Main.ACCENT_COLOR);
        }
        });
        add(btnLogin);

        // Forgot Password Button
        JButton btnForgot = new JButton("Forgot Password?");
        btnForgot.setBounds(centerX, 480, fieldWidth, 20); 
        btnForgot.setForeground(Main.TEXT_COLOR);
        btnForgot.setContentAreaFilled(false);
        btnForgot.setBorderPainted(false);
        btnForgot.setFocusPainted(false);
        btnForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnForgot.addActionListener(e -> {
            new PasswordRecovery(this).setVisible(true);
        });

        add(btnForgot);

        // Register Button
        btnRegister = new JButton("No account? Create one");
        btnRegister.setBounds(centerX, 450, fieldWidth, 30);
        btnRegister.setForeground(Main.TEXT_COLOR);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnRegister);

        btnLogin.addActionListener(e -> login());
        btnRegister.addActionListener(e -> {
            new Register().setVisible(true);
            dispose();
        });

        addWindowControls();
    }

    private void login() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword()); 

        if (user.isEmpty() || pass.isEmpty()) {
            CustomDialog.show(this, "Please enter both username and password.", false);
            return;
        }

        // Case-sensitive SQL query
        String sql = "SELECT id, role, full_name, course_year FROM users " +
                 "WHERE BINARY username = ? AND BINARY password = ?";

        // Database verification
        try (Connection conn = Database.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, user);
            pst.setString(2, pass);
  
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                loginAttempts = 0;

                int id = rs.getInt("id");
                String role = rs.getString("role");
                String name = rs.getString("full_name");
                String course = rs.getString("course_year");
                String actualUsername = user;
                
                CustomDialog.show(this, "✓ Welcome, " + name + "!", true);

                if ("admin".equalsIgnoreCase(role)) {
                    new AdminDashboard().setVisible(true);
                } else {
                    myFrame dashboardFrame = new myFrame();
                    dashboardFrame.loadExistingAvatar(actualUsername);
                    
                    // Passes user info to the dashboard for personalized content
                    new MainContent(dashboardFrame, name, course, actualUsername, id);
                    new Menu(dashboardFrame);
                    new frameDisplay(dashboardFrame);
                }
                this.dispose(); 

            } else {
               loginAttempts++;
                int remaining = MAX_ATTEMPTS - loginAttempts;

                if (loginAttempts >= MAX_ATTEMPTS) {
                    CustomDialog.show(this, "Too many failed attempts. Login Disabled.", false);
                    btnLogin.setEnabled(false); 
                    txtUsername.setEnabled(false);
                    txtPassword.setEnabled(false);
                } else {
                    CustomDialog.show(this, "✕ Invalid credentials! " + remaining + " attempts left.", false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show(this, "Database Error: " + e.getMessage(), false);
        }
    }

    // Custom window controls
    private void addWindowControls() {
        JLayeredPane lp = this.getLayeredPane();

        Color idleColor = Main.BG_COLOR; 
        Color minHover = new Color(0xD1D8E0); 
        Color closeHover = new Color(0xE74C3C);

        // Close Button
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(545, 10, 45, 30); 
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

        // Minimize Button
        JButton minBtn = new JButton("-");
        minBtn.setBounds(495, 10, 45, 30); 
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
}
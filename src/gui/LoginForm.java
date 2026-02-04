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

        // --- Username Field ---
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

        // --- Password Field ---
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

    // Login logic
    private void login() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        String sql = "SELECT id, role, full_name, course_year FROM users WHERE username = ? AND password = ?";

        // Database verification
        try (Connection conn = Database.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, user);
            pst.setString(2, pass);
  
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                String name = rs.getString("full_name");
                String course = rs.getString("course_year");
                String actualUsername = user;
                
                CustomDialog.show(this, "✓ Welcome, " + name + "!", true);

                if ("admin".equalsIgnoreCase(role)) {
                    // Open Admin Dashboard
                    new AdminDashboard().setVisible(true);
                } else {
                    // Open standard Student Dashboard system
                    myFrame dashboardFrame = new myFrame();
                    new MainContent(dashboardFrame, name, course, actualUsername, id);
                    new Menu(dashboardFrame);
                    new frameDisplay(dashboardFrame);
                }
                this.dispose(); // Close login window
            } else {
                CustomDialog.show(this, "✕ Invalid credentials!", false);
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
}
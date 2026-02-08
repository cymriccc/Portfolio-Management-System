package gui;

import db.Database;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import main.Main;

public class myFrame {
    JFrame frame = new JFrame("Student Portfolio System");
    JPanel menu = new JPanel();
    JPanel container = new JPanel();
    CardLayout cardLayout = new CardLayout();

    public JLabel sidebarProfileImg = new JLabel("", SwingConstants.CENTER);

    public myFrame() {
        frame.setSize(1366, 728);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setUndecorated(true);

        // Sidebar (Menu)
        menu.setBounds(0, 0, 350, 728); 
        menu.setBackground(Main.DARK_PANEL); 
        menu.setLayout(null);

        sidebarProfileImg.setBounds(25, 630, 60, 60); 
        sidebarProfileImg.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 1));
        sidebarProfileImg.setOpaque(true);
        sidebarProfileImg.setBackground(new Color(0, 0, 0, 50));
        menu.add(sidebarProfileImg);

        // Content Area (Switcher)
        container.setLayout(cardLayout); 
        container.setBounds(350, 0, 1016, 728);

        frame.add(menu);
        frame.add(container);
    }

    public void loadExistingAvatar(String username) {
        String sql = "SELECT profile_picture FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            byte[] imgBytes = rs.getBytes("profile_picture");
            if (imgBytes != null && imgBytes.length > 0) {
                // Image found - set icon and REMOVE text
                ImageIcon icon = new ImageIcon(imgBytes);
                Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                sidebarProfileImg.setIcon(new ImageIcon(img));
                sidebarProfileImg.setText("");
            } else {
                // No image found - show the "?"
                sidebarProfileImg.setIcon(null);
                sidebarProfileImg.setText("?");
                sidebarProfileImg.setForeground(Color.WHITE);
                sidebarProfileImg.setFont(new Font("Helvetica", Font.BOLD, 20));
            }
        }
            sidebarProfileImg.revalidate();
            sidebarProfileImg.repaint();

    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    public void updateSidebarProfile(String path) { 
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        sidebarProfileImg.setIcon(new ImageIcon(img));
        sidebarProfileImg.setText("");
    }
    
    public JFrame getFrame() {
        return frame;
    }
    public JPanel getMenu() {
        return menu;

    }
    public JPanel getContainer() {
        return container;

    }
    public CardLayout getCardLayout() {
        return cardLayout;

    }
}

package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import main.Main;

public class SettingsPanel extends JPanel {
    public SettingsPanel() {
        setLayout(null);
        setBackground(Main.BG_COLOR);

        JLabel title = new JLabel("Settings (Developers)");
        title.setBounds(40, 40, 400, 50);
        title.setFont(new Font("Helvetica", Font.BOLD, 28));
        title.setForeground(Main.TEXT_COLOR);
        add(title);

        JLabel badge = new JLabel("STUDENT PORTFOLIO SYSTEM BY SANA-OL", SwingConstants.CENTER);
        badge.setBounds(40, 85, 250, 25);
        badge.setOpaque(true); // to show background color
        badge.setBackground(Main.ACCENT_COLOR);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("Helvetica", Font.BOLD, 10));
        add(badge);

        JLabel subTitle = new JLabel("MEET THE DEVELOPERS");
        subTitle.setBounds(40, 150, 400, 30);
        subTitle.setForeground(Main.TEXT_COLOR);
        subTitle.setFont(new Font("Helvetica", Font.BOLD, 16));
        add(subTitle);

        int startX = 40;
        int startY = 200;
        int cardWidth = 460;
        int cardHeight = 200;   
        int padding = 10;

        // developers with descriptions
        add(createMemberCard("Main Coder", "Carlo Sebastian Dingle", 
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", startX, startY, cardWidth, cardHeight));
        add(createMemberCard("Assistant Coder", "Kristine Joice Borres", 
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", startX + cardWidth + padding, startY, cardWidth, cardHeight));
            
        add(createMemberCard("Assistant Coder", "Lianne Jhulzen Guerrero", 
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", startX, startY + cardHeight + padding, cardWidth, cardHeight));
            
        add(createMemberCard("Documentation", "Chelsie Chavez", 
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", startX + cardWidth + padding, startY + cardHeight + padding, cardWidth, cardHeight));

        JLabel footer = new JLabel("© 2026 Student Portfolio System | Developed by SANA-OL for Academic Purposes");
        footer.setBounds(0, 660, 1016, 30);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setForeground(new Color(0x636E72));
        footer.setFont(new Font("Helvetica", Font.ITALIC, 12));
        add(footer);
    }
    
    private JPanel createMemberCard(String role, String name, String desc, int x, int y, int w, int h) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(x, y, w, h);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 1));

        // 1. Role Label 
        JLabel roleLabel = new JLabel(role.toUpperCase());
        roleLabel.setBounds(25, 15, 300, 20);
        roleLabel.setForeground(Main.ACCENT_COLOR);
        roleLabel.setFont(new Font("Verdana", Font.BOLD, 10)); 
        card.add(roleLabel);

        // 2. Name Label 
        JLabel nameLabel = new JLabel(name);
        nameLabel.setBounds(25, 35, 400, 35);
        nameLabel.setForeground(Main.TEXT_COLOR);
        nameLabel.setFont(new Font("Helvetica", Font.BOLD, 22));
        card.add(nameLabel);

        // 3. Description Label 
        JLabel descLabel = new JLabel("<html><body style='width: 300PX;'>" +
                                      "<p style='line-height: 1.4;'>" + desc + "</p>" +
                                      "</body></html>");
        descLabel.setBounds(25, 75, 390, 100);
        descLabel.setForeground(new Color(0x636E72));
        descLabel.setFont(new Font("SansSerif", Font.ITALIC, 13)); 
        descLabel.setVerticalAlignment(SwingConstants.TOP);
        card.add(descLabel);

        // Hover Effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(0xF1F2F6));
                card.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 1));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 1));
            }
        });

        return card;
    }
}

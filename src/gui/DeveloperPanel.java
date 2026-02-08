package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import main.Main;

public class DeveloperPanel extends JPanel {
    public DeveloperPanel() {
        setLayout(null);
        setBackground(Main.BG_COLOR);

        JLabel title = new JLabel("Developers");
        title.setBounds(40, 40, 400, 50);
        title.setFont(new Font("Helvetica", Font.BOLD, 28));
        title.setForeground(Main.TEXT_COLOR);
        add(title);

        JLabel badge = new JLabel("PORTFOLIO MANAGEMENT SYSTEM BY VANTAGE", SwingConstants.CENTER);
        badge.setBounds(40, 85, 320, 25);
        badge.setOpaque(true);
        badge.setBackground(Main.ACCENT_COLOR);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("Helvetica", Font.BOLD, 10));
        badge.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
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
            "Mainly focused on coding the system’s panels and windows. This included creating the user interface for the Sign-In and Sign-Up windows, developing the entire Admin window along with all its included panels, and starting the main system window. The dashboard was also implemented with features such as the total projects display, graphs, and welcome greetings connected to the database. The user interface for the My Portfolio section was created.", startX, startY, cardWidth, cardHeight));
        add(createMemberCard("Assistant Coder", "Kristine Joice Borres", 
            "Improved the Sign-In and Sign-Up modules by adding proper validations and user-friendly guides. Also included developing the “Forgot Password” feature with security questions for secure account recovery. In addition, covered the development of the Discovery module and the Developers Panel, as well as optimizing the user profile system to ensure accurate validation and reliable database updates.", startX + cardWidth + padding, startY, cardWidth, cardHeight));
            
        add(createMemberCard("Assistant Coder", "Lianne Jhulzen Guerrero", 
            "Focused on creating the database used for the login and sign-up system, designing the initial user interface for both forms, and assisting in writing the project documentation. This included setting up how user data is stored, building the basic layout and structure of the login and registration pages, and helping document the system features and processes for better team reference and future improvements.", startX, startY + cardHeight + padding, cardWidth, cardHeight));
            
        add(createMemberCard("Documentation", "Chelsie Chavez", 
            "Focused on creating the progress report and other required project documents, with particular emphasis on writing and completing Chapter 1 for the system documentation. This included organizing project details, clearly presenting the system overview, and ensuring all documentation requirements were properly met.", startX + cardWidth + padding, startY + cardHeight + padding, cardWidth, cardHeight));

        JLabel footer = new JLabel("© 2026 Student Portfolio System | Developed by Vantage for Academic Purposes");
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
        roleLabel.setFont(new Font("Verdana", Font.BOLD, 11)); 
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
        descLabel.setFont(new Font("SansSerif", Font.ITALIC, 11)); 
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

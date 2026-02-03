package gui;

import java.awt.*;
import javax.swing.*;
import main.Main;

public class MainContent {
    private DashboardPanel dashboard;

    public MainContent(myFrame frameObject, String studentName, String courseYear, String username, int userId) {
        JPanel container = frameObject.getContainer();

        dashboard = new DashboardPanel(studentName, courseYear);

        container.add(dashboard, "DASHBOARD");
        container.add(new PortfolioPanel(userId), "PORTFOLIO");
        container.add(new DiscoverPanel(), "DISCOVERY");
        container.add(new SettingsPanel(), "SETTINGS");
        container.add(new ProfilePanel(frameObject, username, dashboard), "PROFILE");

        addWindowControls(frameObject);
    }

    private void addWindowControls(myFrame frameObject) {
        JFrame actualFrame = frameObject.getFrame();
        JLayeredPane lp = actualFrame.getLayeredPane();

        Color idleColor = Main.BG_COLOR; 
        Color minHover = new Color(0xD1D8E0); 
        Color closeHover = new Color(0xE74C3C);

        // Add Minimize and Close Buttons

        // -- CLOSE BUTTON --
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(1310, 10, 45, 30);
        closeBtn.setBackground(idleColor);
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusable(false);
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
        minBtn.setBounds(1260, 10, 45, 30);
        minBtn.setBackground(idleColor);
        minBtn.setBorderPainted(false);
        minBtn.setForeground(Color.BLACK);
        minBtn.setFocusable(false);
        minBtn.addActionListener(e -> actualFrame.setState(Frame.ICONIFIED));
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

        // Add buttons to layered pane
        lp.add(closeBtn, JLayeredPane.PALETTE_LAYER);
        lp.add(minBtn, JLayeredPane.PALETTE_LAYER);
    }
}
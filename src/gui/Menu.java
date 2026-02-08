package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import main.Main;

public class Menu {
    private JPanel activeIndicator;

    public Menu(myFrame frameObject) {
        JPanel menuPanel = frameObject.getMenu();
    
        // --- LOGO IMAGE ---
        try {
            ImageIcon logoIcon = new ImageIcon("logo.png");
            int logoSize = 250; 
            int menuWidth = 350;
            int xCentered = (menuWidth - logoSize) / 2;

            Image image = logoIcon.getImage().getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(image);

            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setBounds(xCentered, -20, logoSize, logoSize); 
            menuPanel.add(logoLabel);
            
        } catch (Exception e) {
            System.out.println("Logo file not found. Check your project folder.");
        }

        // --- TITLE LABEL ---
        JLabel titleLabel = new JLabel("Portfolio Management");
        titleLabel.setBounds(0, 180, 350, 40);
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        menuPanel.add(titleLabel);

        // --- SUBTITLE LABEL ---
        JLabel subtitleLabel = new JLabel("BY VANTAGE");
        subtitleLabel.setBounds(0, 210, 350, 20);
        subtitleLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(0xBDC3C7));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        menuPanel.add(subtitleLabel);

        // --- ACTIVE INDICATOR ---
        activeIndicator = new JPanel();
        // Centered vertically on the first button (Y: 240 + 5px offset)
        activeIndicator.setBounds(0, 245, 5, 40); 
        activeIndicator.setBackground(new Color(0x575FCF)); 
        menuPanel.add(activeIndicator);

        // --- NAVIGATION BUTTONS ---
        // 1. Create the buttons and store them in variables
        JButton dashBtn = createMenuButton("Dashboard", 240, frameObject, "DASHBOARD");
        JButton portBtn = createMenuButton("My Portfolio", 300, frameObject, "PORTFOLIO");
        JButton discoveryBtn = createMenuButton("Discovery", 360, frameObject, "DISCOVERY");
        JButton developBtn = createMenuButton("Developers", 420, frameObject, "DEVELOPERS");
        
        // 2. Add them to the sidebar
        menuPanel.add(dashBtn);
        menuPanel.add(portBtn);
        menuPanel.add(discoveryBtn);
        menuPanel.add(developBtn);

        // 3. Force the Dashboard button to be "Active" on startup
        dashBtn.setBounds(0, 240, 350, 50); // Match the new Y position
        dashBtn.setForeground(new Color(0x575FCF)); 
        dashBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(0x575FCF)),
            BorderFactory.createEmptyBorder(0, 35, 0, 0)
        ));

        // BOTTOM PROFILE SECTION 
        JPanel bottomSection = new JPanel();
        bottomSection.setBounds(0, 528, 350, 200); 
        bottomSection.setBackground(new Color(0x252b2b)); 
        bottomSection.setLayout(null);

        // Tiny Profile Preview in Sidebar
        frameObject.sidebarProfileImg.setBounds(30, 40, 60, 60);
        frameObject.sidebarProfileImg.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 2));
        bottomSection.add(frameObject.sidebarProfileImg);

        // My Profile Button 
        JButton profileBtn = new JButton("My Profile");
        profileBtn.setBounds(100, 40, 250, 60); // Adjusted width to fit section
        profileBtn.setBackground(new Color(0x252b2b)); 
        profileBtn.setForeground(new Color(0xECF0F1));
        profileBtn.setFont(new Font("Helvetica", Font.BOLD, 16));
        profileBtn.setHorizontalAlignment(SwingConstants.LEFT);
        profileBtn.setMargin(new Insets(0, 50, 0, 0));
        profileBtn.setBorderPainted(false);
        profileBtn.setFocusPainted(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Navigation Logic
        profileBtn.addActionListener(e -> {
            frameObject.getCardLayout().show(frameObject.getContainer(), "PROFILE");
            resetButtonStyles(menuPanel); 
            activeIndicator.setVisible(false);
            profileBtn.setBackground(new Color(0x575FCF)); 
            profileBtn.setForeground(Color.WHITE);
        });
        
        // Hover Logic for Profile Button
        profileBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                profileBtn.setBackground(Main.ACCENT_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                if (!frameObject.getContainer().getComponent(0).isVisible()) { 
                    profileBtn.setBackground(new Color(0x252b2b));
                }
            }
        });
        bottomSection.add(profileBtn);

        // Logout Button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(50, 130, 250, 40);
        logoutBtn.setBackground(new Color(0x2D3436)); 
        logoutBtn.setForeground(new Color(0XFFFFFF));
        logoutBtn.setFont(new Font("Helvetica", Font.BOLD, 14));
        logoutBtn.setBorder(BorderFactory.createLineBorder(new Color(0x636E72), 1));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
        logoutBtn.addActionListener(e -> {
            // 1. Get the current window to use as the parent for the dialog
           Window currentWindow = SwingUtilities.getWindowAncestor(logoutBtn);
    
           // 2. Call your custom Yes/No dialog
           // Casting currentWindow to Frame since showConfirm expects a Frame
           boolean confirm = gui.CustomDialog.showConfirm((Frame)currentWindow, "Are you sure you want to logout?");
    
           if (confirm) {
               // 3. Open the Login window
               new gui.LoginForm().setVisible(true);

              // 4. Close the current Dashboard window
              if (currentWindow != null) {
                  currentWindow.dispose();
               }
           }
        }); 

        // Hover Logic for Logout Button
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setForeground(new Color(0xE74C3C));
                logoutBtn.setBorder(BorderFactory.createLineBorder(new Color(0xE74C3C), 1));
            } 
            public void mouseExited(MouseEvent e) {
                logoutBtn.setForeground(new Color(0xFFFFFF));
                logoutBtn.setBorder(BorderFactory.createLineBorder(new Color(0x636E72), 1));
            }
        });
        
        bottomSection.add(logoutBtn);
        menuPanel.add(bottomSection);
    }

    // Helper method to create styled menu buttons
    private JButton createMenuButton(String text, int yPos, myFrame frameObject, String cardName) {
        JButton btn = new JButton(text);
        btn.setBounds(0, yPos, 350, 50);
        btn.setBackground(Main.DARK_PANEL);
        btn.setForeground(new Color(0xD1D8E0));
        btn.setFont(new Font("Helvetica", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMargin(new Insets(0, 50, 0, 0));

        // Initial Border (Empty so text doesn't jump)
        btn.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

        btn.addActionListener(e -> {
           frameObject.getCardLayout().show(frameObject.getContainer(), cardName);
           activeIndicator.setVisible(true);
           activeIndicator.setLocation(0, yPos + 5);
            resetButtonStyles(frameObject.getMenu());
            
            if (cardName.equals("DASHBOARD")) {
                for (Component comp : frameObject.getContainer().getComponents()) {
                    if (comp instanceof DashboardPanel) {
                        ((DashboardPanel) comp).refreshData(); // Updates text and your new graph
                        break;
                    }
                }
            }

            btn.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
            btn.setForeground(new Color(0x575FCF));
        });
        
        // Hover Effects
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(Main.ACCENT_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                if (!btn.getForeground().equals(new Color(0x575FCF))) {
                    btn.setForeground(new Color(0xD1D8E0));
                }
            }
        });
        return btn;
    }

    private void resetButtonStyles(JPanel menuPanel) {
        for (Component c : menuPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setForeground(new Color(0xD1D8E0));
                // This resets the border to a standard empty one for EVERY button
                btn.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
            }
        }
    }
}
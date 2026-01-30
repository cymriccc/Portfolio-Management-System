package gui;

import java.awt.*;
import javax.swing.*;
import main.Main;

public class myFrame {
    JFrame frame = new JFrame("Student Portfolio System");
    JPanel menu = new JPanel();
    JPanel container = new JPanel();
    CardLayout cardLayout = new CardLayout();

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

        // Content Area (Switcher)
        container.setLayout(cardLayout); 
        container.setBounds(350, 0, 1016, 728);
    }

    JLabel sidebarProfileImg = new JLabel();
    public void updateSidebarProfile(String path) { // method to update sidebar profile image
    ImageIcon icon = new ImageIcon(path);
    Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
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

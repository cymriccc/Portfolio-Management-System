package main;

import gui.LoginForm;
import java.awt.Color;

public class Main {
    public static final Color BG_COLOR = new Color(0xF5F7FA);
    public static final Color ACCENT_COLOR = new Color(0x575FCF);
    public static final Color TEXT_COLOR = new Color(0x2D3436);

    public static void main(String[] args) {
        // Start the application at the Login screen
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}